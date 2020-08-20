package cn.yue.base.common.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes

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
            mLoader?.clearCache()
            return mLoader as Loader
        }
    }

    interface Loader {

        fun loadImage(imageView: ImageView?, url: String?)

        fun loadImage(imageView: ImageView?, url: String?, fitCenter: Boolean)

        fun loadImage(imageView: ImageView?, @DrawableRes resId: Int)

        fun loadImage(imageView: ImageView?, @DrawableRes resId: Int, fitCenter: Boolean)

        fun loadImage(imageView: ImageView?, drawable: Drawable?)

        fun loadImage(imageView: ImageView?, drawable: Drawable?, fitCenter: Boolean)

        fun loadImage(imageView: ImageView?, uri: Uri?)

        fun loadImage(imageView: ImageView?, uri: Uri?, fitCenter: Boolean)

        fun loadGif(imageView: ImageView?, url: String?)

        fun loadGif(imageView: ImageView?, @DrawableRes resId: Int)

        fun loadRoundImage(imageView: ImageView?, url: String?, radius: Int)

        fun loadCircleImage(imageView: ImageView?, url: String?)

        fun loadAsBitmap(context: Context, url: String?, onLoaded: (bitmap: Bitmap) -> Unit, noFound: (() -> Unit)? = null)

        fun setPlaceholder(@DrawableRes resId: Int): Loader

        fun loadImageNoCache(imageView: ImageView?, url: String?)

        fun loadImageNoCache(imageView: ImageView?, uri: Uri?)

        fun clearCache()

    }
}