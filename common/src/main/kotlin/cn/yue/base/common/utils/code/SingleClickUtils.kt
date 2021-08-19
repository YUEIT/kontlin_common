package cn.yue.base.common.utils.code

import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {
    override fun onClick(view: View) {
        if (System.currentTimeMillis() - lastClickTime > getIntervalClickTime()) {
            lastClickTime = System.currentTimeMillis()
            onSingleClick(view)
        }
    }

    fun getIntervalClickTime(): Long {
        return intervalClickTime
    }

    /**
     * 处理点击事件
     */
    abstract fun onSingleClick(v: View?)

    companion object {
        //单击判断生效时间，以此时间判断生效
        protected const val intervalClickTime: Long = 500

        //上次点击的时间
        private var lastClickTime: Long = 0
    }
}

fun View.setOnSingleClickListener(block: (() -> Unit)?) {
    if (block != null) {
        this.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                block.invoke()
            }
        })
    } else {
        this.setOnClickListener(null)
    }
}