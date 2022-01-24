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
import androidx.databinding.DataBindingUtil
import cn.yue.base.common.R
import cn.yue.base.common.databinding.LayoutHintDialogBinding

/**
 * Description :
 * Created by yue on 2019/6/17
 */
class HintDialog: Dialog {

    private var binding: LayoutHintDialogBinding

    constructor(context: Context): super(context) {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        window?.setWindowAnimations(R.style.FadeAnimation)
        val content: View = View.inflate(context, R.layout.layout_hint_dialog, null)
        binding = DataBindingUtil.bind<LayoutHintDialogBinding>(content)!!
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
            hintDialog?.apply {
                if (isShowTitle) {
                    binding.titleTV.text = titleStr
                    binding.titleTV.visibility = View.VISIBLE
                } else {
                    binding.titleTV.visibility = View.GONE
                }
                if (titleColor != 0) {
                    binding.titleTV.setTextColor(titleColor)
                }
                if (contentView == null) {
                    if (!TextUtils.isEmpty(contentStr)) {
                        val contentTV = TextView(context)
                        contentTV.setTextColor(Color.parseColor("#9b9b9b"))
                        contentTV.gravity = Gravity.CENTER
                        contentTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                        contentTV.text = contentStr
                        if (contentColor != 0) {
                            contentTV.setTextColor(contentColor)
                        }
                        binding.contentLL.addView(contentTV)
                    }
                } else {
                    binding.contentLL.addView(contentView)
                }
                binding.leftClickTV.text = leftClickStr
                if (leftColor != 0) {
                    binding.leftClickTV.setTextColor(leftColor)
                }
                binding.rightClickTV.text = rightClickStr
                if (rightColor != 0) {
                    binding.rightClickTV.setTextColor(rightColor)
                }
                binding.leftClickTV.setOnClickListener{
                        view ->
                    if (onLeftClickListener != null) {
                        onLeftClickListener!!(view)
                    }
                    dismiss()
                }
                binding.rightClickTV.setOnClickListener{
                        view ->
                    if (onRightClickListener != null) {
                        onRightClickListener!!(view)
                    }
                    dismiss()
                }
                if (isSingleClick) {
                    if (!TextUtils.isEmpty(leftClickStr)) {
                        binding.leftClickTV.visibility = View.VISIBLE
                        binding.rightClickTV.visibility = View.GONE
                    } else if (!TextUtils.isEmpty(rightClickStr)) {
                        binding.leftClickTV.visibility = View.GONE
                        binding.rightClickTV.visibility = View.VISIBLE
                    } else {
                        binding.rightClickTV.visibility = View.VISIBLE
                        binding.leftClickTV.visibility = View.GONE
                    }
                    binding.divider.visibility = View.GONE
                }
            }

            return hintDialog!!
        }
    }

}