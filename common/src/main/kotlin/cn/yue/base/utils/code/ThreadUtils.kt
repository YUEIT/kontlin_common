package cn.yue.base.utils.code

import android.os.Handler
import android.os.Looper

object ThreadUtils {

    private val HANDLER = Handler(Looper.getMainLooper())

    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            HANDLER.post(runnable)
        }
    }

    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        HANDLER.postDelayed(runnable, delayMillis)
    }
}