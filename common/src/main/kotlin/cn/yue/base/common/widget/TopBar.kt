package cn.yue.base.common.widget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import cn.yue.base.common.R
import cn.yue.base.common.utils.app.BarUtils
import cn.yue.base.common.utils.app.DisplayUtils.dip2px
import kotlinx.android.synthetic.main.layout_top_bar.view.*

/**
 * Description :
 * Created by yue on 2019/3/8
 */
class TopBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.layout_top_bar, this)
        val statusBarSpace = findViewById<View>(R.id.statusBarSpace)
        val layoutParams = statusBarSpace.layoutParams as LinearLayout.LayoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layoutParams.height = Math.max(BarUtils.getStatusBarHeight(), dip2px(30f))
        }
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

    fun setContentVisibility(visibility: Int): TopBar {
        barContentRL.visibility = visibility
        return this
    }

    fun setLeftTextStr(s: String?): TopBar {
        if (leftTV != null) {
            leftTV.visibility = View.VISIBLE
            leftTV.text = s
        }
        return this
    }

    fun setLeftImage(@DrawableRes resId: Int): TopBar {
        leftIV.visibility = View.VISIBLE
        leftIV.setImageResource(resId)
        return this
    }

    fun setLeftClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        leftLL.setOnClickListener(onClickListener)
        return this
    }

    fun setCenterTextStr(s: String?): TopBar {
        centerTV.visibility = View.VISIBLE
        centerTV.text = s
        return this
    }

    fun setCenterClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        centerTV.setOnClickListener(onClickListener)
        return this
    }

    fun setRightTextStr(s: String?): TopBar {
        rightTV.visibility = View.VISIBLE
        rightTV.text = s
        return this
    }

    fun setRightImage(@DrawableRes resId: Int): TopBar {
        rightIV.visibility = View.VISIBLE
        rightIV.setImageResource(resId)
        return this
    }

    fun setRightClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        rightLL.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftTextSize(sp: Float): TopBar {
        leftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setLeftTextColor(@ColorInt color: Int): TopBar {
        leftTV.setTextColor(color)
        return this
    }

    fun setCenterTextSize(sp: Float): TopBar {
        centerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setCenterTextColor(@ColorInt color: Int): TopBar {
        centerTV.setTextColor(color)
        return this
    }

    fun setRightTextSize(sp: Float): TopBar {
        rightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setRightTextColor(@ColorInt color: Int): TopBar {
        rightTV.setTextColor(color)
        return this
    }

    fun setDividerVisible(visible: Boolean): TopBar {
        divider.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }
}