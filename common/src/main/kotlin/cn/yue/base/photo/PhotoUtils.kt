package cn.yue.base.photo

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import cn.yue.base.photo.data.MediaFolderVO
import cn.yue.base.photo.data.MediaType
import cn.yue.base.photo.data.MediaData
import cn.yue.base.photo.data.MimeType.Companion.isImage
import cn.yue.base.photo.data.MimeType.Companion.isVideo
import cn.yue.base.photo.loader.MediaFolderLoader.COLUMN_BUCKET_DISPLAY_NAME
import cn.yue.base.photo.loader.MediaFolderLoader.COLUMN_BUCKET_ID
import cn.yue.base.photo.loader.MediaFolderLoader.COLUMN_COUNT
import cn.yue.base.photo.loader.MediaFolderLoader.COLUMN_DATA
import cn.yue.base.photo.loader.MediaFolderLoader.COLUMN_URI
import cn.yue.base.photo.loader.MediaFolderLoader.load
import cn.yue.base.photo.loader.MediaLoader.load
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
    fun getAllMedia(context: Context): List<MediaData> {
        return getMediaByFolder(context, true, null, MediaType.ALL)
    }

    /**
     * 获取所有图片文件
     * @param context
     * @return
     */
    fun getAllMediaPhotos(context: Context): List<MediaData> {
        return getMediaByFolder(context, true, null, MediaType.PHOTO)
    }

    /**
     * 获取所有视频
     * @param context
     * @return
     */
    fun getAllMediaVideos(context: Context): List<MediaData> {
        return getMediaByFolder(context, true, null, MediaType.VIDEO)
    }

    /**
     * 获取最近num个资源
     * @param num
     * @return
     */
    @JvmStatic
    fun getTheLastMedias(context: Context, num: Int, mediaType: MediaType): ArrayList<MediaData> {
        val list = ArrayList<MediaData>()
        val photos = getMediaByFolder(context, true, null, mediaType)
        if (photos.size > num) {
            list.addAll(photos.subList (0, num))
        } else {
            list.addAll(photos)
        }
        return list
    }

    /**
     * 获取对应路径下的所有资源
     * @param context
     * @param isAll
     * @param folderId
     * @param mediaType
     * @return
     */
    @SuppressLint("Range")
    fun getMediaByFolder(context: Context, isAll: Boolean, folderId: String?, mediaType: MediaType): ArrayList<MediaData> {
        val list = ArrayList<MediaData>()
        val cursor = load(context, isAll, folderId, mediaType)
        while (cursor.moveToNext()) {
            val mediaVO = MediaData()
            val id = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            mediaVO.id = id
            val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
            mediaVO.mimeType = mimeType
            mediaVO.size = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE).toLong()
            mediaVO.duration = cursor.getLong(cursor.getColumnIndex("duration"))
            val orientation = cursor.getInt(cursor.getColumnIndex("orientation"))
            if (orientation == 90 || orientation == 270) {
                mediaVO.height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                mediaVO.width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
            } else {
                mediaVO.width = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
                mediaVO.height = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
            }
            val contentUri = if (isImage(mimeType)) {
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
    fun getAllPhotosFolder(context: Context): MutableList<MediaFolderVO> {
        return getAllMediaFolder(context, MediaType.ALL)
    }

    /**
     * 获取资源文件夹
     * @param context
     * @return
     */
    @SuppressLint("Range")
    fun getAllMediaFolder(context: Context, mediaType: MediaType): MutableList<MediaFolderVO> {
        val list: MutableList<MediaFolderVO> = ArrayList()
        val cursor = load(context, mediaType)
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