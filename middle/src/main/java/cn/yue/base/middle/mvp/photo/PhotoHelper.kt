package cn.yue.base.middle.mvp.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import cn.yue.base.common.activity.BaseFragmentActivity
import cn.yue.base.common.activity.FRouter
import cn.yue.base.common.activity.PermissionCallBack
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.utils.file.BitmapFileUtils
import java.io.File

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class PhotoHelper(val context: BaseFragmentActivity, val iPhotoView: IPhotoView) {

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

    fun toAlbum() {
        RunTimePermissionUtil.requestPermissions(context, object : PermissionCallBack {
            override fun requestSuccess(permission: String) {
                FRouter.instance.build("/common/selectPhoto")
                        .withStringArrayList("photos", selectCache as ArrayList<String>)
                        .withInt("maxNum", maxNum)
                        .navigation(context, REQUEST_SELECT_PHOTO)
            }

            override fun requestFailed(permission: String) {

            }

        }, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun toAlbum(selectList: List<String>?) {
        selectCache.clear()
        if (selectList != null && selectList.isNotEmpty()) {
            selectCache.addAll(selectList)
        }
        RunTimePermissionUtil.requestPermissions(context, object : PermissionCallBack {
            override fun requestSuccess(permission: String) {
                FRouter.instance.build("/common/selectPhoto")
                        .withStringArrayList("photos", selectCache as ArrayList<String>)
                        .withInt("maxNum", maxNum)
                        .navigation(context, REQUEST_SELECT_PHOTO)
            }

            override fun requestFailed(permission: String) {

            }

        }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun toCamera() {
        RunTimePermissionUtil.requestPermissions(context, object : PermissionCallBack {

            override fun requestSuccess(permission: String) {
                val tempFile = BitmapFileUtils.createRandomFile()
                cachePhotoPath = tempFile.absolutePath
                targetUri = Uri.fromFile(tempFile)
                if (targetUri != null) {
                    val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
                    context.startActivityForResult(intent, REQUEST_CAMERA)
                }
            }

            override fun requestFailed(permission: String) {
            }

        }, Manifest.permission.CAMERA)
    }

    fun toAutoCrop() {
        toCrop(true, 0, 0)
    }

    fun toCrop() {
        toCrop(false, 1, 1)
    }

    fun upload() {
        upload(cachePhotoPath!!)
    }

    fun upload(name: String) {
        val list: MutableList<String> = ArrayList()
        list.add(name)
        upload(list)
    }

    fun upload(imageList: List<String>) {

    }

    fun toCrop(autoCrop: Boolean, aspectX: Int, aspectY: Int) {
        val outPutUri: Uri
        if (cachePhotoPath != null) {
            targetUri = Uri.fromFile(File(cachePhotoPath))
            val tempFile = BitmapFileUtils.createRandomFile()
            cachePhotoPath = tempFile.getAbsolutePath()
            outPutUri = Uri.fromFile(tempFile)
        } else {
            ToastUtils.showShortToast("没有裁剪的图片~")
            return
        }
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(targetUri, "image/*")
        intent.putExtra("crop", "true")//可裁剪
        if (!autoCrop) {
            intent.putExtra("aspectX", aspectX)
            intent.putExtra("aspectY", aspectY)
        }
        intent.putExtra("scale", false)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
        //intent.putExtra("return-data", false);//若为false则表示不返回数据
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        context.startActivityForResult(intent, REQUEST_CROP_PHOTO)
    }

    private var targetUri: Uri? = null
    private var cachePhotoPath: String? = null
    private var selectCache: MutableList<String> = ArrayList()
    private val REQUEST_SELECT_PHOTO: Int = 10001
    private val REQUEST_CAMERA: Int = 10002
    private val REQUEST_CROP_PHOTO = 10003

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_SELECT_PHOTO ->
                if (data != null) {
                    val selectList: MutableList<String> = data.getStringArrayListExtra("photos")
                    selectCache.clear()
                    selectCache.addAll(selectList)
                    if (selectList.size == 1) {
                        cachePhotoPath = selectList[0]
                    }
                    iPhotoView.selectImageResult(selectList)
                }
            REQUEST_CROP_PHOTO ->
                iPhotoView.cropImageResult(cachePhotoPath!!)
            REQUEST_CAMERA -> {
                    val temp: MutableList<String> = ArrayList()
                    temp.add(cachePhotoPath!!)
                    iPhotoView.selectImageResult(temp)
                }

        }
    }
}