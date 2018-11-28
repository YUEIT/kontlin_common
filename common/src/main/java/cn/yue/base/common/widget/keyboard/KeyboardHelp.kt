package cn.yue.base.common.widget.keyboard

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver

/**
 * Description :
 * Created by yue on 2018/11/14
 */
class KeyboardHelp {

    var keyboardHeight: Int = 0
        private set
    private var maxDisplayHeight: Int = 0
    internal var isVisibleForLast = false

    fun addOnSoftKeyBoardVisibleListener(context: Context, iKeyboard: IKeyboard) {
        val decorView = (context as Activity).window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                decorView.getWindowVisibleDisplayFrame(rect)
                //获得屏幕整体的高度
                if (maxDisplayHeight < rect.bottom) {
                    maxDisplayHeight = rect.bottom
                }
                //获得键盘高度
                val keyboardHeight = maxDisplayHeight - rect.bottom
                val visible = keyboardHeight.toDouble() != 0.0
                if (visible != isVisibleForLast) {
                    this@KeyboardHelp.keyboardHeight = keyboardHeight
                    if (visible) {
                        iKeyboard.onKeyboardOpen()
                    } else {
                        iKeyboard.onKeyboardClose()
                    }
                    isVisibleForLast = visible
                }
            }
        })
    }

}
