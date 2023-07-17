package cn.yue.base.widget.viewpager

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HeaderScrollHelper {

    private val sysVersion: Int = Build.VERSION.SDK_INT         //当前sdk版本，用于判断api版本
    private var mCurrentScrollableContainer: ScrollableContainer? = null

    val scrollableView: View?
        get() = if (mCurrentScrollableContainer == null) null else mCurrentScrollableContainer!!.scrollableView

    /**
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     */
    val isTop: Boolean
        get() {
            val scrollableView = scrollableView
                    ?: throw NullPointerException("You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.")
            if (scrollableView is AdapterView<*>) {
                return isAdapterViewTop(scrollableView)
            }
            if (scrollableView is ScrollView) {
                return isScrollViewTop(scrollableView)
            }
            if (scrollableView is RecyclerView) {
                return isRecyclerViewTop(scrollableView)
            }
            if (scrollableView is WebView) {
                return isWebViewTop(scrollableView)
            }
            throw IllegalStateException("scrollableView must be a instance of AdapterView|ScrollView|RecyclerView")
        }

    /** 包含有 ScrollView ListView RecyclerView 的组件  */
    interface ScrollableContainer {

        /** @return ScrollView ListView RecyclerView 或者其他的布局的实例
         */
        val scrollableView: View
    }

    fun setCurrentScrollableContainer(scrollableContainer: ScrollableContainer) {
        this.mCurrentScrollableContainer = scrollableContainer
    }

    private fun isRecyclerViewTop(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val childAt = recyclerView.getChildAt(0)
                if (childAt == null || firstVisibleItemPosition == 0 && childAt.top >= layoutManager.getTopDecorationHeight(childAt)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isAdapterViewTop(adapterView: AdapterView<*>?): Boolean {
        if (adapterView != null) {
            val firstVisiblePosition = adapterView.firstVisiblePosition
            val childAt = adapterView.getChildAt(0)
            if (childAt == null || firstVisiblePosition == 0 && childAt.top == 0) {
                return true
            }
        }
        return false
    }

    private fun isScrollViewTop(scrollView: ScrollView?): Boolean {
        if (scrollView != null) {
            val scrollViewY = scrollView.scrollY
            return scrollViewY <= 0
        }
        return false
    }

    private fun isWebViewTop(scrollView: WebView?): Boolean {
        if (scrollView != null) {
            val scrollViewY = scrollView.scrollY
            return scrollViewY <= 0
        }
        return false
    }

    /**
     * 将特定的view按照初始条件滚动
     *
     * @param velocityY 初始滚动速度
     * @param distance  需要滚动的距离
     * @param duration  允许滚动的时间
     */
    @SuppressLint("NewApi")
    fun smoothScrollBy(velocityY: Int, distance: Int, duration: Int) {
        val scrollableView = scrollableView
        if (scrollableView is AbsListView) {
            val absListView = scrollableView as AbsListView?
            if (sysVersion >= 21) {
                absListView!!.fling(velocityY)
            } else {
                absListView!!.smoothScrollBy(distance, duration)
            }
        } else (scrollableView as? ScrollView)?.fling(velocityY)
                ?: ((scrollableView as? RecyclerView)?.fling(0, velocityY)
                        ?: if (scrollableView is WebView) {
                            scrollableView.flingScroll(0, velocityY)
                        })
    }
}

