package cn.yue.base.common.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
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

        fun loadGif(imageView: ImageView?, url: String?)

        fun loadGif(imageView: ImageView?, @DrawableRes resId: Int)

        fun loadRoundImage(imageView: ImageView?, url: String?, radius: Int)

        fun loadCircleImage(imageView: ImageView?, url: String?)

        fun loadAsBitmap(context: Context, url: String?, callBack: LoadBitmapCallBack)

        fun setPlaceholder(@DrawableRes resId: Int): Loader

        fun loadImageNoCache(imageView: ImageView?, url: String?)

        fun clearCache()

    }
}