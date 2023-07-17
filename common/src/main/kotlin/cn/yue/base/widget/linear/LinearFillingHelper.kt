package cn.yue.base.widget.linear

import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL

/**
 * Description : LinearLayout简单布局工具
 * Created by yue on 2020/7/31
 */
class LinearFillingHelper(private val linearLayout: LinearLayout) {
    private lateinit var adapter: Adapter

    fun setAdapter(adapter: Adapter) {
        this.adapter = adapter
        adapter.injectionFillingHelper(this)
        fillLayout()
    }

    private fun fillLayout() {
        linearLayout.removeAllViews()
        for (i in 0 until adapter.getItemCount()) {
            val child = View.inflate(linearLayout.context, adapter.getLayoutId(), null)
            val width = if (linearLayout.orientation == HORIZONTAL) LinearLayout.LayoutParams.WRAP_CONTENT else LinearLayout.LayoutParams.MATCH_PARENT
            val height = if (linearLayout.orientation == HORIZONTAL) LinearLayout.LayoutParams.MATCH_PARENT else LinearLayout.LayoutParams.WRAP_CONTENT
            val param =  LinearLayout.LayoutParams(width, height)
            if (adapter.shouldExpand()) {
                param.weight = 1f
            } else {
                param.weight = 0f
            }
            linearLayout.addView(child, param)
            adapter.bindView(child, i)
        }
    }

    private fun notifyLayout() {
        if (linearLayout.childCount != adapter.getItemCount()) {
            fillLayout()
            return
        }
        for (i in 0 until linearLayout.childCount) {
            val child = linearLayout.getChildAt(i)
            adapter.bindView(child, i)
        }
    }

    abstract class Adapter {
        open fun shouldExpand(): Boolean = false
        abstract fun getItemCount(): Int
        abstract fun getLayoutId(): Int
        abstract fun bindView(contentView: View, position: Int)
        private var fillingHelper: LinearFillingHelper? = null
        fun injectionFillingHelper(fillingHelper: LinearFillingHelper?) {
            this.fillingHelper = fillingHelper
        }

        fun notifyDataSetChanged() {
            fillingHelper?.notifyLayout()
        }
    }

    abstract class SimpleAdapter<T> : Adapter {
        private val dataList = ArrayList<T>()

        constructor()

        constructor(dataList: List<T>?) {
            if (dataList != null) {
                this.dataList.addAll(dataList)
            }
        }

        fun setData(dataList: List<T>?) {
            this.dataList.clear()
            if (dataList != null) {
                this.dataList.addAll(dataList)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun bindView(contentView: View, position: Int) {
            if (position < dataList.size) {
                bindView(contentView, position, dataList[position])
            }
        }

        abstract fun bindView(contentView: View, position: Int, itemData: T)
    }
}