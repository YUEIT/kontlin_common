package cn.yue.base.common.utils.file

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.WorkerThread
import cn.yue.base.common.photo.data.MimeType
import cn.yue.base.common.photo.data.MimeType.Companion.isImage
import cn.yue.base.common.photo.data.MimeType.Companion.isVideo
import cn.yue.base.common.utils.Utils.getContext
import java.io.*
import java.util.*

/**
 * Description : android Q 沙盒; 外部存储只能通过MediaStore 或者 SAF两种方式操作
 * Created by yue on 2020/5/21
 */
object AndroidQFileUtils {
    //对于MediaStore下Images、Video、Audio 只能操作在DCIM目录; File只能在Documents、Download;
    private const val IMAGE_PATH = "DCIM/image/"
    private const val VIDEO_PATH = "DCIM/video/"
    private const val AUDIO_PATH = "DCIM/audio/"
    private const val FILE_PATH = "Documents/file/"

    /**
     * 判断文件是否存在
     *
     * @param uri
     * @return
     */
    fun fileUriIsExists(uri: Uri?): Boolean {
        if (uri == null) {
            return false
        }
        try {
            val fileDescriptor = getContext().contentResolver.openFileDescriptor(uri, "r")
            if (fileDescriptor != null) {
                return fileDescriptor.fileDescriptor.valid()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    @WorkerThread
    fun getBitmapFromUri(uri: Uri?): Bitmap? {
        try {
            val parcelFileDescriptor = getContext().contentResolver.openFileDescriptor(uri!!, "r")
            if (parcelFileDescriptor != null) {
                val fileDescriptor = parcelFileDescriptor.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
                return image
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun getTextFromUri(uri: Uri?): String? {
        try {
            val stringBuilder = StringBuilder()
            val inputStream = getContext().contentResolver.openInputStream(uri!!)
            val reader = BufferedReader(
                    InputStreamReader(Objects.requireNonNull(inputStream)))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun saveBitmap(bitmap: Bitmap, fileName: String?): Uri? {
        return saveBitmap(bitmap, IMAGE_PATH, fileName)
    }

    /**
     * 保存bitmap
     *
     * @param relativePath
     * @param bitmap
     * @param fileName
     * @return
     */
    fun saveBitmap(bitmap: Bitmap, relativePath: String?, fileName: String?): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver = getContext().contentResolver
        val resultUri = contentResolver.insert(contentUri, contentValues)
        try {
            if (resultUri != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, contentResolver.openOutputStream(resultUri))
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return resultUri
    }

    fun saveFile(inputStream: InputStream, fileName: String?, mimeType: String?): Uri? {
        return saveFile(inputStream, FILE_PATH, fileName, mimeType)
    }

    /**
     * 保存文件
     *
     * @param relativePath
     * @param inputStream
     * @param fileName
     * @return
     */
    fun saveFile(inputStream: InputStream, relativePath: String?, fileName: String?, mimeType: String?): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, relativePath)
        val contentUri = MediaStore.Files.getContentUri("external")
        val contentResolver = getContext().contentResolver
        val resultUri = contentResolver.insert(contentUri, contentValues)
        try {
            if (resultUri != null) {
                var read = -1
                val buffer = ByteArray(2048)
                val outputStream = contentResolver.openOutputStream(resultUri)
                if (outputStream != null) {
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return resultUri
    }

    fun saveVideo(inputStream: InputStream, fileName: String?, mimeType: String?): Uri? {
        return saveVideo(inputStream, VIDEO_PATH, fileName, mimeType)
    }

    /**
     * 保存视频
     *
     * @param inputStream
     * @param fileName
     * @param mimeType
     * @return
     */
    fun saveVideo(inputStream: InputStream, relativePath: String?, fileName: String?, mimeType: String?): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, relativePath)
        val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val contentResolver = getContext().contentResolver
        val resultUri = contentResolver.insert(contentUri, contentValues)
        try {
            if (resultUri != null) {
                var read = -1
                val buffer = ByteArray(2048)
                val outputStream = contentResolver.openOutputStream(resultUri)
                if (outputStream != null) {
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return resultUri
    }

    fun saveAudio(inputStream: InputStream, fileName: String?, mimeType: String?): Uri? {
        return saveVideo(inputStream, AUDIO_PATH, fileName, mimeType)
    }

    /**
     * 保存音频
     *
     * @param relativePath
     * @param inputStream
     * @param fileName
     * @param mimeType
     * @return
     */
    fun saveAudio(inputStream: InputStream, relativePath: String?, fileName: String?, mimeType: String?): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Audio.Media.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath)
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val contentResolver = getContext().contentResolver
        val resultUri = contentResolver.insert(contentUri, contentValues)
        try {
            if (resultUri != null) {
                var read = -1
                val buffer = ByteArray(2048)
                val outputStream = contentResolver.openOutputStream(resultUri)
                if (outputStream != null) {
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return resultUri
    }

    fun saveImage(inputStream: InputStream, fileName: String?, mimeType: String?): Uri? {
        return saveImage(inputStream, IMAGE_PATH, fileName, mimeType)
    }

    /**
     * 保存图片
     *
     * @param relativePath
     * @param inputStream
     * @param fileName
     * @param mimeType
     * @return
     */
    fun saveImage(inputStream: InputStream, relativePath: String?, fileName: String?, mimeType: String?): Uri? {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver = getContext().contentResolver
        val resultUri = contentResolver.insert(contentUri, contentValues)
        try {
            if (resultUri != null) {
                var read = -1
                val buffer = ByteArray(2048)
                val outputStream = contentResolver.openOutputStream(resultUri)
                if (outputStream != null) {
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return resultUri
    }
    
    fun createNewFile(context: Context, uri: Uri): File {
        val file = BitmapFileUtils.createRandomFile()
        try {
            //根据uri获取输入字节流
            val inputStream = context.contentResolver.openInputStream(uri)
            //把输入流写进file里
            val fileOutputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var byteRead = 0
            while (-1 != (inputStream!!.read(buffer).also { byteRead = it })){
                fileOutputStream.write(buffer,0,byteRead)
            }
            fileOutputStream.flush();
            inputStream.close();
            fileOutputStream.close();
        } catch (e : Exception) {
            e.printStackTrace();
        }
        return file
    }

    /**
     * 获取uri的文件路径 （DATA 已经被Deprecated，慎用！）
     *
     * @param uri
     * @return
     */
    @SuppressLint("Range")
    fun getPath(uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        if ("content" == uri.scheme) {
            var cursor: Cursor? = null
            return try {
                val contentResolver = getContext().contentResolver
                cursor = contentResolver.query(uri, arrayOf("_data"),
                        null, null, null)
                if (cursor == null || !cursor.moveToFirst()) {
                    null
                } else cursor.getString(cursor.getColumnIndex("_data"))
            } finally {
                cursor?.close()
            }
        }
        return uri.path
    }

    fun getMediaUriFromName(fileName: String?, mimeType: String?): Uri? {
        return getMediaUriFromName("", fileName, mimeType)
    }

    /**
     * 获取Uri
     *
     * @param relativePath
     * @param fileName
     * @param mimeType
     * @return
     */
    @SuppressLint("Range")
    fun getMediaUriFromName(relativePath: String?, fileName: String?, mimeType: String?): Uri? {
        val mediaUri: Uri
        var selection: String
        val mediaId: String
        var selectionArgs: Array<String?>
        val findInPath = !TextUtils.isEmpty(relativePath)
        if (isImage(mimeType)) {
            mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = MediaStore.Images.Media.DISPLAY_NAME + "=?"
            if (findInPath) {
                selection = selection + " AND " + MediaStore.Images.Media.RELATIVE_PATH + "=?"
            }
            mediaId = MediaStore.Images.Media._ID
        } else if (isVideo(mimeType)) {
            mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            selection = MediaStore.Video.Media.DISPLAY_NAME + "=?"
            if (findInPath) {
                selection = selection + " AND " + MediaStore.Video.Media.RELATIVE_PATH + "=?"
            }
            mediaId = MediaStore.Video.Media._ID
        } else if (MimeType.isAudio(mimeType!!)) {
            mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            selection = MediaStore.Audio.Media.DISPLAY_NAME + "=?"
            if (findInPath) {
                selection = selection + " AND " + MediaStore.Video.Media.RELATIVE_PATH + "=?"
            }
            mediaId = MediaStore.Audio.Media._ID
        } else if (MimeType.isFile(mimeType)) {
            mediaUri = MediaStore.Files.getContentUri("external")
            selection = MediaStore.Files.FileColumns.DISPLAY_NAME + "=?"
            if (findInPath) {
                selection = selection + " AND " + MediaStore.Video.Media.RELATIVE_PATH + "=?"
            }
            mediaId = MediaStore.Files.FileColumns._ID
        } else {
            return null
        }
        selectionArgs = arrayOf(fileName)
        if (findInPath) {
            selectionArgs = arrayOf(fileName, relativePath)
        }
        val cursor = getContext().contentResolver.query(mediaUri,
                null,
                selection,
                selectionArgs,
                null)
        var uri: Uri? = null
        if (cursor != null && cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(mediaId)))
            cursor.close()
        }
        return uri
    }
    
    fun getPathFromUri(context: Context, uri: Uri): String {
        var imagePath: String? = ""
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                // 解析出数字格式的id
                val id = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                //注意！这里第二个参数千万不能传上面传参进来的uri
                imagePath = getImagePath(context,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    docId.toLong()
                )
                imagePath = getImagePath(context, contentUri, null)
            } else if ("com.android.externalstorage.documents" == uri.authority) {
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    imagePath = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(context, uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        return imagePath ?: ""
    }
    
    fun getImagePath(context: Context, uri: Uri, selection: String?): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, selection,
            null, null)
        val picPath = try {
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            cursor?.getString(columnIndex!!) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            cursor?.close()
        }
        return picPath
    }
}