package cn.yue.base.common.image

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import cn.yue.base.common.R
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.security.MessageDigest

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class GlideImageLoader : ImageLoader.Loader {

    override fun loadImage(imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        loadImage(imageView, url, false)
    }

    override fun loadImage(imageView: ImageView?, url: String?, fitCenter: Boolean) {
        if (imageView == null) {
            return
        }
        realLoadImage(imageView, url, 0, null, null, fitCenter)
    }

    override fun loadImage(imageView: ImageView?, resId: Int) {
        if (imageView == null) {
            return
        }
        loadImage(imageView, resId, false)
    }

    override fun loadImage(imageView: ImageView?, resId: Int, fitCenter: Boolean) {
        if (imageView == null) {
            return
        }
        realLoadImage(imageView, null, resId, null, null, fitCenter)
    }

    override fun loadImage(imageView: ImageView?, drawable: Drawable?) {
        if (imageView == null) {
            return
        }
        loadImage(imageView, drawable, false)
    }

    override fun loadImage(imageView: ImageView?, drawable: Drawable?, fitCenter: Boolean) {
        if (imageView == null) {
            return
        }
        realLoadImage(imageView, null, 0, drawable, null, fitCenter)
    }

    override fun loadImage(imageView: ImageView?, uri: Uri?) {
        if (imageView == null) {
            return
        }
        loadImage(imageView, uri, false)
    }

    override fun loadImage(imageView: ImageView?, uri: Uri?, fitCenter: Boolean) {
        if (imageView == null) {
            return
        }
        realLoadImage(imageView, null, 0, null, uri, false)
    }

    private fun realLoadImage(imageView: ImageView?, url: String?, resId: Int, drawable: Drawable?, uri: Uri?, fitCenter: Boolean) {
        if (imageView == null) {
            return
        }
        var requestBuilder: RequestBuilder<*>? = null
        if (!TextUtils.isEmpty(url)) {
            if (isGift(url!!)) {
                loadGif(imageView, url)
                return
            }
            requestBuilder = Glide.with(imageView.context).load(url)
        }
        if (resId > 0) {
           requestBuilder = Glide.with(imageView.context).load(resId)
        }
        if (drawable != null) {
            requestBuilder = Glide.with(imageView.context).load(drawable)
        }
        if (uri != null) {
            requestBuilder = Glide.with(imageView.context).load(uri)
        }
        if (requestBuilder == null) {
            return
        }
        val requestOptions = if (fitCenter) getRequestOptions().fitCenter() else getRequestOptions().centerCrop()
        requestBuilder.apply(requestOptions).into(imageView)
    }

    private fun isGift(url: String) : Boolean {
        if (url.endsWith("gif")) {
            return true
        }
        return false
    }

    private var placeholderResId: Int = 0

    override fun setPlaceholder(resId: Int): ImageLoader.Loader {
        placeholderResId = resId
        return this
    }

    private fun getRequestOptions() : RequestOptions {
        return RequestOptions()
                .placeholder(placeholderResId)
                .error(placeholderResId)
    }

    override fun loadGif(imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        Glide.with(imageView.context)
                .asGif()
                .load(url)
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView?, url: String?, radius: Int) {
        if (imageView == null) {
            return
        }
        Glide.with(imageView.context)
                .load(url)
                .apply(getRequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(GlideRoundTransform(radius))
                        .dontAnimate())
                .into(imageView)

    }

    override fun loadCircleImage(imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        Glide.with(imageView.context)
                .load(url)
                .apply(getRequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(GlideCircleTransform())
                        .dontAnimate())
                .into(imageView)
    }

    override fun loadGif(imageView: ImageView?, resId: Int) {
        if (imageView == null) {
            return
        }
        Glide.with(imageView.context)
                .asGif()
                .load(resId)
                .into(imageView)
    }

    override fun loadAsBitmap(context: Context, url: String?, callBack: LoadBitmapCallBack) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object :SimpleTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        callBack.onBitmapLoaded(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        callBack.onBitmapNoFound()
                    }
                })
    }

    override fun loadImageNoCache(imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        Glide.with(imageView.context)
                .load(url)
                .apply(getRequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .dontAnimate())
                .into(imageView)
    }

    override fun loadImageNoCache(imageView: ImageView?, uri: Uri?) {
        if (imageView == null) {
            return
        }
        Glide.with(imageView.context)
                .load(uri)
                .apply(getRequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .dontAnimate())
                .into(imageView)
    }

    override fun clearCache() {
        placeholderResId = R.drawable.drawable_default
    }

    class GlideRoundTransform(var dp: Int) : BitmapTransformation() {

        constructor(): this(4)

        var radius: Float = Resources.getSystem().displayMetrics.density * dp

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {

        }

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
            val bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)
            return roundCrop(pool, bitmap)
        }

        private fun roundCrop(pool: BitmapPool, source: Bitmap) : Bitmap? {
            var result: Bitmap? = pool.get(source.width, source.height, Bitmap.Config.ARGB_8888)
            if (result == null) {
                result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(result!!)
            val paint = Paint()
            paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.isAntiAlias = true
            val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
            canvas.drawRoundRect(rectF, radius, radius, paint)
            return result
        }

    }

    class GlideCircleTransform : BitmapTransformation {

        private var mBorderWidth: Float? = 0f
        private var mBorderPaint: Paint? = null

        constructor() : super() {
            mBorderPaint = Paint()
        }

        constructor(borderWidth: Int, borderColor: Int) : super() {
            mBorderWidth = Resources.getSystem().displayMetrics.density * borderWidth
            mBorderPaint = Paint()
            mBorderPaint!!.isDither = true
            mBorderPaint!!.isAntiAlias = true
            mBorderPaint!!.color = borderColor
            mBorderPaint!!.style = Paint.Style.STROKE
            mBorderPaint!!.strokeWidth = mBorderWidth!!
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {

        }

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
            var bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)
            return circleCrop(pool, bitmap)
        }

        private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
            if (source == null) return null

            val size = (Math.min(source.width, source.height) - mBorderWidth!! / 2).toInt()
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2
            val squared = Bitmap.createBitmap(source, x, y, size, size)
            var result: Bitmap? = pool.get(size, size, Bitmap.Config.ARGB_8888)
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(result!!)
            val paint = Paint()
            paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            if (mBorderPaint != null) {
                val borderRadius = r - mBorderWidth!!/2f
                canvas.drawCircle(r, r, borderRadius, mBorderPaint!!)
            }
            return result
        }

    }
}
