package cn.yue.test.camera

import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.view.Surface
import android.view.TextureView
import android.view.View
import cn.yue.base.common.Constant
import cn.yue.base.middle.mvp.components.BaseHintFragment
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestCamera2Binding
import com.alibaba.android.arouter.facade.annotation.Route
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Description :
 * Created by yue on 2022/1/20
 */
@Route(path = "/app/testCamera")
class TestCameraFragment : BaseHintFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_camera2
    }
    
    private lateinit var binding: FragmentTestCamera2Binding
    
    override fun bindLayout(inflated: View) {
        binding = FragmentTestCamera2Binding.bind(inflated)
    }
    
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if (binding.texture.isAvailable) {
            mInputSurfaceTexture = binding.texture.surfaceTexture
        } else {
            binding.texture.surfaceTextureListener = surfaceTextureListener
        }
        binding.tv.setOnClickListener {
            takePicture()
        }
        val mThread = HandlerThread("camera-cap")
        mThread.start()
        // 创建camera异步消息处理handler
        cameraThreadHandler = Handler(mThread.looper)
    }

    override fun onResume() {
        super.onResume()
        if (binding.texture.isAvailable) {
            startPreview()
        }
    }

    override fun onPause() {
        super.onPause()
        stopPreview()
    }

    private val CAMERA_STOP_TIMEOUT_MS = 7000

    private var mCam: Camera? = null
    private var mCamInfo: Camera.CameraInfo? = null
    private val isCameraRunning = AtomicBoolean()
    private var mInputSurfaceTexture: SurfaceTexture? = null
    private val pendingCameraRestartLock = Any()
    @Volatile
    private var pendingCameraRestart = false
    private var cameraThreadHandler: Handler? = null

    // camera相关参数的初始值
    private var mFront = 0
    private var mCameraWidth = 640
    private var mCameraHeight = 480
    private var mFrameRate = 15
    private var mImageRotation = 0

    private var mIsPreview = false
    private var mIsCapture = false

    // 启动camera
    private fun startCamera(): Int {
        if (isCameraRunning.getAndSet(true)) {
            return 0
        }
        maybePostOnCameraThread(Runnable {
            // * Create and Start Cam
            createCamOnCameraThread()
            startCamOnCameraThread()
        })
        return 0
    }

    // 关闭camera
    private fun stopCamera(): Int {
        if (mIsPreview || mIsCapture) {
            return 0
        }
        val barrier = CountDownLatch(1)
        val didPost: Boolean = maybePostOnCameraThread(Runnable {
            stopCaptureOnCameraThread(true /* stopHandler */)
            releaseCam()
            barrier.countDown()
        })
        if (!didPost) {
            return 0
        }
        try {
            barrier.await(CAMERA_STOP_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return 0
    }

    // 启动预览
    private fun startPreview(): Int {
        mIsPreview = true
        return startCamera()
    }

    // 停止预览
    private fun stopPreview(): Int {
        mIsPreview = false
        return stopCamera()
    }

    private fun checkIsOnCameraThread() {
        if (cameraThreadHandler == null) {
        } else check(!(Thread.currentThread() != cameraThreadHandler?.looper?.thread)) {
            "Wrong thread"
        }
    }

    private fun maybePostOnCameraThread(runnable: Runnable): Boolean {
        return (cameraThreadHandler != null && isCameraRunning.get()
                && cameraThreadHandler!!.postAtTime(runnable, this, SystemClock.uptimeMillis()))
    }

    // 创建camera
    private fun createCamOnCameraThread(): Int {
        checkIsOnCameraThread()
        if (!isCameraRunning.get()) {
            return 0
        }
        // 获取欲设置camera的索引号
        val nFacing = if (mFront != 0) {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        } else {
            Camera.CameraInfo.CAMERA_FACING_BACK
        }
        if (mCam != null) {
            // 已打开camera
            return 0
        }
        mCamInfo = Camera.CameraInfo()
        // 获取camera的数目
        val nCnt = Camera.getNumberOfCameras()
        // 得到欲设置camera的索引号并打开camera
        for (i in 0 until nCnt) {
            Camera.getCameraInfo(i, mCamInfo)
            if (mCamInfo!!.facing == nFacing) {
                mCam = try {
                    Camera.open(i)
                } catch (e: RuntimeException) {
                    null
                }
                break
            }
        }
        // 没找到欲设置的camera
        if (mCam == null) {
            // 先试图打开默认camera
            mCam = Camera.open()
            if (mCam == null) {
                return -1
            }
        }
        // *
        // * Now set preview size
        // *
        val params = mCam!!.parameters
        // 获取camera首选的size
//        var psz = params.preferredPreviewSizeForVideo
        var psz = findProperSize(Point(binding.texture.width, binding.texture.height), params.supportedPreviewSizes)
        if (psz == null) {
            psz = mCam!!.Size(640, 480)
        }

        // 设置camera的采集视图size
        params.setPreviewSize(psz.width, psz.height)
        mCameraWidth = psz.width
        mCameraHeight = psz.height

        // 获取camera支持的帧率范围，并设置预览帧率范围，得到camera的实际帧率
        mFrameRate = chooseFixedPreviewFps(params, mFrameRate)

        // 设置camera的对焦模式
        chooseFocusModes(params)
        try {
            // 设置camera的参数
            mCam!!.parameters = params
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        val actualParam = mCam!!.parameters
        mCameraWidth = actualParam.previewSize.width
        mCameraHeight = actualParam.previewSize.height
        // 设置预览图像的转方向
        val result = calculateCameraPreviewOrientation(mCamInfo!!)
        mCam!!.setDisplayOrientation(result)
        mImageRotation = result
        return 0
    }

    // 启动camera
    private fun startCamOnCameraThread(): Int {
        checkIsOnCameraThread()
        if (!isCameraRunning.get() || mCam == null) {
            return 0
        }

        // * mCam.setDisplayOrientation(90);
        if (mInputSurfaceTexture == null) {
            return -1
        }
        try {
            // 设置预览SurfaceTexture
            mCam?.setPreviewTexture(mInputSurfaceTexture)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // 启动camera预览
        mCam?.startPreview()
        return 0
    }

    private fun takePicture() {
        mCam?.takePicture(null, null) { data, camera ->
            val storePath = Constant.imagePath
            val appDir = File(storePath)
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val file = File(appDir, fileName)
            try {
                val fos = FileOutputStream(file)
                fos.write(data)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // camera停止采集
    private fun stopCaptureOnCameraThread(stopHandler: Boolean): Int {
        checkIsOnCameraThread()
        if (stopHandler) {
            // Clear the cameraThreadHandler first, in case stopPreview or
            // other driver code deadlocks. Deadlock in
            // android.hardware.Camera._stopPreview(Native Method) has
            // been observed on Nexus 5 (hammerhead), OS version LMY48I.
            // The camera might post another one or two preview frames
            // before stopped, so we have to check |isCameraRunning|.
            // Remove all pending Runnables posted from |this|.
            isCameraRunning.set(false)
            cameraThreadHandler?.removeCallbacksAndMessages(this /* token */)
        }
        if (mCam != null) {
            // 停止camera预览
            mCam?.stopPreview()
        }
        return 0
    }

    // 重启camera
    private fun restartCam(): Int {
        synchronized(pendingCameraRestartLock) {
            if (pendingCameraRestart) {
                // Do not handle multiple camera switch request to avoid blocking
                // camera thread by handling too many switch request from a queue.
                return 0
            }
            pendingCameraRestart = true
        }
        val didPost: Boolean = maybePostOnCameraThread(Runnable {
            stopCaptureOnCameraThread(false)
            releaseCam()
            createCamOnCameraThread()
            startCamOnCameraThread()
            synchronized(pendingCameraRestartLock) { pendingCameraRestart = false }
        })
        if (!didPost) {
            synchronized(pendingCameraRestartLock) { pendingCameraRestart = false }
        }
        return 0
    }

    // 释放camera
    private fun releaseCam(): Int {
        // * release cam
        if (mCam != null) {
            mCam?.release()
            mCam = null
        }

        // * release cam info
        mCamInfo = null
        return 0
    }

    private val surfaceTextureListener by lazy {
        object : TextureView.SurfaceTextureListener {

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mInputSurfaceTexture = surface
                startPreview()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                stopCamera()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

        }
    }

    private fun calculateCameraPreviewOrientation(info: Camera.CameraInfo): Int {
        val rotation = mActivity.windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    /**
     * 找出最合适的尺寸，规则如下：
     * 1.将尺寸按比例分组，找出比例最接近屏幕比例的尺寸组
     * 2.在比例最接近的尺寸组中找出最接近屏幕尺寸且大于屏幕尺寸的尺寸
     * 3.如果没有找到，则忽略2中第二个条件再找一遍，应该是最合适的尺寸了
     */
    private fun findProperSize(surfaceSize: Point, sizeList: List<Camera.Size>?): Camera.Size? {
        if (surfaceSize.x <= 0 || surfaceSize.y <= 0 || sizeList == null) {
            return null
        }
        val surfaceWidth: Int = surfaceSize.x
        val surfaceHeight: Int = surfaceSize.y
        val ratioListList: MutableList<MutableList<Camera.Size>> = ArrayList()
        for (size in sizeList) {
            addRatioList(ratioListList, size)
        }
        val surfaceRatio = surfaceWidth.toFloat() / surfaceHeight
        var bestRatioList: List<Camera.Size>? = null
        var ratioDiff = Float.MAX_VALUE
        for (ratioList in ratioListList) {
            val ratio = ratioList[0].width.toFloat() / ratioList[0].height
            val newRatioDiff = Math.abs(ratio - surfaceRatio)
            if (newRatioDiff < ratioDiff) {
                bestRatioList = ratioList
                ratioDiff = newRatioDiff
            }
        }
        var bestSize: Camera.Size? = null
        var diff = Int.MAX_VALUE
        assert(bestRatioList != null)
        for (size in bestRatioList!!) {
            val newDiff =
                Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight)
            if (size.height >= surfaceHeight && newDiff < diff) {
                bestSize = size
                diff = newDiff
            }
        }
        if (bestSize != null) {
            return bestSize
        }
        diff = Int.MAX_VALUE
        for (size in bestRatioList) {
            val newDiff =
                Math.abs(size.width - surfaceWidth) + Math.abs(size.height - surfaceHeight)
            if (newDiff < diff) {
                bestSize = size
                diff = newDiff
            }
        }
        return bestSize
    }

    private fun addRatioList(
        ratioListList: MutableList<MutableList<Camera.Size>>,
        size: Camera.Size
    ) {
        val ratio = size.width.toFloat() / size.height
        for (ratioList in ratioListList) {
            val mine = ratioList[0].width.toFloat() / ratioList[0].height
            if (ratio == mine) {
                ratioList.add(size)
                return
            }
        }
        val ratioList: MutableList<Camera.Size> = ArrayList()
        ratioList.add(size)
        ratioListList.add(ratioList)
    }

    /**
     * 选择合适的FPS
     * @param params
     * @param fps 期望的FPS
     * @return
     */
    private fun chooseFixedPreviewFps(params: Camera.Parameters, fps: Int): Int {
        val supportedFps = params.supportedPreviewFpsRange
        for (entry in supportedFps) {
            if (entry[0] == entry[1] && entry[0] == fps) {
                params.setPreviewFpsRange(entry[0], entry[1])
            }
        }
        val realRate = IntArray(2)
        params.getPreviewFpsRange(realRate)
        return if (realRate[0] == realRate[1]) {
            realRate[0] / 1000
        } else {
            realRate[1] / 2 / 1000
        }
    }

    /**
     * 设置camera的对焦模式
     */
    private fun chooseFocusModes(params: Camera.Parameters) {
        for (mode in params.supportedFocusModes) {
            if (mode.compareTo(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) == 0) {
                try {
                    params.focusMode = mode
                    break
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

}