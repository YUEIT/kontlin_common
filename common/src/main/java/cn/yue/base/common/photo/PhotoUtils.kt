package cn.yue.base.common.photo

import android.content.Context
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File
import java.util.*

/**
 * Description :
 * Created by yue on 2018/11/19
 */
object PhotoUtils {

    private val IMAGE_URL = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val MIME_TYPE = MediaStore.Images.Media.MIME_TYPE
    private val DATA = MediaStore.Images.Media.DATA
    private val DATE_MODIFIED = MediaStore.Images.Media.DATE_MODIFIED
    /**
     * 获取最近num张照片（jpg, jpeg, png）
     * @param num
     * @return
     */
    @JvmStatic
    fun getTheLastPhotos(context: Context, num: Int): ArrayList<String>? {
        var list: ArrayList<String>? = null

        val contentResolver = context.contentResolver
        if (null != contentResolver) {
            val cursor = contentResolver.query(IMAGE_URL, arrayOf(DATA),
                    "$MIME_TYPE=? or $MIME_TYPE=? or $MIME_TYPE=?",
                    arrayOf("image/jpg", "image/jpeg", "image/png"),
                    "$DATE_MODIFIED DESC limit 0,$num")
            if (null != cursor) {
                list = ArrayList()
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0))
                }
                cursor.close()
            }
        }
        return list
    }

    //获取对应路径下的所有图片
    @JvmStatic
    fun getAllPhotosPathByFolder(path: String): ArrayList<String>? {
        if (!TextUtils.isEmpty(path)) {
            val folder = File(path)
            val files = folder.list()
            val length: Int = files.size
            if (null == files || length < 1) {
                return null
            }
            val imageFilePaths = ArrayList<String>()
            for (i in length - 1 downTo 0) {
                if (isPhotoByName(files[i])) {
                    imageFilePaths.add(path + File.separator + files[i])
                }
            }
            return imageFilePaths
        }
        return null
    }

    //获取所有图片文件（按目录）
    @JvmStatic
    fun getPhotosByPage(context: Context, page: Int, count: Int): List<String> {
        val list = ArrayList<String>()
        val contentResolver = context.contentResolver
        if (null != contentResolver) {
            val cursor = contentResolver.query(IMAGE_URL, arrayOf(DATA),
                    "$MIME_TYPE=? or $MIME_TYPE=? or $MIME_TYPE=?",
                    arrayOf("image/jpg", "image/jpeg", "image/png"),
                    DATE_MODIFIED + " DESC LIMIT " + page * count + " , " + count)
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    val path = cursor.getString(0)
                    if (!TextUtils.isEmpty(path)) {
                        list.add(path)
                    }
                }
                cursor.close()
            }
        }
        return list
    }


    //获取所有图片文件（按目录）
    @JvmStatic
    fun getAllPhotos(context: Context): List<String> {
        val list = ArrayList<String>()
        val contentResolver = context.contentResolver
        if (null != contentResolver) {
            val cursor = contentResolver.query(IMAGE_URL, arrayOf(DATA),
                    "$MIME_TYPE=? or $MIME_TYPE=? or $MIME_TYPE=?",
                    arrayOf("image/jpg", "image/jpeg", "image/png"),
                    DATE_MODIFIED)
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    val path = cursor.getString(0)
                    if (!TextUtils.isEmpty(path)) {
                        list.add(path)
                    }
                }
                cursor.close()
            }
        }
        return list
    }

    //判断文件是否是图片
    private fun isPhotoByName(name: String): Boolean {
        if (!TextUtils.isEmpty(name)) {
            val nameStr = name.toLowerCase()
            return nameStr.endsWith("jpg") || nameStr.endsWith("png") || nameStr.endsWith("jpeg")
        }
        return false
    }

    private fun isPhotoByFile(file: File?): Boolean {
        if (file == null) {
            return false
        }
        val name: String = file.name
        if (!TextUtils.isEmpty(name)) {
            val nameStr = name.toLowerCase()
            return nameStr.endsWith("jpg") || nameStr.endsWith("png") || nameStr.endsWith("jpeg")
        }
        return false
    }

    //获取目录下文件数量
    private fun getImgCount(file: File?): Int {
        var count = 0
        if (null != file) {
            val files = file.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (f in files) {
                    if (null != f) {
                        val name = f.name
                        if (null != name && !name.isEmpty()) {
                            val nameStr = name.toLowerCase()
                            if (nameStr.endsWith(".jpg") || nameStr.endsWith(".jpeg") || nameStr.endsWith(".png")) {
                                count++
                            }
                        }
                    }
                }
            }
        }
        return count
    }

    //获取目录下的第一张图片的路径
    private fun getFirstPhotoPath(file: File?): String? {
        if (null != file) {
            val files = file.listFiles()
            var start: Int = files.size
            if (null != files && start > 0) {
                start--
                for (i in start downTo -1) {
                    val f = files[i]
                    if (isPhotoByFile(f)) {
                        return f.absolutePath
                    }
                }
            }
        }
        return null
    }
}
