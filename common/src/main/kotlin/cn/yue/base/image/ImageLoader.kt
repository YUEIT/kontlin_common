package cn.yue.base.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView

/**
 * Description :
 * Created by yue on 2018/11/15
 */

class ImageLoader {

    companion object {
        private var mLoader: Loader? = null

        @JvmStatic
        fun getLoader(): Loader {
            if (mLoader == null) {
                mLoader = GlideImageLoader()
            }
            return mLoader as Loader
        }
        
        fun ImageView.with() {
            getLoader().with(this)
        }
        
        fun ImageView.loadImage(url: String?) {
            getLoader().with(this).setUrl(url).loadImage()
        }
        
        fun ImageView.loadImage(uri: Uri?) {
            getLoader().with(this).setUri(uri).loadImage()
        }
        
        fun ImageView.loadImage(resId: Int) {
            getLoader().with(this).setResId(resId).loadImage()
        }
        
        fun ImageView.loadImage(drawable: Drawable?) {
            getLoader().with(this).setDrawable(drawable).loadImage()
        }
    }

    interface Loader {
        fun with(imageView: ImageView): Builder

        fun loadAsBitmap(context: Context, url: String?, onLoaded: (bitmap: Bitmap) -> Unit, noFound: (() -> Unit)? = null)

    }
    
    interface Builder {
        fun setFitCenter(fitCenter: Boolean): Builder
        
        fun setUrl(url: String?): Builder
        
        fun setUri(uri: Uri?): Builder
        
        fun setDrawable(drawable: Drawable?): Builder
        
        fun setResId(resId: Int): Builder
        
        fun setRadius(radius: Int): Builder
        
        fun setPlaceholderResId(placeholderResId: Int): Builder
        
        fun setSkipMemoryCache(skipMemoryCache: Boolean): Builder
        
        fun loadImage()
    }
}