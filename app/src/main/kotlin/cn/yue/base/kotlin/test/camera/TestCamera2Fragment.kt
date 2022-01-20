package cn.yue.base.kotlin.test.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.util.Range
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.databinding.FragmentTestCamera2Binding
import cn.yue.base.middle.components.binding.BaseHintBindFragment
import com.alibaba.android.arouter.facade.annotation.Route
import java.util.*
import kotlin.collections.ArrayList


/**
 * Description :
 * Created by yue on 2022/1/17
 */
@Route(path = "/app/testCamera2")
class TestCamera2Fragment : BaseHintBindFragment<FragmentTestCamera2Binding>() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_camera2
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.tv.setOnClickListener {
            takePicture()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        if (cameraDevice != null) {
            stopCamera()
        }
    }

    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0
    private var cameraDevice: CameraDevice? = null
    private var cameraId = "0"
    private var mCaptureRequestBuilder: CaptureRequest.Builder? = null
    private var mCaptureRequest: CaptureRequest? = null
    private var mPreviewSession: CameraCaptureSession? = null
    private var previewSize: Size? = null
    private var imageReader: ImageReader? = null
    private val orientations = SparseIntArray()
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private var fps: Range<Int> = Range(0, 30)

    init {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCamera() {
        if (binding.texture.isAvailable) {
            if (cameraDevice == null) {
                openCamera()
            }
        } else {
            binding.texture.surfaceTextureListener = surfaceTextureListener
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun openCamera() {
        val cameraManager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            //设置摄像头特性
            setCameraCharacteristics(cameraManager)
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                val perms = arrayOf(Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(mActivity, perms, 100)
                mActivity.requestPermission(arrayOf(Manifest.permission.CAMERA), {
                    cameraManager.openCamera(cameraId, stateCallback, null)
                }, {
                    ToastUtils.showLongToast("授权失败")
                })
            } else {
                cameraManager.openCamera(cameraId, stateCallback, null)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setCameraCharacteristics(manager: CameraManager) {
        try {
            // 获取指定摄像头的特性
            val characteristics: CameraCharacteristics = manager.getCameraCharacteristics(cameraId)
            // 获取摄像头支持的配置属性
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            // 获取摄像头支持的最大尺寸
            val largest: Size = Collections.max(
                map!!.getOutputSizes(ImageFormat.JPEG).asList(), CompareSizesByArea())
            // 创建一个ImageReader对象，用于获取摄像头的图像数据
            imageReader = ImageReader.newInstance(
                largest.width, largest.height,
                ImageFormat.JPEG, 2
            )
            //设置获取图片的监听
            imageReader!!.setOnImageAvailableListener(imageAvailableListener, null)
            // 获取最佳的预览尺寸
            previewSize = chooseOptimalSize(
                map.getOutputSizes(
                    SurfaceTexture::class.java
                ), surfaceWidth, surfaceHeight, largest
            )
            //fps帧率
            val fpsRange = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)
            if (fpsRange.isNullOrEmpty()) {
                return
            }
            var selectRang: Range<Int> = Range(0, 0)
            for (rang in fpsRange) {
                if (rang.upper > selectRang.upper && rang.upper <= 30) {
                    selectRang = rang
                } else if (rang.upper == selectRang.upper && rang.lower < selectRang.lower) {
                    selectRang = rang
                }
            }
            if (selectRang.lower >= 0 && selectRang.upper > 0) {
                fps = selectRang
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
        }
    }

    /**
     * 为Size定义一个比较器Comparator
     */
    internal class CompareSizesByArea : Comparator<Size> {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun compare(lhs: Size, rhs: Size): Int {
            return java.lang.Long.signum(
                lhs.width.toLong() * lhs.height -
                        rhs.width.toLong() * rhs.height
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun chooseOptimalSize(choices: Array<Size>, width: Int, height: Int, aspectRatio: Size): Size {
        // 收集摄像头支持的大过预览Surface的分辨率
        val bigEnough: MutableList<Size> = ArrayList()
        val w: Int = aspectRatio.width
        val h: Int = aspectRatio.height
        for (option in choices) {
            if (option.height == option.width * h / w && option.width >= width && option.height >= height) {
                bigEnough.add(option)
            }
        }
        return if (bigEnough.size > 0) {
            Collections.min(bigEnough, CompareSizesByArea())
        } else {
            //没有合适的预览尺寸
            choices[0]
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun takePreview() {
        val mSurfaceTexture: SurfaceTexture? = binding.texture.surfaceTexture
        //设置TextureView的缓冲区大小
        mSurfaceTexture?.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
        //获取Surface显示预览数据
        val mSurface = Surface(mSurfaceTexture)
        try {
            //创建预览请求
            mCaptureRequestBuilder =
                cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // 设置自动对焦模式
            mCaptureRequestBuilder?.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            //设置Surface作为预览数据的显示界面
            mCaptureRequestBuilder?.addTarget(mSurface)
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，
            // 当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCaptureRequestBuilder?.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps)
            cameraDevice?.createCaptureSession(
                listOf(mSurface, imageReader!!.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        try {
                            //开始预览
                            mCaptureRequest = mCaptureRequestBuilder?.build()
                            mPreviewSession = session
                            //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                            mPreviewSession?.setRepeatingRequest(mCaptureRequest!!, null, null)
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {}
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun takePicture() {
        try {
            if (cameraDevice == null) {
                return
            }
            // 创建拍照请求
            mCaptureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            // 设置自动对焦模式
            mCaptureRequestBuilder?.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            // 将imageReader的surface设为目标
            mCaptureRequestBuilder?.addTarget(imageReader!!.surface)
            // 获取设备方向
            val rotation: Int = mActivity.windowManager.getDefaultDisplay().getRotation()
            // 根据设备方向计算设置照片的方向
            mCaptureRequestBuilder?.set(
                CaptureRequest.JPEG_ORIENTATION, orientations.get(rotation)
            )
            // 停止连续取景
            mPreviewSession!!.stopRepeating()
            //拍照
            val captureRequest: CaptureRequest = mCaptureRequestBuilder!!.build()
            //设置拍照监听
            mPreviewSession!!.capture(captureRequest, captureCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun stopCamera() {
        if (cameraDevice != null) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    private val surfaceTextureListener by lazy {
        object : TextureView.SurfaceTextureListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                surfaceWidth = width
                surfaceHeight = height
                openCamera()
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

    private val stateCallback by lazy {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                takePreview()
            }

            override fun onDisconnected(camera: CameraDevice) {
                stopCamera()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                stopCamera()
            }

        }
    }

    private val captureCallback by lazy {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        object : CameraCaptureSession.CaptureCallback() {

            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)
                // 重设自动对焦模式
                mCaptureRequestBuilder?.set(
                    CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
                )
                // 设置自动曝光模式
                mCaptureRequestBuilder?.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                )
                try {
                    //重新进行预览
                    mPreviewSession!!.setRepeatingRequest(mCaptureRequest!!, null, null)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }

            }
        }
    }

    private val imageAvailableListener by lazy {
        ImageReader.OnImageAvailableListener {

        }
    }
}