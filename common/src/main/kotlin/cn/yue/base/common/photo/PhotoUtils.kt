package cn.yue.base.common.photo

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import cn.yue.base.common.photo.data.MediaFolderVO
import cn.yue.base.common.photo.data.MediaType
import cn.yue.base.common.photo.data.MediaVO
import cn.yue.base.common.photo.data.MimeType.Companion.isImage
import cn.yue.base.common.photo.data.MimeType.Companion.isVideo
import cn.yue.base.common.photo.loader.MediaFolderLoader.COLUMN_BUCKET_DISPLAY_NAME
import cn.yue.base.common.photo.loader.MediaFolderLoader.COLUMN_BUCKET_ID
import cn.yue.base.common.photo.loader.MediaFolderLoader.COLUMN_COUNT
import cn.yue.base.common.photo.loader.MediaFolderLoader.COLUMN_DATA
import cn.yue.base.common.photo.loader.MediaFolderLoader.COLUMN_URI
import cn.yue.base.common.photo.loader.MediaFolderLoader.load
import cn.yue.base.common.photo.loader.MediaLoader.load
import java.util.*

/**
 * Description :
 * Created by yue on 2019/3/11
 */
object PhotoUtils {
    /**
     * 获取所有视频和图片集合
     * @param context
     * @return
     */
    fun getAllMedia(context: Context?): List<MediaVO> {
        return getMediaByFolder(context, true, null, MediaType.ALL)
    }

    /**
     * 获取所有图片文件
     * @param context
     * @return
     */
    fun getAllMediaPhotos(context: Context?): List<MediaVO> {
        return getMediaByFolder(context, true, null, MediaType.PHOTO)
    }

    /**
     * 获取所有视频
     * @param context
     * @return
     */
    fun getAllMediaVideos(context: Context?): List<MediaVO> {
        return getMediaByFolder(context, true, null, MediaType.VIDEO)
    }

    /**
     * 获取最近num张照片
     * @param num
     * @return
     */
    @JvmStatic
    fun getTheLastPhotos(context: Context?, num: Int): ArrayList<MediaVO> {
        val list = ArrayList<MediaVO>()
        list.addAll(getMediaByFolder(context, true, null, MediaType.PHOTO).subList(0, num))
        return list
    }

    /**
     * 获取对应路径下的所有图片
     * @param context
     * @param isAll
     * @param folderId
     * @return
     */
    @JvmStatic
    fun getPhotosByFolder(context: Context?, isAll: Boolean, folderId: String?): ArrayList<MediaVO> {
        return getMediaByFolder(context, isAll, folderId, MediaType.PHOTO)
    }

    /**
     * 获取对应路径下的所有资源
     * @param context
     * @param isAll
     * @param folderId
     * @param mediaType
     * @return
     */
    fun getMediaByFolder(context: Context?, isAll: Boolean, folderId: String?, mediaType: MediaType?): ArrayList<MediaVO> {
        val list = ArrayList<MediaVO>()
        val cursor = load(context!!, isAll, folderId!!, mediaType!!)
        while (cursor.moveToNext()) {
            val mediaVO = MediaVO()
            val id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            mediaVO.id = id
            val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
            mediaVO.mimeType = mimeType
            mediaVO.size = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE).toLong()
            mediaVO.duration = cursor.getLong(cursor.getColumnIndex("duration"))
            var contentUri: Uri?
            contentUri = if (isImage(mimeType)) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if (isVideo(mimeType)) {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else {
                // ?
                MediaStore.Files.getContentUri("external")
            }
            mediaVO.uri = ContentUris.withAppendedId(contentUri, id.toLong())
            val data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
            mediaVO.url = data
            list.add(mediaVO)
        }
        return list
    }

    /**
     * 获取照片文件夹
     * @param context
     * @return
     */
    @JvmStatic
    fun getAllPhotosFolder(context: Context?): MutableList<MediaFolderVO> {
        return getAllMediaFolder(context, MediaType.PHOTO)
    }

    /**
     * 获取资源文件夹
     * @param context
     * @return
     */
    fun getAllMediaFolder(context: Context?, mediaType: MediaType?): MutableList<MediaFolderVO> {
        val list: MutableList<MediaFolderVO> = ArrayList()
        val cursor = load(context!!, mediaType!!)
        while (cursor.moveToNext()) {
            val column = cursor.getString(cursor.getColumnIndex(COLUMN_URI))
            val folderVO = MediaFolderVO()
            folderVO.id = cursor.getString(cursor.getColumnIndex(COLUMN_BUCKET_ID))
            folderVO.name = cursor.getString(cursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME))
            folderVO.count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT))
            folderVO.coverUri = Uri.parse(column ?: "")
            folderVO.path = cursor.getString(cursor.getColumnIndex(COLUMN_DATA))
            list.add(folderVO)
        }
        cursor.close()
        return list
    }
}