package cn.yue.base.photo.loader

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContentResolverCompat
import androidx.core.os.CancellationSignal
import cn.yue.base.photo.data.MediaType
import cn.yue.base.photo.data.MediaType.Companion.onlyShowImages
import cn.yue.base.photo.data.MediaType.Companion.onlyShowVideos
import cn.yue.base.photo.data.MimeType.Companion.isImage
import cn.yue.base.photo.data.MimeType.Companion.isVideo
import java.util.*

object MediaFolderLoader {
    const val COLUMN_BUCKET_ID = "bucket_id"
    const val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"
    const val COLUMN_URI = "uri"
    const val COLUMN_COUNT = "count"
    const val COLUMN_DATA = "_data"
    private val QUERY_URI = MediaStore.Files.getContentUri("external")
    private val COLUMNS = arrayOf(
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_URI,
            COLUMN_COUNT,
            COLUMN_DATA
    )
    private val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            "COUNT(*) AS $COLUMN_COUNT",
            COLUMN_DATA
    )
    private val PROJECTION_29 = arrayOf(
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_DATA
    )

    // === params for showSingleMediaType: false ===
    private const val SELECTION = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + ") GROUP BY (bucket_id")
    private const val SELECTION_29 = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")
    private val SELECTION_ARGS = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

    // =============================================
    // === params for showSingleMediaType: true ===
    private const val SELECTION_FOR_SINGLE_MEDIA_TYPE = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + ") GROUP BY (bucket_id")
    private const val SELECTION_FOR_SINGLE_MEDIA_TYPE_29 = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String> {
        return arrayOf(mediaType.toString())
    }

    // =============================================
    private fun getSelection(mediaType: MediaType): String {
        return if (onlyShowImages(mediaType)) {
            if (beforeAndroidTen()) SELECTION_FOR_SINGLE_MEDIA_TYPE else SELECTION_FOR_SINGLE_MEDIA_TYPE_29
        } else if (onlyShowVideos(mediaType)) {
            if (beforeAndroidTen()) SELECTION_FOR_SINGLE_MEDIA_TYPE else SELECTION_FOR_SINGLE_MEDIA_TYPE_29
        } else {
            if (beforeAndroidTen()) SELECTION else SELECTION_29
        }
    }

    private fun getSelectionArgs(mediaType: MediaType): Array<String> {
        return if (onlyShowImages(mediaType)) {
            getSelectionArgsForSingleMediaType(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        } else if (onlyShowVideos(mediaType)) {
            getSelectionArgsForSingleMediaType(
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        } else {
            SELECTION_ARGS
        }
    }

    // =============================================
    private const val BUCKET_ORDER_BY = "datetaken DESC"
    private const val ALBUM_ID_ALL: String = "-1"
    private const val ALBUM_NAME_ALL = "All"
    @SuppressLint("Range")
    @JvmStatic
    fun load(context: Context, mediaType: MediaType): Cursor {
        val albums = ContentResolverCompat.query(context.contentResolver,
                QUERY_URI, if (beforeAndroidTen()) PROJECTION else PROJECTION_29, getSelection(mediaType), getSelectionArgs(mediaType), BUCKET_ORDER_BY,
                CancellationSignal())
        val allAlbum = MatrixCursor(COLUMNS)
        return if (beforeAndroidTen()) {
            var totalCount = 0
            var allAlbumCoverUri: Uri? = null
            val otherAlbums = MatrixCursor(COLUMNS)
            if (albums != null) {
                while (albums.moveToNext()) {
                    val fileId = albums.getLong(
                            albums.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    val bucketId = albums.getLong(
                            albums.getColumnIndex(COLUMN_BUCKET_ID))
                    val bucketDisplayName = albums.getString(
                            albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME))
                    val mimeType = albums.getString(
                            albums.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    val uri = getUri(albums)
                    val count = albums.getInt(albums.getColumnIndex(COLUMN_COUNT))
                    val data = albums.getString(albums.getColumnIndex(COLUMN_DATA))
                    otherAlbums.addRow(arrayOf(
                            fileId.toString(),
                            bucketId.toString(),
                            bucketDisplayName,
                            mimeType,
                            uri.toString(), count.toString(),
                            data))
                    totalCount += count
                }
                if (albums.moveToFirst()) {
                    allAlbumCoverUri = getUri(albums)
                }
            }
            allAlbum.addRow(arrayOf(
                    ALBUM_ID_ALL, ALBUM_ID_ALL, ALBUM_NAME_ALL, null,
                    allAlbumCoverUri?.toString(), totalCount.toString(), ""))
            MergeCursor(arrayOf<Cursor>(allAlbum, otherAlbums))
        } else {
            var totalCount = 0
            var allAlbumCoverUri: Uri? = null

            // Pseudo GROUP BY
            val countMap: MutableMap<Long, Long> = HashMap()
            if (albums != null) {
                while (albums.moveToNext()) {
                    val bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID))
                    var count = countMap[bucketId]
                    if (count == null) {
                        count = 1L
                    } else {
                        count++
                    }
                    countMap[bucketId] = count
                }
            }
            val otherAlbums = MatrixCursor(COLUMNS)
            if (albums != null) {
                if (albums.moveToFirst()) {
                    allAlbumCoverUri = getUri(albums)
                    val done: MutableSet<Long> = HashSet()
                    do {
                        val bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID))
                        if (done.contains(bucketId)) {
                            continue
                        }
                        val fileId = albums.getLong(
                                albums.getColumnIndex(MediaStore.Files.FileColumns._ID))
                        val bucketDisplayName = albums.getString(
                                albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME))
                        val mimeType = albums.getString(
                                albums.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                        val uri = getUri(albums)
                        val count = countMap[bucketId]!!
                        val data = albums.getString(albums.getColumnIndex(COLUMN_DATA))
                        otherAlbums.addRow(arrayOf(
                                fileId.toString(),
                                bucketId.toString(),
                                bucketDisplayName,
                                mimeType,
                                uri.toString(), count.toString(),
                                data))
                        done.add(bucketId)
                        totalCount += count.toInt()
                    } while (albums.moveToNext())
                }
            }
            allAlbum.addRow(arrayOf(
                    ALBUM_ID_ALL,
                    ALBUM_ID_ALL,
                    ALBUM_NAME_ALL,
                    null,
                    allAlbumCoverUri?.toString(), totalCount.toString(),
                    ""
            ))
            MergeCursor(arrayOf<Cursor>(allAlbum, otherAlbums))
        }
    }

    @SuppressLint("Range")
    private fun getUri(cursor: Cursor): Uri {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
        val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
        val contentUri: Uri = if (isImage(mimeType)) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if (isVideo(mimeType)) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            // ?
            MediaStore.Files.getContentUri("external")
        }
        return ContentUris.withAppendedId(contentUri, id)
    }

    /**
     * @return 是否是 Android 10 （Q） 之前的版本
     */
    private fun beforeAndroidTen(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    }
}