package cn.yue.base.widget.excel

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * Description :
 * Created by yue on 2020/11/21
 */
class InnerRecyclerView(context: Context, attributeSet: AttributeSet) : RecyclerView(context, attributeSet){

    init {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mListener != null) {
                    mListener?.invoke(recyclerView, dx, dy)
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isTouch = newState != SCROLL_STATE_IDLE
            }
        })
    }

    private var isTouch: Boolean = false
//    override fun onTouchEvent(e: MotionEvent): Boolean {
//        if (e.action == MotionEvent.ACTION_DOWN) {
//            isTouch = true
//        } else if (e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_CANCEL) {
//            isTouch = false
//        }
//        return super.onTouchEvent(e)
//    }

    private var mListener: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)? = null

    fun setOnScrollListener(mListener: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)) {
        this.mListener = mListener
    }

    fun fastScrollBy(dx: Int, dy: Int) {
        if (!isTouch) {
            scrollBy(dx, dy)
        }
    }
}