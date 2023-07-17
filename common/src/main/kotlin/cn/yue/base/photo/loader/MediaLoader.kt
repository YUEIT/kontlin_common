package cn.yue.base.photo.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import androidx.core.os.CancellationSignal
import cn.yue.base.photo.data.MediaType
import cn.yue.base.photo.data.MediaType.Companion.onlyShowImages
import cn.yue.base.photo.data.MediaType.Companion.onlyShowVideos

object MediaLoader {
    private const val COLUMN_DATA = "_data"
    private val QUERY_URI = MediaStore.Files.getContentUri("external")
    private val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
//            MediaStore.MediaColumns.DURATION,
            "duration",
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
        //  MediaStore.MediaColumns.ORIENTATION,
            "orientation",
            COLUMN_DATA
    )

    // === params for album ALL && showSingleMediaType: false ===
    private const val SELECTION_ALL = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")
    private val SELECTION_ALL_ARGS = arrayOf<String?>(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

    // ===========================================================
    // === params for album ALL && showSingleMediaType: true ===
    private const val SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String?> {
        return arrayOf(mediaType.toString())
    }

    // =========================================================
    // === params for ordinary album && showSingleMediaType: false ===
    private const val SELECTION_ALBUM = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND "
            + " bucket_id=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionAlbumArgs(albumId: String?): Array<String?> {
        return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
                albumId
        )
    }

    // ===============================================================
    // === params for ordinary album && showSingleMediaType: true ===
    private const val SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND "
            + " bucket_id=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionAlbumArgsForSingleMediaType(mediaType: Int, albumId: String?): Array<String?> {
        return arrayOf(mediaType.toString(), albumId)
    }

    // ===============================================================
    private fun getSelection(isAll: Boolean, mediaType: MediaType): String {
        return if (isAll) {
            if (onlyShowImages(mediaType)) {
                SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
            } else if (onlyShowVideos(mediaType)) {
                SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE
            } else {
                SELECTION_ALL
            }
        } else {
            if (onlyShowImages(mediaType)) {
                SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
            } else if (onlyShowVideos(mediaType)) {
                SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE
            } else {
                SELECTION_ALBUM
            }
        }
    }

    private fun getSelectionArgs(isAll: Boolean, folderId: String?, mediaType: MediaType): Array<String?> {
        return if (isAll) {
            if (onlyShowImages(mediaType)) {
                getSelectionArgsForSingleMediaType(
                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            } else if (onlyShowVideos(mediaType)) {
                getSelectionArgsForSingleMediaType(
                        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            } else {
                SELECTION_ALL_ARGS
            }
        } else {
            if (onlyShowImages(mediaType)) {
                getSelectionAlbumArgsForSingleMediaType(
                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        folderId)
            } else if (onlyShowVideos(mediaType)) {
                getSelectionAlbumArgsForSingleMediaType(
                        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                        folderId)
            } else {
                getSelectionAlbumArgs(folderId)
            }
        }
    }

    // ===============================================================
    private const val ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC"
    const val ITEM_ID_CAPTURE: Long = -1
    const val ITEM_DISPLAY_NAME_CAPTURE = "Capture"
    @JvmStatic
    fun load(context: Context, isAll: Boolean, folderId: String?, mediaType: MediaType): Cursor {
        val result = ContentResolverCompat.query(context.contentResolver,
                QUERY_URI, PROJECTION, getSelection(isAll, mediaType), getSelectionArgs(isAll, folderId, mediaType), ORDER_BY,
                CancellationSignal())
        val dummy = MatrixCursor(PROJECTION)
        //        dummy.addRow(new Object[]{ITEM_ID_CAPTURE, ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0});
        return MergeCursor(arrayOf(dummy, result))
    }
}