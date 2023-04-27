package cn.yue.base.middle.mvp.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import cn.yue.base.common.activity.BaseFragmentActivity
import cn.yue.base.common.photo.data.MediaData
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.utils.file.AndroidQFileUtils
import cn.yue.base.common.utils.file.BitmapFileUtils
import cn.yue.base.middle.R
import cn.yue.base.middle.router.FRouter
import java.io.File

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class PhotoHelper(val context: BaseFragmentActivity, private val iPhotoView: IPhotoView) {

    private var maxNum: Int = 1

    fun setMaxNum(maxNum: Int): PhotoHelper {
        this.maxNum = maxNum
        return this
    }

    private var noHintText: Boolean = false

    fun setNoHintText(noHintText: Boolean): PhotoHelper {
        this.noHintText = noHintText
        return this
    }
    
    fun openSystemAlbum() {
        val selectBlock = {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            context.startActivityForResult(intent, requestSelectSystemPhoto)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RunTimePermissionUtil.requestPermissions(context, {
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            RunTimePermissionUtil.requestPermissions(context, {
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun openAlbum() {
        val selectBlock = {
            FRouter.instance.build("/common/selectPhoto")
                .withParcelableArrayList("medias", selectCache as ArrayList<MediaData>)
                .withInt("maxNum", maxNum)
                .navigation(context, requestSelectPhoto)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RunTimePermissionUtil.requestPermissions(context, {
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            RunTimePermissionUtil.requestPermissions(context, {
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
            FRouter.instance.build("/common/selectPhoto")
                .withParcelableArrayList("medias", selectCache as ArrayList<MediaData>)
                .withInt("maxNum", maxNum)
                .navigation(context, requestSelectPhoto)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RunTimePermissionUtil.requestPermissions(context, {
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            RunTimePermissionUtil.requestPermissions(context, {
                selectBlock.invoke()
            }, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun openCamera() {
        RunTimePermissionUtil.requestPermissions(context, {
            val tempFile = BitmapFileUtils.createRandomFile()
            targetUri = Uri.fromFile(tempFile)
            if (targetUri != null) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
                context.startActivityForResult(intent, requestCamera)
            }
            cachePhotoUri = targetUri
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

    fun cropPhoto(autoCrop: Boolean, aspectX: Int, aspectY: Int) {
        if (cachePhotoUri == null) {
            ToastUtils.showShortToast(R.string.app_no_crop_picture.getString())
            return
        }
        val intent = Intent("com.android.camera.action.CROP")
        val path = AndroidQFileUtils.getPathFromUri(context, cachePhotoUri!!)
        if (path.isNullOrEmpty()) {
            ToastUtils.showShortToast(R.string.app_no_crop_picture.getString())
            return
        }
        val targetFile = File(path)
        val tempFile = BitmapFileUtils.createRandomFile()
        val outPutUri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            targetUri = FileProvider.getUriForFile(context,
                "cn.yue.base.kotlin.test.fileprovider", targetFile)
            outPutUri = FileProvider.getUriForFile(context,
                "cn.yue.base.kotlin.test.fileprovider", tempFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else { //7.0以下
            targetUri = Uri.fromFile(targetFile)
            outPutUri = Uri.fromFile(tempFile)
        }
        cachePhotoUri = outPutUri
        intent.setDataAndType(targetUri, "image/*")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
        intent.putExtra("crop", "true")//可裁剪
        if (!autoCrop) {
            intent.putExtra("aspectX", aspectX)
            intent.putExtra("aspectY", aspectY)
        }
        intent.putExtra("scale", false)
        //intent.putExtra("return-data", false);//若为false则表示不返回数据
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
        context.startActivityForResult(intent, requestCropPhoto)
    }

    private var targetUri: Uri? = null
    private var cachePhotoUri: Uri? = null
    private var selectCache: MutableList<MediaData> = ArrayList()
    private val requestSelectPhoto = 10001
    private val requestCamera = 10002
    private val requestCropPhoto = 10003
    private val requestSelectSystemPhoto = 10004

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            requestSelectPhoto -> {
                if (data != null) {
                    val selectList = data.getParcelableArrayListExtra<MediaData>("medias")
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
            requestCamera -> {
                val temp: MutableList<MediaData> = ArrayList()
                val mediaData = MediaData().apply {
                    uri = cachePhotoUri
                }
                temp.add(mediaData)
                iPhotoView.selectImageResult(temp)
            }
            requestCropPhoto -> {
                iPhotoView.cropImageResult(cachePhotoUri!!)
            }
            requestSelectSystemPhoto -> {
                data?.let {
                    cachePhotoUri = data.data
                    val temp: MutableList<MediaData> = ArrayList()
                    val mediaData = MediaData().apply {
                        uri = cachePhotoUri
                    }
                    temp.add(mediaData)
                    iPhotoView.selectImageResult(temp)
                }
            }
        }
    }
}