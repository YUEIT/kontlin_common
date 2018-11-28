package cn.yue.base.common.image

import android.graphics.drawable.Drawable
import android.widget.ImageView
import cn.yue.base.common.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class GlideImageLoader : ImageLoader.Loader {

    private val requestOptions: RequestOptions
        get() = RequestOptions()
                .placeholder(R.drawable.drawable_default)
                .error(R.drawable.drawable_default)

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
        Glide.with(imageView.context)
                .load(url)
                .apply(if (fitCenter) requestOptions.fitCenter() else requestOptions.centerCrop())
                .into(imageView)
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
        Glide.with(imageView.context)
                .load(resId)
                .apply(if (fitCenter) requestOptions.fitCenter() else requestOptions.centerCrop())
                .into(imageView)
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
        Glide.with(imageView.context)
                .load(drawable)
                .apply(if (fitCenter) requestOptions.fitCenter() else requestOptions.centerCrop())
                .into(imageView)
    }

    override fun loadGif(imageView: ImageView?, url: String?) {
        if (imageView == null) {
            return
        }
        val requestOptions = requestOptions
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .dontAnimate()
        Glide.with(imageView.context)
                .asGif()
                .load(url)
                .apply(requestOptions)
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView?, url: String?, radius: Int) {

    }

    override fun loadCircleImage(imageView: ImageView?, url: String?) {

    }

}
