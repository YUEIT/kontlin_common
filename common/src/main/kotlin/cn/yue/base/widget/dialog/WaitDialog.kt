package cn.yue.base.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import cn.yue.base.R

/**
 * Description : 等待框
 * Created by yue on 2019/3/11
 */
class WaitDialog(private val activity: Activity) {

    private var dialog: Dialog = Dialog(activity)
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var waitText: TextView

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.CENTER)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        val view = View.inflate(activity, R.layout.layout_wait_dialog, null)
        waitText = view.findViewById(R.id.waitText)
        dialog.setContentView(view)
    }

    fun show() {
        if (activity.isFinishing) {
            return
        }
        if (dialog.isShowing) {
            dialog.cancel()
        }
        dialog.show()
    }


    fun show(title: String?) {
        if (activity.isFinishing) {
            return
        }
        if (dialog.isShowing) {
            dialog.cancel()
        }
        dialog.show()
        setDialog(title)
    }

    private fun setDialog(title: String?) {
        if (!TextUtils.isEmpty(title)) {
            waitText.text = title
            waitText.visibility = View.VISIBLE
        } else {
            waitText.visibility = View.GONE
        }
    }

    fun cancel() {
        if (!activity.isFinishing) {
            dialog.cancel()
        }
    }

    fun delayCancel(time: Int) {
        handler.postDelayed({ cancel() }, time.toLong())
    }

    fun delayCancel(time: Int, listener: DelayCancelListener?) {
        handler.postDelayed({
            cancel()
            listener?.onDeal()
        }, time.toLong())
    }

    fun isShowing(): Boolean {
        return dialog.isShowing
    }

    fun setCancelable(cancelable: Boolean) {
        dialog.setCancelable(cancelable)
    }

    interface DelayCancelListener {
        fun onDeal()
    }

}