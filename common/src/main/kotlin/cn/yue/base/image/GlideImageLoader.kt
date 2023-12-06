package cn.yue.base.image

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.widget.ImageView
import cn.yue.base.R
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference
import java.security.MessageDigest

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class GlideImageLoader : ImageLoader.Loader {
    
    inner class GlideImageLoaderBuilder: ImageLoader.Builder {
    
        private var weakImageView: WeakReference<ImageView>? = null
        private var fitCenter: Boolean = false
        private var url: String? = null
        private var uri: Uri? = null
        private var drawable: Drawable? = null
        private var resId: Int = 0
        private var radius: Int = 0
        private var placeholderResId: Int = 0
        private var skipMemoryCache: Boolean = false
        
        fun setImageView(imageView: ImageView): ImageLoader.Builder {
            weakImageView = WeakReference(imageView)
            return this
        }
        override fun setFitCenter(fitCenter: Boolean): ImageLoader.Builder {
            this.fitCenter = fitCenter
            return this
        }
        
        override fun setUrl(url: String?): ImageLoader.Builder {
            this.url = url
            return this
        }
        
        override fun setUri(uri: Uri?): ImageLoader.Builder {
            this.uri = uri
            return this
        }
        
        override fun setDrawable(drawable: Drawable?): ImageLoader.Builder {
            this.drawable = drawable
            return this
        }
        
        override fun setResId(resId: Int): ImageLoader.Builder {
            this.resId = resId
            return this
        }
        
        override fun setRadius(radius: Int): ImageLoader.Builder {
            this.radius = radius
            return this
        }
        
        override fun setPlaceholderResId(placeholderResId: Int): ImageLoader.Builder {
            this.placeholderResId = placeholderResId
            return this
        }
        
        override fun setSkipMemoryCache(skipMemoryCache: Boolean): ImageLoader.Builder {
            this.skipMemoryCache = skipMemoryCache
            return this
        }
        
        override fun loadImage() {
            val imageView = weakImageView?.get() ?: return
            realLoadImage(imageView, placeholderResId, url, resId, drawable, uri,
                fitCenter, skipMemoryCache, radius)
        }
    }
    
    override fun with(imageView: ImageView): ImageLoader.Builder {
        return GlideImageLoaderBuilder().setImageView(imageView)
    }
    
    @SuppressLint("CheckResult")
    private fun realLoadImage(imageView: ImageView?,
                              placeholderResId: Int,
                              url: String?,
                              resId: Int,
                              drawable: Drawable?,
                              uri: Uri?,
                              fitCenter: Boolean,
                              skipMemoryCache: Boolean,
                              radius: Int
    ) {
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
        val requestOptions = RequestOptions()
        if (placeholderResId != 0) {
            requestOptions.placeholder(placeholderResId)
                .error(placeholderResId)
        } else {
            requestOptions.placeholder(R.drawable.drawable_default)
                .error(R.drawable.drawable_default)
        }
        if (fitCenter) {
            requestOptions.fitCenter()
        } else {
            requestOptions.centerCrop()
        }
        if (skipMemoryCache) {
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
        } else {
            requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        }
        if (radius > 0) {
            requestOptions.transform(GlideRoundTransform(radius))
                .dontAnimate()
        }
        requestBuilder.apply(requestOptions).into(imageView)
    }

    private fun isGift(url: String) : Boolean {
        if (url.endsWith("gif")) {
            return true
        }
        return false
    }
    
    private fun loadGif(imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        Glide.with(imageView.context)
            .asGif()
            .load(url)
            .into(imageView)
    }

    override fun loadAsBitmap(context: Context, url: String?, onLoaded: (bitmap: Bitmap) -> Unit, noFound: (() -> Unit)?) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        onLoaded(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        noFound?.invoke()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
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
