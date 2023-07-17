package cn.yue.base.photo.preview

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * Description :
 * Created by yue on 2019/3/11
 */
class PhotoViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    override fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return if (v is PhotoView) {
            v.canScroll(dx) || super.canScroll(v, checkV, dx, x, y)
        } else super.canScroll(v, checkV, dx, x, y)
    }

    fun setCurrentPhotoView(currentPhotoView: PhotoView?, position: Int, s: String?, uri: Uri?) {
        currentPhotoView?.setOnClickListener {
            onItemClickListener?.invoke(position, s, uri)
        }
    }

    private var onItemClickListener: ((position: Int, s: String?, uri: Uri?) -> Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((position: Int, s: String?, uri: Uri?) -> Unit)? = null) {
        this.onItemClickListener = onItemClickListener
    }

}