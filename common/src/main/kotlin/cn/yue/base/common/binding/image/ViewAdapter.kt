package cn.yue.base.common.binding.image

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cn.yue.base.common.image.ImageLoader
import cn.yue.base.common.utils.code.hasValue

object ViewAdapter {

    @BindingAdapter(value = ["url", "imageRes"], requireAll = false)
    @JvmStatic
    fun setImageResource(imageView: ImageView, url: String?, imageRes: Int) {
        if (url.hasValue()) {
            ImageLoader.getLoader().loadImage(imageView, url)
            return
        }
        if (imageRes != 0) {
            ImageLoader.getLoader().loadImage(imageView, imageRes)
            return
        }
    }
}