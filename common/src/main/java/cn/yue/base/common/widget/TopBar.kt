package cn.yue.base.common.widget

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import cn.yue.base.common.R
import kotlinx.android.synthetic.main.layout_top_bar.view.*

/**
 * Description :
 * Created by yue on 2019/6/17
 */
open class TopBar : RelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.layout_top_bar, this)
        defaultStyle()
    }

    private fun defaultStyle() {
        setBackgroundColor(Color.WHITE)
        leftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        leftTV.setTextColor(Color.parseColor("#000000"))
        centerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        centerTV.setTextColor(Color.parseColor("#000000"))
        rightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        rightTV.setTextColor(Color.parseColor("#000000"))
        leftIV.visibility = View.GONE
        leftTV.visibility = View.GONE
        rightTV.visibility = View.GONE
        rightIV.visibility = View.GONE
        divider.visibility = View.GONE
    }

    fun setBgColor(@ColorInt color: Int): TopBar {
        setBackgroundColor(color)
        return this
    }

    fun setBarVisibility(visible: Int): TopBar {
        visibility = visible
        return this
    }

    fun setLeftTextStr(s: String): TopBar {
        if (leftTV != null) {
            leftTV.visibility = View.VISIBLE
            leftTV.text = s
        }
        return this
    }

    fun setLeftImage(@DrawableRes resId: Int): TopBar {
        if (leftIV != null) {
            leftIV.visibility = View.VISIBLE
            leftIV.setImageResource(resId)
        }
        return this
    }

    fun setLeftClickListener(onClickListener: ((view: View) -> Unit)): TopBar {
        if (leftLL != null) {
            leftLL.setOnClickListener(onClickListener)
        }
        return this
    }

    fun setCenterTextStr(s: String): TopBar {
        if (centerTV != null) {
            centerTV.visibility = View.VISIBLE
            centerTV.text = s
        }
        return this
    }

    fun setCenterClickListener(onClickListener: ((view: View) -> Unit)): TopBar {
        if (centerTV != null) {
            centerTV.setOnClickListener(onClickListener)
        }
        return this
    }

    fun setRightTextStr(s: String): TopBar {
        if (rightTV != null) {
            rightTV.visibility = View.VISIBLE
            rightTV.text = s
        }
        return this
    }

    fun setRightImage(@DrawableRes resId: Int): TopBar {
        if (rightIV != null) {
            rightIV.visibility = View.VISIBLE
            rightIV.setImageResource(resId)
        }
        return this
    }

    fun setRightClickListener(onClickListener: ((view: View) -> Unit)): TopBar {
        if (rightLL != null) {
            rightLL.setOnClickListener(onClickListener)
        }
        return this
    }

    fun setLeftTextSize(sp: Float): TopBar {
        if (leftTV != null) {
            leftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        }
        return this
    }

    fun setLeftTextColor(@ColorInt color: Int): TopBar {
        if (leftTV != null) {
            leftTV.setTextColor(color)
        }
        return this
    }

    fun setCenterTextSize(sp: Float): TopBar {
        if (centerTV != null) {
            centerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        }
        return this
    }

    fun setCenterTextColor(@ColorInt color: Int): TopBar {
        if (centerTV != null) {
            centerTV.setTextColor(color)
        }
        return this
    }

    fun setRightTextSize(sp: Float): TopBar {
        if (rightTV != null) {
            rightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        }
        return this
    }

    fun setRightTextColor(@ColorInt color: Int): TopBar {
        if (rightTV != null) {
            rightTV.setTextColor(color)
        }
        return this
    }

    fun setDividerVisible(visible: Boolean): TopBar {
        if (divider != null) {
            divider.visibility = if (visible) View.VISIBLE else View.GONE
        }
        return this
    }

}