package cn.yue.base.common.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.ColorInt
import cn.yue.base.common.R
import kotlinx.android.synthetic.main.layout_hint_dialog.*

/**
 * Description :
 * Created by yue on 2019/6/17
 */
class HintDialog: Dialog {

    private var mContext: Context
    constructor(context: Context): super(context) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        window?.setWindowAnimations(R.style.FadeAnimation)
        val content: View = View.inflate(mContext, R.layout.layout_hint_dialog, null)
        setContentView(content)
        setCanceledOnTouchOutside(true)
    }

    class Builder(private var context: Context?) {
        private var titleStr: String? = null
        private var titleColor: Int = 0
        private var isShowTitle = true
        private var contentStr: String? = null
        private var contentColor: Int = 0
        private var contentView: View? = null
        private var leftClickStr: String? = null
        private var leftColor: Int = 0
        private var rightClickStr: String? = null
        private var rightColor: Int = 0
        private var isSingleClick: Boolean = false
        private var onLeftClickListener: ((view: View) -> Unit)? = null
        private var onRightClickListener: ((view: View) -> Unit)? = null

        private var hintDialog: HintDialog? = null

        fun setContext(context: Context): Builder {
            this.context = context
            return this
        }

        fun setTitleStr(titleStr: String): Builder {
            this.titleStr = titleStr
            return this
        }

        fun setShowTitle(showTitle: Boolean): Builder {
            isShowTitle = showTitle
            return this
        }

        fun setContentStr(contentStr: String): Builder {
            this.contentStr = contentStr
            return this
        }

        fun setContentView(contentView: View): Builder {
            this.contentView = contentView
            return this
        }

        fun setLeftClickStr(leftClickStr: String): Builder {
            this.leftClickStr = leftClickStr
            return this
        }

        fun setRightClickStr(rightClickStr: String): Builder {
            this.rightClickStr = rightClickStr
            return this
        }

        fun setSingleClick(singleClick: Boolean): Builder {
            isSingleClick = singleClick
            return this
        }

        fun setOnLeftClickListener(onLeftClickListener: (view: View) -> Unit): Builder {
            this.onLeftClickListener = onLeftClickListener
            return this
        }

        fun setOnRightClickListener(onRightClickListener: (view: View) -> Unit): Builder {
            this.onRightClickListener = onRightClickListener
            return this
        }

        fun setTitleColor(@ColorInt titleColor: Int): Builder {
            this.titleColor = titleColor
            return this
        }

        fun setContentColor(@ColorInt contentColor: Int): Builder {
            this.contentColor = contentColor
            return this
        }

        fun setLeftColor(@ColorInt leftColor: Int): Builder {
            this.leftColor = leftColor
            return this
        }

        fun setRightColor(@ColorInt rightColor: Int): Builder {
            this.rightColor = rightColor
            return this
        }

        fun build(): HintDialog {
            if (context == null) {
                throw NullPointerException("context is null")
            }
            if (hintDialog == null) {
                hintDialog = HintDialog(context!!)
            }
            if (isShowTitle) {
                hintDialog!!.titleTV.text = titleStr
                hintDialog!!.titleTV.visibility = View.VISIBLE
            } else {
                hintDialog!!.titleTV.visibility = View.GONE
            }
            if (titleColor > 0) {
                hintDialog!!.titleTV.setTextColor(titleColor)
            }
            if (contentView == null) {
                if (!TextUtils.isEmpty(contentStr)) {
                    val contentTV = TextView(context)
                    contentTV.setTextColor(Color.parseColor("#9b9b9b"))
                    contentTV.gravity = Gravity.CENTER
                    contentTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                    contentTV.text = contentStr
                    if (contentColor > 0) {
                        contentTV.setTextColor(contentColor)
                    }
                    hintDialog!!.contentLL.addView(contentTV)
                }
            } else {
                hintDialog!!.contentLL.addView(contentView)
            }
            hintDialog!!.leftClickTV.text = leftClickStr
            if (leftColor > 0) {
                hintDialog!!.leftClickTV.setTextColor(leftColor)
            }
            hintDialog!!.rightClickTV.text = rightClickStr
            if (rightColor > 0) {
                hintDialog!!.rightClickTV.setTextColor(rightColor)
            }
            hintDialog!!.leftClickTV.setOnClickListener{
                view ->
                if (onLeftClickListener != null) {
                    onLeftClickListener!!(view)
                }
                hintDialog!!.dismiss()
            }
            hintDialog!!.rightClickTV.setOnClickListener{
                view ->
                if (onRightClickListener != null) {
                    onRightClickListener!!(view)
                }
                hintDialog!!.dismiss()
            }
            if (isSingleClick) {
                if (!TextUtils.isEmpty(leftClickStr)) {
                    hintDialog!!.leftClickTV.visibility = View.VISIBLE
                    hintDialog!!.rightClickTV.visibility = View.GONE
                } else if (!TextUtils.isEmpty(rightClickStr)) {
                    hintDialog!!.leftClickTV.visibility = View.GONE
                    hintDialog!!.rightClickTV.visibility = View.VISIBLE
                } else {
                    hintDialog!!.rightClickTV.visibility = View.VISIBLE
                    hintDialog!!.leftClickTV.visibility = View.GONE
                }
                hintDialog!!.divider.visibility = View.GONE
            }
            return hintDialog!!
        }
    }

}