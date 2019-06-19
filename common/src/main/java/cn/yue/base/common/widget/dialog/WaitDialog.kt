package cn.yue.base.common.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import cn.yue.base.common.R
import cn.yue.base.common.image.ImageLoader

/**
 * Description :
 * Created by yue on 2019/6/19
 */
class WaitDialog(var activity: Activity) {

    private var dialog: Dialog = Dialog(activity)
    private var handler: Handler = Handler()
    private var image: ImageView? = null
    private var titleText: TextView? = null

    private fun init() {
        if (null != activity) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setGravity(Gravity.CENTER)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            val view = (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.layout_wait_dialog, null)
            image = view.findViewById(R.id.waitDialogIV)
            ImageLoader.getLoader().loadGif(image, R.drawable.app_icon_wait)
            titleText = view.findViewById(R.id.waitDialogTV)
            dialog.setContentView(view)
            //            progressBar.startAnimation();
        }
    }

    /**
     *
     * @param title
     * @param isProgress
     * @param imgRes     显示滚动条的时候该值传递null
     */
    fun show(title: String, isProgress: Boolean, imgRes: Int?) {
        if (null == dialog) {
            init()
        }
        if (null != activity && activity.isFinishing) {
            return
        }

        if (null != dialog) {
            if (dialog.isShowing) {
                dialog.cancel()
            }
            dialog.show()
        }
        setDialog(title, isProgress, imgRes)
    }

    private fun setDialog(title: String, isProgress: Boolean, imgRes: Int?) {
        if (null != titleText && !TextUtils.isEmpty(title)) {
            titleText!!.setText(title)
            titleText!!.setVisibility(View.VISIBLE)
        } else {
            titleText!!.setVisibility(View.GONE)
        }
        if (null != image) {
            if (isProgress) {
                image!!.setVisibility(View.VISIBLE)
            } else {
                image!!.setVisibility(View.VISIBLE)
            }
        }
        if (null != imgRes && null != image && null != activity) {
            image!!.setBackgroundDrawable(activity.resources.getDrawable(imgRes))
        }
    }


    fun cancel() {
        if (null != dialog && null != activity && !activity.isFinishing) {
            dialog.cancel()
        }
    }

    fun delayCancel(time: Int) {
        if (null != handler) {
            handler.postDelayed(Runnable { cancel() }, time.toLong())
        }
    }

    fun delayCancel(time: Int, listener: DelayCancelListener?) {
        if (null != handler) {
            handler.postDelayed(Runnable {
                cancel()
                listener?.onDeal()
            }, time.toLong())
        }
    }

    fun isShowing(): Boolean {
        return if (null != dialog) {
            dialog.isShowing
        } else false
    }

    fun setCancelable(cancelable: Boolean) {
        if (null != dialog) {
            dialog.setCancelable(cancelable)
        }
    }

    interface DelayCancelListener {
        fun onDeal()
    }
}