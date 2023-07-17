package cn.yue.base.binding.recycler

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

object ViewAdapter {

    @BindingAdapter(value = ["adapter"])
    @JvmStatic
    fun setAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        recyclerView.adapter = adapter
    }

    @BindingAdapter(value = ["layoutManager", "linearLayoutManager", "gridLayoutManager",
        "staggeredGridLayoutManager", "spanCount"], requireAll = false)
    @JvmStatic
    fun setLayoutManager(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager?,
                         linearLayoutManager: String?, gridLayoutManager: String?, staggeredGridLayoutManager: String?, spanCount: Int?) {
        var mLayoutManager: RecyclerView.LayoutManager? = null
        if (layoutManager != null) {
            mLayoutManager = layoutManager
        }
        if (linearLayoutManager != null) {
            if (("horizontal").contains(linearLayoutManager)) {
                mLayoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
            } else if ("vertical".contains(linearLayoutManager)) {
                mLayoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
            }
        }
        if (gridLayoutManager != null && spanCount != null) {
            if ("horizontal".contains(gridLayoutManager)) {
                mLayoutManager = GridLayoutManager(recyclerView.context, spanCount, GridLayoutManager.HORIZONTAL, false)
            } else if ("vertical".contains(gridLayoutManager)) {
                mLayoutManager = GridLayoutManager(recyclerView.context, spanCount, GridLayoutManager.VERTICAL, false)
            }
        }
        if (staggeredGridLayoutManager != null && spanCount != null) {
            if (("horizontal").contains(staggeredGridLayoutManager)) {
                mLayoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL)
            } else if ("vertical".contains(staggeredGridLayoutManager)) {
                mLayoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            }
        }
        if (mLayoutManager != null) {
            recyclerView.layoutManager = mLayoutManager
        }
    }

    @BindingAdapter(value = ["nestedScrollingEnabled"])
    @JvmStatic
    fun setAdapter(recyclerView: RecyclerView, enable: Boolean) {
        recyclerView.isNestedScrollingEnabled = enable
    }
}