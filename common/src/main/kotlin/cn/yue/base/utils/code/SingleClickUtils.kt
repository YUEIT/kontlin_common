package cn.yue.base.utils.code

import android.view.View
import kotlin.math.abs

abstract class OnSingleClickListener : View.OnClickListener {

    override fun onClick(view: View) {
        if (abs(System.currentTimeMillis() - lastClickTime) > getIntervalClickTime()) {
            lastClickTime = System.currentTimeMillis()
            onSingleClick(view)
        }
    }

    open fun getIntervalClickTime(): Long {
        return mIntervalClickTime
    }

    /**
     * 处理点击事件
     */
    abstract fun onSingleClick(v: View?)

    //单击判断生效时间，以此时间判断生效
     private val mIntervalClickTime: Long = 500

    //上次点击的时间
    private var lastClickTime: Long = 0

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

fun View.setOnSingleClickListener(intervalTime: Long, block: (() -> Unit)?) {
    if (block != null) {
        this.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                block.invoke()
            }
    
            override fun getIntervalClickTime(): Long {
                return intervalTime
            }
        })
    } else {
        this.setOnClickListener(null)
    }
}