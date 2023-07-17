package cn.yue.base.utils.file

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import cn.yue.base.Constant
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.utils.device.ScreenUtils
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.*

/**
 * Description :
 * Created by yue on 2019/6/18
 */
object BitmapFileUtils {

    fun init() {
        var file: File? = File(Constant.cachePath)
        if (file != null && !file.exists()) {
            file.mkdirs()
        }
        file = File(Constant.imagePath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun clearCache() {
        var file = File(Constant.cachePath)
        if (file.exists()) {
            file.deleteOnExit()
        }
        file = File(Constant.imagePath)
        if (file.exists()) {
            file.deleteOnExit()
        }
        file = File(Constant.audioPath)
        if (file.exists()) {
            file.deleteOnExit()
        }
    }

    fun getMediaFilePath(): String {
        return Constant.imagePath
    }

    fun getCacheFilePath(): String {
        return Constant.cachePath
    }

    fun getAudioFilePath(): String {
        return Constant.audioPath
    }

    private fun getSaveBitmapPath(): String {
        val sb = StringBuilder()
        sb.append(Constant.imagePath)
        sb.append(UUID.randomUUID().toString())
        sb.append(".jpg")
        return sb.toString()
    }


    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    fun isCanUseSD(): Boolean {
        try {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getBitmapFormLoaclPath(url: String): Bitmap? {
        try {
            val fileInputStream = FileInputStream(url)
            return getBitmapFormInputStream(fileInputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getBitmapFormNetUrl(netUrl: String): Bitmap? {
        try {
            val url = URL(netUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"   //设置请求方法为GET
            conn.readTimeout = 5 * 1000    //设置请求过时时间为5秒
            val inputStream = conn.inputStream   //通过输入流获得图片数据
            return getBitmapFormInputStream(inputStream)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getBitmapFormInputStream(inputStream: InputStream?): Bitmap? {
        if (inputStream == null) {
            return null
        }
        try {
            val data = readStream(inputStream)
            if (data != null) {
                //return BitmapFactory.decodeByteArray(data, 0, data.length);
                return decodeSampledBitmapFromDataArray(data, ScreenUtils.screenWidth, ScreenUtils.screenHeight)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }

    @Throws(Exception::class)
     fun readStream(inStream: InputStream): ByteArray? {
        val outStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len = 0
        while (len != -1) {
            outStream.write(buffer, 0, len)
            len = inStream.read(buffer)
        }
        outStream.close()
        inStream.close()
        return outStream.toByteArray()
    }


    private fun getBitmapFromUrl(url: String): Bitmap? {
        if (TextUtils.isEmpty(url)) {
            return null
        }
        return if (url.startsWith("http")) {
            getBitmapFormNetUrl(url)
        } else getBitmapFormLoaclPath(url)
    }

    /**
     * 将图片压缩后存入文件
     *
     * @param image
     * @param fos
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun compressImage(image: Bitmap, fos: FileOutputStream) {
        val baos = ByteArrayOutputStream()
        var optionsSize = 10
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset()//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
            options -= optionsSize//每次都减少10
            if (options == 10) {
                optionsSize = 5
            }
            if (options == 5) {
                optionsSize = 1
            }
            if (options == 0) {
                break
            }
        }
        image.recycle()
        fos.write(baos.toByteArray())
        fos.flush()
    }

    private fun saveCompressImage(bitmap: Bitmap?, name: String): Boolean {
        var fos: FileOutputStream? = null
        try {
            val file = File(name)
            if (!isCanUseSD()) {
                return false
            }
            // 文件目录是否存在
            val fileDir = File(getMediaFilePath())
            if (fileDir != null && !fileDir.exists()) {
                fileDir.mkdirs()
            }
            val fileDir1 = File(Constant.imagePath)
            if (fileDir1 != null && !fileDir1.exists()) {
                fileDir1.mkdirs()
            }
            //文件是否存在
            if (!file.exists()) {
                file.createNewFile()
            }
            fos = FileOutputStream(file)
            compressImage(bitmap!!, fos)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: Exception) {
                }

            }
        }
    }

    fun saveCompressImage(bitmap: Bitmap): Boolean {
        val saveFile = getSaveBitmapPath()
        return saveCompressImage(bitmap, saveFile)
    }


    fun getCompressBitmapFile(url: String): File {
        var bitmap = getBitmapFromUrl(url)
        if (getBitmapDegree(url) != 0) {
            bitmap = rotateBitmapByDegree(bitmap, getBitmapDegree(url))
        }
        val saveFile = getSaveBitmapPath()
        saveCompressImage(bitmap, saveFile)
        return File(saveFile)
    }

    fun getCompressBitmapFile(bitmap: Bitmap): File {
        val saveFile = getSaveBitmapPath()
        saveCompressImage(bitmap, saveFile)
        return File(saveFile)
    }


    fun getBitmapHeightAndWith(url: String, size: IntArray) {
        var bitmap = getBitmapFromUrl(url)
        if (bitmap != null) {
            size[0] = bitmap.width
            size[1] = bitmap.height
            bitmap.recycle()
            bitmap = null
        } else {
            size[0] = 0
            size[1] = 0
        }
    }

    fun getLocalHeightAndWith(url: String, size: IntArray) {
        if (TextUtils.isEmpty(url) || url.startsWith("http") || url.startsWith("https")) {
            size[0] = 0
            size[1] = 0
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bitmap: Bitmap? = BitmapFactory.decodeFile(url, options)
        if (options != null) {
            size[0] = options.outWidth
            size[1] = options.outHeight
            bitmap = null
        } else {
            size[0] = 0
            size[1] = 0
        }
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */

    fun getBitmapDegree(path: String): Int {
        var degree = 0
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            val exifInterface = ExifInterface(path)
            // 获取图片的旋转信息
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    fun rotateBitmapByDegree(bm: Bitmap?,
                             degree: Int): Bitmap {
        var returnBm: Bitmap? = null
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm!!,
                    0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }

        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm!!.recycle()
        }
        return returnBm!!
    }

    /**
     * webview上传图片使用,压缩图片返回uri
     *
     * @param path
     * @return
     */
    fun getCompressBitmapUri(path: String): Uri {
        return Uri.fromFile(getCompressBitmapFile(path))
    }


    /*根据传入的宽和高，计算出合适的inSampleSize值：*/
    fun calculateInSampleSize(options: BitmapFactory.Options,
                              reqWidth: Int, reqHeight: Int): Int {
        // 源图片的高度和宽度
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    //使用这个方法，首先你要将BitmapFactory.Options的inJustDecodeBounds属性设置为true，解析一次图片。然后将BitmapFactory.Options连同期望的宽度和高度一起传递到到calculateInSampleSize方法中，就可以得到合适的inSampleSize值了。之后再解析一次图片，使用新获取到的inSampleSize值，并把inJustDecodeBounds设置为false，就可以得到压缩后的图片了。
    fun decodeSampledBitmapFromDataArray(data: ByteArray,
                                         reqWidth: Int, reqHeight: Int): Bitmap {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeByteArray(data, 0, data.size, options)
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(data, 0, data.size, options)

    }

    fun createRandomFile(): File {
        val storePath = Constant.cachePath
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val uuid = UUID.randomUUID().toString()
        val tempFile = File(Constant.cachePath, "$uuid.jpg")
        return tempFile
    }

    //保存文件到指定路径
    fun saveImageToGallery(context: Context, bmp: Bitmap): Boolean {
        // 首先保存图片
        val storePath = Constant.imagePath
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片
            val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos)
            fos.flush()
            fos.close()

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            val uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            if (isSuccess) {
                ToastUtils.showShortToast("图片已保存到$storePath")
                return true
            } else {
                return false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    //保存文件到指定路径
    fun saveBitmapToFile(context: Context, bmp: Bitmap): String? {
        // 首先保存图片
        val storePath = Constant.imagePath
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片
            val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos)
            fos.flush()
            fos.close()

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            val uri = Uri.fromFile(file)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            return if (isSuccess) {
                file.getPath()
            } else {
                file.getPath()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

}