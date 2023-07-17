package cn.yue.base.mvp.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import cn.yue.base.common.R
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.photo.SelectPhotoActivity
import cn.yue.base.photo.data.MediaData
import cn.yue.base.utils.app.RunTimePermissionUtil.requestPermissions
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.utils.file.AndroidQFileUtils
import cn.yue.base.utils.file.BitmapFileUtils
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.DisposableSingleObserver

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class PhotoHelper(val context: BaseFragmentActivity, private val iPhotoView: IPhotoView) {
    
    private var targetUri: Uri? = null
    private var cachePhotoUri: Uri? = null
    private var selectCache: MutableList<MediaData> = ArrayList()
    private var maxNum: Int = 1

    fun setMaxNum(maxNum: Int): PhotoHelper {
        this.maxNum = maxNum
        return this
    }
    
    private val selectSystemPhotoLauncher = context.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data?.let { intent ->
                cachePhotoUri = intent.data
                val temp: MutableList<MediaData> = ArrayList()
                val mediaData = MediaData().apply {
                    uri = cachePhotoUri
                }
                temp.add(mediaData)
                iPhotoView.selectImageResult(temp)
            }
        }
    }
    
    fun openSystemAlbum() {
        val selectBlock = {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectSystemPhotoLauncher.launch(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.requestPermissions({
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            context.requestPermissions({
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    
    private val selectPhotoLauncher = context.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val selectList = it.data?.getParcelableArrayListExtra<MediaData>("medias")
            if (selectList != null) {
                selectCache.clear()
                selectCache.addAll(selectList)
                if (selectList.size == 1) {
                    cachePhotoUri = selectList[0].uri
                }
                iPhotoView.selectImageResult(selectList)
            }
        }
    }

    fun openAlbum() {
        val selectBlock = {
            val intent = Intent(context, SelectPhotoActivity::class.java)
            intent.putParcelableArrayListExtra("medias", selectCache as ArrayList<MediaData>)
            intent.putExtra("maxNum", maxNum)
            selectPhotoLauncher.launch(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.requestPermissions({
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            context.requestPermissions({
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun openAlbum(selectList: List<MediaData>?) {
        selectCache.clear()
        if (selectList != null && selectList.isNotEmpty()) {
            selectCache.addAll(selectList)
        }
        val selectBlock = {
            val intent = Intent(context, SelectPhotoActivity::class.java)
            intent.putParcelableArrayListExtra("medias", selectCache as ArrayList<MediaData>)
            intent.putExtra("maxNum", maxNum)
            selectPhotoLauncher.launch(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.requestPermissions({
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            context.requestPermissions({
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    
    private val cameraLauncher = context.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val temp: MutableList<MediaData> = ArrayList()
            val mediaData = MediaData().apply {
                uri = cachePhotoUri
            }
            temp.add(mediaData)
            iPhotoView.selectImageResult(temp)
        }
    }

    fun openCamera() {
        context.requestPermissions({
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val tempFile = BitmapFileUtils.createRandomFile()
            targetUri = AndroidQFileUtils.getUriForFile(tempFile)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            if (targetUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
                cameraLauncher.launch(intent)
            }
            cachePhotoUri = Uri.fromFile(tempFile)
        }, {}, Manifest.permission.CAMERA)
    }

    fun autoCropPhoto() {
        cropPhoto(true, 0, 0)
    }

    fun cropPhoto() {
        cropPhoto(false, 1, 1)
    }

    fun uploadPhoto() {
        uploadPhoto(cachePhotoUri!!)
    }

    fun uploadPhoto(name: Uri) {
        val list: MutableList<Uri> = ArrayList()
        list.add(name)
        uploadPhoto(list)
    }

    fun uploadPhoto(imageList: List<Uri>) {

    }
    
    private val cropPhotoLauncher = context.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            iPhotoView.cropImageResult(cachePhotoUri!!)
        }
    }

    fun cropPhoto(autoCrop: Boolean, aspectX: Int, aspectY: Int) {
        if (cachePhotoUri == null) {
            ToastUtils.showShortToast(R.string.app_no_crop_picture.getString())
            return
        }
        val intent = Intent("com.android.camera.action.CROP")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        runOnWork({
            AndroidQFileUtils.createNewFile(context, cachePhotoUri!!, ".jpg")
        }, { targetFile ->
            val tempFile = BitmapFileUtils.createRandomFile()
            targetUri = AndroidQFileUtils.getUriForFile(targetFile)
            val outPutUri= AndroidQFileUtils.getUriForFile(tempFile)
            cachePhotoUri = outPutUri
            intent.setDataAndType(targetUri, "image/*")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
            intent.putExtra("crop", "true")//可裁剪
            if (!autoCrop) {
                intent.putExtra("aspectX", aspectX)
                intent.putExtra("aspectY", aspectY)
            }
            intent.putExtra("scale", false)
            intent.putExtra("return-data", false);//若为false则表示不返回数据
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("noFaceDetection", true)
            //将存储图片的uri读写权限授权给剪裁工具应用
            val resInfoList: List<ResolveInfo> = context.packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    outPutUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            cropPhotoLauncher.launch(intent)
        })
    }
    
    private fun <T> runOnWork(work: () -> T, success: (t: T) -> Unit) {
        Single.fromCallable {
            work.invoke()
        }.compose(context.getLifecycleProvider()!!.toBindLifecycle())
            .subscribe(object : DisposableSingleObserver<T>() {
                override fun onSuccess(t: T) {
                    success.invoke(t)
                }
    
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }
}