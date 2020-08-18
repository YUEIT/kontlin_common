package cn.yue.base.common.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import cn.yue.base.common.R
import cn.yue.base.common.image.ImageLoader

/**
 * Description : 等待框
 * Created by yue on 2019/3/11
 */
class WaitDialog(private val activity: Activity) {
    private var dialog: Dialog? = null
    private lateinit var handler: Handler
    private lateinit var waitText: TextView
    private lateinit var waitImage: ImageView

    init {
        init()
    }

    private fun init() {
        handler = Handler()
        dialog = Dialog(activity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setGravity(Gravity.CENTER)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val view = View.inflate(activity, R.layout.layout_wait_dialog, null)
        waitText = view.findViewById(R.id.waitText)
        waitImage = view.findViewById(R.id.waitImage)
        ImageLoader.getLoader().loadGif(waitImage, R.drawable.app_icon_wait)
        dialog!!.setContentView(view)
    }


    /**
     *
     * @param title
     * @param isProgress
     * @param imgRes     显示滚动条的时候该值传递null
     */
    fun show(title: String?, isProgress: Boolean, imgRes: Int?) {
        if (null == dialog) {
            init()
        }
        if (activity.isFinishing) {
            return
        }
        if (null != dialog) {
            if (dialog!!.isShowing) {
                dialog!!.cancel()
            }
            dialog!!.show()
        }
        setDialog(title, isProgress, imgRes)
    }

    private fun setDialog(title: String?, isProgress: Boolean, imgRes: Int?) {
        if (!TextUtils.isEmpty(title)) {
            waitText.text = title
            waitText.visibility = View.VISIBLE
        } else {
            waitText.visibility = View.GONE
        }
        if (isProgress) {
            waitImage.visibility = View.VISIBLE
        } else {
            waitImage.visibility = View.VISIBLE
        }
        if (null != imgRes) {
            waitImage.setBackgroundDrawable(activity.resources.getDrawable(imgRes))
        }
    }

    fun cancel() {
        if (null != dialog && !activity.isFinishing) {
            dialog!!.cancel()
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
        return if (null != dialog) {
            dialog!!.isShowing
        } else false
    }

    fun setCancelable(cancelable: Boolean) {
        if (null != dialog) {
            dialog!!.setCancelable(cancelable)
        }
    }

    interface DelayCancelListener {
        fun onDeal()
    }

}