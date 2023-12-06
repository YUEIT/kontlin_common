package cn.yue.base.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import cn.yue.base.R
import cn.yue.base.databinding.LayoutHintDialogBinding
import cn.yue.base.utils.app.DisplayUtils

/**
 * Description :
 * Created by yue on 2019/6/17
 */
class HintDialog(context: Context): Dialog(context) {

    private var binding: LayoutHintDialogBinding

    init {
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        val content: View = View.inflate(context, R.layout.layout_hint_dialog, null)
        binding = LayoutHintDialogBinding.bind(content)
        setContentView(content)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setGravity(Gravity.CENTER)
        window?.setWindowAnimations(R.style.FadeAnimation)
        val lp = window?.attributes
        lp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
    }

    fun setCanCanceled(canCancel: Boolean) {
        setCanceledOnTouchOutside(canCancel)
        setCancelable(canCancel)
    }

    fun setDelayDimBehind() {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isShowing) {
                window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }, 100)
    }

    class Builder(private var context: Context) {
        private var titleStr: String? = null
        private var titleColor: Int = 0
        private var isShowTitle = true
        private var contentStr: String? = null
        private var contentColor: Int = 0
        private var contentView: View? = null
        private var leftClickStr: String? = null
        private var leftColor: Int = 0
        private var leftBackground: Int = 0
        private var rightClickStr: String? = null
        private var rightColor: Int = 0
        private var rightBackground: Int = 0
        private var isSingleClick: Boolean = false
        private var canCancel: Boolean = true
        private var delayDimBehind: Boolean = false
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

        fun setLeftBackground(@DrawableRes leftBackground: Int): Builder {
            this.leftBackground = leftBackground
            return this
        }

        fun setRightColor(@ColorInt rightColor: Int): Builder {
            this.rightColor = rightColor
            return this
        }

        fun setRightBackground(@DrawableRes rightBackground: Int): Builder {
            this.rightBackground = rightBackground
            return this
        }

        fun setCanCanceled(canCancel: Boolean): Builder {
            this.canCancel = canCancel
            return this
        }

        fun setDelayDimBehind(delayDimBehind: Boolean): Builder {
            this.delayDimBehind = delayDimBehind
            return this
        }

        fun build(): HintDialog {
            hintDialog = HintDialog(context)
            hintDialog?.apply {
                setCanCanceled(canCancel)
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
                        val contentTV = AppCompatTextView(context)
                        contentTV.gravity = Gravity.CENTER
                        contentTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                        contentTV.text = contentStr
                        if (contentColor != 0) {
                            contentTV.setTextColor(contentColor)
                        } else {
                            contentTV.setTextColor(Color.parseColor("#333333"))
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
                if (leftBackground != 0) {
                    binding.leftClickTV.setBackgroundResource(leftBackground)
                }
                binding.rightClickTV.text = rightClickStr
                if (rightColor != 0) {
                    binding.rightClickTV.setTextColor(rightColor)
                }
                if (rightBackground != 0) {
                    binding.rightClickTV.setBackgroundResource(rightBackground)
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
                        val layoutParams = binding.leftClickTV.layoutParams
                        layoutParams.width = DisplayUtils.dp2px(187)
                        binding.leftClickTV.layoutParams = layoutParams
                    } else {
                        binding.rightClickTV.visibility = View.VISIBLE
                        binding.leftClickTV.visibility = View.GONE
                        val layoutParams = binding.rightClickTV.layoutParams
                        layoutParams.width = DisplayUtils.dp2px(187)
                        binding.rightClickTV.layoutParams = layoutParams
                    }
                }
                if (delayDimBehind) {
                    setDelayDimBehind()
                }
            }

            return hintDialog!!
        }
    }

}