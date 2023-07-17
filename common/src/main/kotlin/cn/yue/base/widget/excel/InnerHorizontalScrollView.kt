package cn.yue.base.widget.excel

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView

/**
 * Description :
 * Created by yue on 2020/11/21
 */
class InnerHorizontalScrollView(context: Context, attributeSet: AttributeSet) : HorizontalScrollView(context, attributeSet){

    private var mListener: ((view: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) -> Unit)? = null

    fun setOnScrollChangeListener(mListener: ((view: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) -> Unit)) {
        this.mListener = mListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mListener != null) {
            mListener!!(this, l, t, oldl, oldt)
        }
    }

    private var isTouch: Boolean = false
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            isTouch = true
        } else if (e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_CANCEL) {
            isTouch = false
        }
        return super.onTouchEvent(e)
    }

    fun fastScrollTo(x: Int, y: Int) {
        if (!isTouch) {
            scrollTo(x, y)
        }
    }
}