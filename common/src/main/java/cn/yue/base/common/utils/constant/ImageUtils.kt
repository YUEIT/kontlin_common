package cn.yue.base.common.utils.constant

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.media.ExifInterface
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.view.View
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.debug.CloseUtils
import cn.yue.base.common.utils.file.FileUtils
import java.io.*

/**
 * 介绍：图片相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object ImageUtils {

    /**
     * bitmap转byteArr
     *
     * @param bitmap bitmap对象
     * @param format 格式
     * @return 字节数组
     */
    @JvmStatic
    fun bitmap2Bytes(bitmap: Bitmap?, format: CompressFormat): ByteArray? {
        if (bitmap == null) return null
        val baos = ByteArrayOutputStream()
        bitmap.compress(format, 100, baos)
        return baos.toByteArray()
    }

    /**
     * byteArr转bitmap
     *
     * @param bytes 字节数组
     * @return bitmap
     */
    @JvmStatic
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes == null || bytes.size == 0) null else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap
     */
    @JvmStatic
    fun drawable2Bitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        } else if (drawable is NinePatchDrawable) {
            val bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    if (drawable.getOpacity() != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight())
            drawable.draw(canvas)
            return bitmap
        } else {
            return null
        }
    }

    /**
     * bitmap转drawable
     *
     * @param res    resources对象
     * @param bitmap bitmap对象
     * @return drawable
     */
    @JvmStatic
    fun bitmap2Drawable(res: Resources, bitmap: Bitmap?): Drawable? {
        return if (bitmap == null) null else BitmapDrawable(res, bitmap)
    }

    /**
     * drawable转byteArr
     *
     * @param drawable drawable对象
     * @param format   格式
     * @return 字节数组
     */
    @JvmStatic
    fun drawable2Bytes(drawable: Drawable?, format: CompressFormat): ByteArray? {
        return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format)
    }

    /**
     * byteArr转drawable
     *
     * @param res   resources对象
     * @param bytes 字节数组
     * @return drawable
     */
    @JvmStatic
    fun bytes2Drawable(res: Resources?, bytes: ByteArray): Drawable? {
        return if (res == null) null else bitmap2Drawable(res, bytes2Bitmap(bytes))
    }

    /**
     * view转Bitmap
     *
     * @param view 视图
     * @return bitmap
     */
    @JvmStatic
    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val ret = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(ret)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return ret
    }

    /**
     * 计算采样大小
     *
     * @param options   选项
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 采样大小
     */
    private @JvmStatic
    fun calculateInSampleSize(options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int): Int {
        if (maxWidth == 0 || maxHeight == 0) return 1
        var height = options.outHeight shr 1
        var width = options.outWidth shr 1
        var inSampleSize = 1
        while (height >= maxHeight && width >= maxWidth) {
            inSampleSize = inSampleSize shl 1
            height = height shr 1
            width = width shr 1
        }
        return inSampleSize
    }

    /**
     * 获取bitmap
     *
     * @param file 文件
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(file: File?): Bitmap? {
        if (file == null) return null
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            return BitmapFactory.decodeStream(`is`)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(`is` as Closeable)
        }
    }

    /**
     * 获取bitmap
     *
     * @param file      文件
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(file: File?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (file == null) return null
        var `is`: InputStream? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            `is` = BufferedInputStream(FileInputStream(file))
            BitmapFactory.decodeStream(`is`, null, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(`is`, null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(`is` as Closeable)
        }
    }

    /**
     * 获取bitmap
     *
     * @param filePath 文件路径
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(filePath: String): Bitmap? {
        return if (StringUtils.isSpace(filePath)) null else BitmapFactory.decodeFile(filePath)
    }

    /**
     * 获取bitmap
     *
     * @param filePath  文件路径
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(filePath: String, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (StringUtils.isSpace(filePath)) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 获取bitmap
     *
     * @param is 输入流
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(`is`: InputStream?): Bitmap? {
        return if (`is` == null) null else BitmapFactory.decodeStream(`is`)
    }

    /**
     * 获取bitmap
     *
     * @param is        输入流
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(`is`: InputStream?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (`is` == null) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(`is`, null, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(`is`, null, options)
    }

    /**
     * 获取bitmap
     *
     * @param data   数据
     * @param offset 偏移量
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(data: ByteArray, offset: Int): Bitmap? {
        return if (data.size == 0) null else BitmapFactory.decodeByteArray(data, offset, data.size)
    }

    /**
     * 获取bitmap
     *
     * @param data      数据
     * @param offset    偏移量
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(data: ByteArray, offset: Int, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (data.size == 0) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, offset, data.size, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(data, offset, data.size, options)
    }

    /**
     * 获取bitmap
     *
     * @param res 资源对象
     * @param id  资源id
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(res: Resources?, id: Int): Bitmap? {
        return if (res == null) null else BitmapFactory.decodeResource(res, id)
    }

    /**
     * 获取bitmap
     *
     * @param res       资源对象
     * @param id        资源id
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(res: Resources?, id: Int, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (res == null) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, id, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, id, options)
    }

    /**
     * 获取bitmap
     *
     * @param fd 文件描述
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(fd: FileDescriptor?): Bitmap? {
        return if (fd == null) null else BitmapFactory.decodeFileDescriptor(fd)
    }

    /**
     * 获取bitmap
     *
     * @param fd        文件描述
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    @JvmStatic
    fun getBitmap(fd: FileDescriptor?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (fd == null) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(fd, null, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFileDescriptor(fd, null, options)
    }

    /**
     * 缩放图片
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun scale(src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 缩放图片
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun scale(src: Bitmap, scaleWidth: Float, scaleHeight: Float, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setScale(scaleWidth, scaleHeight)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 裁剪图片
     *
     * @param src     源图片
     * @param x       开始坐标x
     * @param y       开始坐标y
     * @param width   裁剪宽度
     * @param height  裁剪高度
     * @param recycle 是否回收
     * @return 裁剪后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun clip(src: Bitmap, x: Int, y: Int, width: Int, height: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createBitmap(src, x, y, width, height)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 倾斜图片
     *
     * @param src     源图片
     * @param kx      倾斜因子x
     * @param ky      倾斜因子y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    @JvmStatic
    fun skew(src: Bitmap, kx: Float, ky: Float, recycle: Boolean): Bitmap? {
        return skew(src, kx, ky, 0f, 0f, recycle)
    }

    /**
     * 倾斜图片
     *
     * @param src     源图片
     * @param kx      倾斜因子x
     * @param ky      倾斜因子y
     * @param px      平移因子x
     * @param py      平移因子y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun skew(src: Bitmap, kx: Float, ky: Float, px: Float = 0f, py: Float = 0f, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setSkew(kx, ky, px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 旋转图片
     *
     * @param src     源图片
     * @param degrees 旋转角度
     * @param px      旋转点横坐标
     * @param py      旋转点纵坐标
     * @param recycle 是否回收
     * @return 旋转后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun rotate(src: Bitmap, degrees: Int, px: Float, py: Float, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        if (degrees == 0) return src
        val matrix = Matrix()
        matrix.setRotate(degrees.toFloat(), px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 获取图片旋转角度
     *
     * @param filePath 文件路径
     * @return 旋转角度
     */
    @JvmStatic
    fun getRotateDegree(filePath: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(filePath)
            val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                else -> degree = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    /**
     * 转为圆形图片
     *
     * @param src     源图片
     * @param recycle 是否回收
     * @return 圆形图片
     */
    @JvmOverloads
    @JvmStatic
    fun toRound(src: Bitmap, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val radius = Math.min(width, height) shr 1
        val ret = Bitmap.createBitmap(width, height, src.config)
        val paint = Paint()
        val canvas = Canvas(ret)
        val rect = Rect(0, 0, width, height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle((width shr 1).toFloat(), (height shr 1).toFloat(), radius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, rect, rect, paint)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 转为圆角图片
     *
     * @param src     源图片
     * @param radius  圆角的度数
     * @param recycle 是否回收
     * @return 圆角图片
     */
    @JvmOverloads
    @JvmStatic
    fun toRoundCorner(src: Bitmap?, radius: Float, recycle: Boolean = false): Bitmap? {
        if (null == src) return null
        val width = src.width
        val height = src.height
        val ret = Bitmap.createBitmap(width, height, src.config)
        val paint = Paint()
        val canvas = Canvas(ret)
        val rect = Rect(0, 0, width, height)
        paint.isAntiAlias = true
        canvas.drawRoundRect(RectF(rect), radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(src, rect, rect, paint)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 快速模糊图片
     *
     * 先缩小原图，对小图进行模糊，再放大回原先尺寸
     *
     * @param src     源图片
     * @param scale   缩放比例(0...1)
     * @param radius  模糊半径(0...25)
     * @param recycle 是否回收
     * @return 模糊后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun fastBlur(src: Bitmap,
                 @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) scale: Float,
                 @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float,
                 recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val scaleWidth = (width * scale + 0.5f).toInt()
        val scaleHeight = (height * scale + 0.5f).toInt()
        if (scaleWidth == 0 || scaleHeight == 0) return null
        var scaleBitmap: Bitmap? = Bitmap.createScaledBitmap(src, scaleWidth, scaleHeight, true)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas()
        val filter = PorterDuffColorFilter(
                Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP)
        paint.colorFilter = filter
        canvas.scale(scale, scale)
        canvas.drawBitmap(scaleBitmap!!, 0f, 0f, paint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            scaleBitmap = renderScriptBlur(Utils.getContext(), scaleBitmap, radius)
        } else {
            scaleBitmap = stackBlur(scaleBitmap, radius.toInt(), recycle)
        }
        if (scale == 1f) return scaleBitmap
        val ret = Bitmap.createScaledBitmap(scaleBitmap!!, width, height, true)
        if (scaleBitmap != null && !scaleBitmap.isRecycled) scaleBitmap.recycle()
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * renderScript模糊图片
     *
     * API大于17
     *
     * @param context 上下文
     * @param src     源图片
     * @param radius  模糊半径(0...25)
     * @return 模糊后的图片
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @JvmStatic
    fun renderScriptBlur(context: Context, src: Bitmap, @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float): Bitmap? {
        if (isEmptyBitmap(src)) return null
        var rs: RenderScript? = null
        try {
            rs = RenderScript.create(context)
            rs!!.messageHandler = RenderScript.RSMessageHandler()
            val input = Allocation.createFromBitmap(rs, src, Allocation.MipmapControl.MIPMAP_NONE, Allocation
                    .USAGE_SCRIPT)
            val output = Allocation.createTyped(rs, input.type)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setInput(input)
            blurScript.setRadius(radius)
            blurScript.forEach(output)
            output.copyTo(src)
        } finally {
            if (rs != null) {
                rs.destroy()
            }
        }
        return src
    }

    /**
     * stack模糊图片
     *
     * @param src     源图片
     * @param radius  模糊半径
     * @param recycle 是否回收
     * @return stack模糊后的图片
     */
    @JvmStatic
    fun stackBlur(src: Bitmap, radius: Int, recycle: Boolean): Bitmap? {
        val ret: Bitmap
        if (recycle) {
            ret = src
        } else {
            ret = src.copy(src.config, true)
        }

        if (radius < 1) {
            return null
        }

        val w = ret.width
        val h = ret.height

        val pix = IntArray(w * h)
        ret.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h)
        return ret
    }

    /**
     * 添加颜色边框
     *
     * @param src         源图片
     * @param borderWidth 边框宽度
     * @param color       边框的颜色值
     * @param recycle     是否回收
     * @return 带颜色边框图
     */
    @JvmOverloads
    @JvmStatic
    fun addFrame(src: Bitmap, borderWidth: Int, color: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val doubleBorder = borderWidth shl 1
        val newWidth = src.width + doubleBorder
        val newHeight = src.height + doubleBorder
        val ret = Bitmap.createBitmap(newWidth, newHeight, src.config)
        val canvas = Canvas(ret)
        val rect = Rect(0, 0, newWidth, newHeight)
        val paint = Paint()
        paint.color = color
        paint.style = Paint.Style.STROKE
        // setStrokeWidth是居中画的，所以要两倍的宽度才能画，否则有一半的宽度是空的
        paint.strokeWidth = doubleBorder.toFloat()
        canvas.drawRect(rect, paint)
        canvas.drawBitmap(src, borderWidth.toFloat(), borderWidth.toFloat(), null)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 添加倒影
     *
     * @param src              源图片的
     * @param reflectionHeight 倒影高度
     * @param recycle          是否回收
     * @return 带倒影图片
     */
    @JvmOverloads
    @JvmStatic
    fun addReflection(src: Bitmap, reflectionHeight: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        // 原图与倒影之间的间距
        val REFLECTION_GAP = 0
        val srcWidth = src.width
        val srcHeight = src.height
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        val reflectionBitmap = Bitmap.createBitmap(src, 0, srcHeight - reflectionHeight,
                srcWidth, reflectionHeight, matrix, false)
        val ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.config)
        val canvas = Canvas(ret)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawBitmap(reflectionBitmap, 0f, (srcHeight + REFLECTION_GAP).toFloat(), null)
        val paint = Paint()
        paint.isAntiAlias = true
        val shader = LinearGradient(0f, srcHeight.toFloat(),
                0f, (ret.height + REFLECTION_GAP).toFloat(),
                0x70FFFFFF, 0x00FFFFFF, Shader.TileMode.MIRROR)
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawRect(0f, (srcHeight + REFLECTION_GAP).toFloat(),
                srcWidth.toFloat(), ret.height.toFloat(), paint)
        if (!reflectionBitmap.isRecycled) reflectionBitmap.recycle()
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 添加文字水印
     *
     * @param src      源图片
     * @param content  水印文本
     * @param textSize 水印字体大小
     * @param color    水印字体颜色
     * @param x        起始坐标x
     * @param y        起始坐标y
     * @return 带有文字水印的图片
     */
    @JvmStatic
    fun addTextWatermark(src: Bitmap, content: String, textSize: Int, color: Int, x: Float,
                         y: Float): Bitmap? {
        return addTextWatermark(src, content, textSize.toFloat(), color, x, y, false)
    }

    /**
     * 添加文字水印
     *
     * @param src      源图片
     * @param content  水印文本
     * @param textSize 水印字体大小
     * @param color    水印字体颜色
     * @param x        起始坐标x
     * @param y        起始坐标y
     * @param recycle  是否回收
     * @return 带有文字水印的图片
     */
    @JvmStatic
    fun addTextWatermark(src: Bitmap, content: String?, textSize: Float, color: Int, x: Float,
                         y: Float, recycle: Boolean): Bitmap? {
        if (isEmptyBitmap(src) || content == null) return null
        val ret = src.copy(src.config, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas(ret)
        paint.color = color
        paint.textSize = textSize
        val bounds = Rect()
        paint.getTextBounds(content, 0, content.length, bounds)
        canvas.drawText(content, x, y + textSize, paint)
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 添加图片水印
     *
     * @param src       源图片
     * @param watermark 图片水印
     * @param x         起始坐标x
     * @param y         起始坐标y
     * @param alpha     透明度
     * @param recycle   是否回收
     * @return 带有图片水印的图片
     */
    @JvmOverloads
    @JvmStatic
    fun addImageWatermark(src: Bitmap, watermark: Bitmap, x: Int, y: Int, alpha: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = src.copy(src.config, true)
        if (!isEmptyBitmap(watermark)) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas(ret)
            paint.alpha = alpha
            canvas.drawBitmap(watermark, x.toFloat(), y.toFloat(), paint)
        }
        if (recycle && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 转为alpha位图
     *
     * @param src     源图片
     * @param recycle 是否回收
     * @return alpha位图
     */
    @JvmOverloads
    @JvmStatic
    fun toAlpha(src: Bitmap, recycle: Boolean? = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = src.extractAlpha()
        if (recycle!! && !src.isRecycled) src.recycle()
        return ret
    }

    /**
     * 转为灰度图片
     *
     * @param src     源图片
     * @param recycle 是否回收
     * @return 灰度图
     */
    @JvmOverloads
    @JvmStatic
    fun toGray(src: Bitmap, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val grayBitmap = Bitmap.createBitmap(src.width,
                src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(src, 0f, 0f, paint)
        if (recycle && !src.isRecycled) src.recycle()
        return grayBitmap
    }

    /**
     * 保存图片
     *
     * @param src      源图片
     * @param filePath 要保存到的文件路径
     * @param format   格式
     * @return `true`: 成功<br></br>`false`: 失败
     */
    @JvmStatic
    fun save(src: Bitmap, filePath: String, format: CompressFormat): Boolean {
        return save(src, FileUtils.getFileByPath(filePath), format, false)
    }

    /**
     * 保存图片
     *
     * @param src      源图片
     * @param filePath 要保存到的文件路径
     * @param format   格式
     * @param recycle  是否回收
     * @return `true`: 成功<br></br>`false`: 失败
     */
    @JvmStatic
    fun save(src: Bitmap, filePath: String, format: CompressFormat, recycle: Boolean): Boolean {
        return save(src, FileUtils.getFileByPath(filePath), format, recycle)
    }

    /**
     * 保存图片
     *
     * @param src     源图片
     * @param file    要保存到的文件
     * @param format  格式
     * @param recycle 是否回收
     * @return `true`: 成功<br></br>`false`: 失败
     */
    @JvmOverloads
    @JvmStatic
    fun save(src: Bitmap, file: File?, format: CompressFormat, recycle: Boolean = false): Boolean {
        if (isEmptyBitmap(src) || !FileUtils.createOrExistsFile(file)) return false
        println(src.width.toString() + ", " + src.height)
        var os: OutputStream? = null
        var ret = false
        try {
            os = BufferedOutputStream(FileOutputStream(file!!))
            ret = src.compress(format, 100, os)
            if (recycle && !src.isRecycled) src.recycle()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            CloseUtils.closeIO(os as Closeable)
        }
        return ret
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param file 　文件
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic
    fun isImage(file: File?): Boolean {
        return file != null && isImage(file.path)
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param filePath 　文件路径
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic
    fun isImage(filePath: String): Boolean {
        val path = filePath.toUpperCase()
        return (path.endsWith(".PNG") || path.endsWith(".JPG")
                || path.endsWith(".JPEG") || path.endsWith(".BMP")
                || path.endsWith(".GIF"))
    }

    /**
     * 获取图片类型
     *
     * @param filePath 文件路径
     * @return 图片类型
     */
    @JvmStatic
    fun getImageType(filePath: String): String? {
        return getImageType(FileUtils.getFileByPath(filePath))
    }

    /**
     * 获取图片类型
     *
     * @param file 文件
     * @return 图片类型
     */
    @JvmStatic
    fun getImageType(file: File?): String? {
        if (file == null) return null
        var `is`: InputStream? = null
        try {
            `is` = FileInputStream(file)
            return getImageType(`is`)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(`is` as Closeable)
        }
    }

    /**
     * 流获取图片类型
     *
     * @param is 图片输入流
     * @return 图片类型
     */
    @JvmStatic
    fun getImageType(`is`: InputStream?): String? {
        if (`is` == null) return null
        try {
            val bytes = ByteArray(8)
            return if (`is`.read(bytes, 0, 8) != -1) getImageType(bytes) else null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取图片类型
     *
     * @param bytes bitmap的前8字节
     * @return 图片类型
     */
    @JvmStatic
    fun getImageType(bytes: ByteArray): String? {
        if (isJPEG(bytes)) return "JPEG"
        if (isGIF(bytes)) return "GIF"
        if (isPNG(bytes)) return "PNG"
        return if (isBMP(bytes)) "BMP" else null
    }

    private fun isJPEG(b: ByteArray): Boolean {
        return (b.size >= 2
                && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte())
    }

    private fun isGIF(b: ByteArray): Boolean {
        return (b.size >= 6
                && b[0] == 'G'.toByte() && b[1] == 'I'.toByte()
                && b[2] == 'F'.toByte() && b[3] == '8'.toByte()
                && (b[4] == '7'.toByte() || b[4] == '9'.toByte()) && b[5] == 'a'.toByte())
    }

    private fun isPNG(b: ByteArray): Boolean {
        return b.size >= 8 && (b[0] == 137.toByte() && b[1] == 80.toByte()
                && b[2] == 78.toByte() && b[3] == 71.toByte()
                && b[4] == 13.toByte() && b[5] == 10.toByte()
                && b[6] == 26.toByte() && b[7] == 10.toByte())
    }

    private fun isBMP(b: ByteArray): Boolean {
        return (b.size >= 2
                && b[0].toInt() == 0x42 && b[1].toInt() == 0x4d)
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return `true`: 是<br></br>`false`: 否
     */
    private fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }

    /******************************~~~~~~~~~ 下方和压缩有关 ~~~~~~~~~ */

    /**
     * 按缩放压缩
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放压缩后的图片
     */
    @JvmStatic
    fun compressByScale(src: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        return scale(src, newWidth, newHeight, false)
    }

    /**
     * 按缩放压缩
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放压缩后的图片
     */
    @JvmStatic
    fun compressByScale(src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean): Bitmap? {
        return scale(src, newWidth, newHeight, recycle)
    }

    /**
     * 按缩放压缩
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @return 缩放压缩后的图片
     */
    @JvmStatic
    fun compressByScale(src: Bitmap, scaleWidth: Float, scaleHeight: Float): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, false)
    }

    /**
     * 按缩放压缩
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放压缩后的图片
     */
    @JvmStatic
    fun compressByScale(src: Bitmap, scaleWidth: Float, scaleHeight: Float, recycle: Boolean): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, recycle)
    }

    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun compressByQuality(src: Bitmap, @IntRange(from = 0, to = 100) quality: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, quality, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 按质量压缩
     *
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @param recycle     是否回收
     * @return 质量压缩压缩过的图片
     */
    @JvmOverloads
    @JvmStatic
    fun compressByQuality(src: Bitmap, maxByteSize: Long, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null
        val baos = ByteArrayOutputStream()
        var quality = 100
        src.compress(CompressFormat.JPEG, quality, baos)
        while (baos.toByteArray().size > maxByteSize && quality > 0) {
            baos.reset()
            quality -= 5
            src.compress(CompressFormat.JPEG, quality, baos)
        }
        if (quality < 0) return null
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 按采样大小压缩
     *
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @param recycle    是否回收
     * @return 按采样率压缩后的图片
     */
    @JvmOverloads
    @JvmStatic
    fun compressBySampleSize(src: Bitmap, sampleSize: Int, recycle: Boolean = false): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        val baos = ByteArrayOutputStream()
        src.compress(CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

}
