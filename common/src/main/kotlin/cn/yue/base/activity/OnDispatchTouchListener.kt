package cn.yue.base.activity

import android.view.MotionEvent

interface OnDispatchTouchListener {
    fun dispatchTouchEvent(ev: MotionEvent)
}