package cn.yue.test.camera

import android.Manifest
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Looper
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.annotation.NonNull
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import cn.yue.base.common.Constant
import cn.yue.base.common.utils.app.RunTimePermissionUtil.requestPermissions
import cn.yue.base.middle.mvp.components.BaseHintFragment
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestCameraxBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.Executors


/**
 * Description :
 * Created by yue on 2022/1/17
 */

@Route(path = "/app/testCamerax")
class TestCameraXFragment : BaseHintFragment(){
    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_camerax
    }
    
    private lateinit var binding: FragmentTestCameraxBinding
    
    override fun bindLayout(inflated: View) {
        binding = FragmentTestCameraxBinding.bind(inflated)
    }
    
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private val width = 640
    private val height = 360


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.tv.setOnClickListener {
            takePicture()
        }
        mActivity.requestPermissions({
            openCamera()
        }, {}, Manifest.permission.CAMERA)
       binding.tv.setOnClickListener {
           takePicture()
       }
    }


    private fun openCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(mActivity)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            if (binding.texture.isAvailable) {
                bindPreview(cameraProvider)
            } else {
                binding.texture.surfaceTextureListener = surfaceTextureListener
            }
        }, ContextCompat.getMainExecutor(mActivity))
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val preview : Preview = Preview.Builder()
            .setTargetResolution(Size(width, height))
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            // enable the following line if RGBA output is needed.
            // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setTargetResolution(Size(width, height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            // insert your code here.

            // after done, release the ImageProxy object
            imageProxy.close()
        })

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(binding.texture.display.rotation)
            .setTargetResolution(Size(width, height))
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

//        preview.setSurfaceProvider(binding.preview.surfaceProvider)
        preview.setSurfaceProvider(mSurfaceProvider)
        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageCapture, imageAnalysis, preview)

    }

    private fun takePicture() {
        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(File(Constant.imagePath + "pi.jpg"))
            .build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    // insert your code here.

                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // insert your code here.

                }
            })
    }

    private val mSurfaceProvider = object : Preview.SurfaceProvider {
        // This executor must have also been used with Preview.setSurfaceProvider() to
        // ensure onSurfaceRequested() is called on our GL thread.


        override fun onSurfaceRequested(@NonNull request: SurfaceRequest) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                // Post on main thread to ensure thread safety.
                postToMain(request)
                return
            }
            // Create the surface and attempt to provide it to the camera.
            binding.texture.surfaceTexture?.setDefaultBufferSize(request.resolution.width, request.resolution.height)
            val surface = Surface(binding.texture.surfaceTexture)
            request.provideSurface(surface, cameraExecutor) { result: SurfaceRequest.Result? ->
                // In all cases (even errors), we can clean up the state. As an
                // optimization, we could also optionally check for REQUEST_CANCELLED
                // since we may be able to reuse the surface on subsequent surface requests.
                when (result?.resultCode) {
                    SurfaceRequest.Result.RESULT_INVALID_SURFACE -> {

                    }
                    SurfaceRequest.Result.RESULT_REQUEST_CANCELLED -> {

                    }
                    SurfaceRequest.Result.RESULT_SURFACE_ALREADY_PROVIDED -> {

                    }
                    SurfaceRequest.Result.RESULT_SURFACE_USED_SUCCESSFULLY -> {

                    }
                    SurfaceRequest.Result.RESULT_WILL_NOT_PROVIDE_SURFACE -> {

                    }
                }
            }
        }
    }


    private fun postToMain(request: SurfaceRequest) {
        ContextCompat.getMainExecutor(requireContext()).execute {
            mSurfaceProvider.onSurfaceRequested(request)
        }
    }

    private val surfaceTextureListener by lazy {
        object : TextureView.SurfaceTextureListener {

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                surface.release()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

        }
    }

}