package cn.yue.base.widget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import cn.yue.base.common.R
import cn.yue.base.common.databinding.LayoutTopBarBinding
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.app.DisplayUtils.dip2px

/**
 * Description :
 * Created by yue on 2019/3/8
 */
class TopBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    var binding: LayoutTopBarBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_top_bar, this, true)

    init {
        val layoutParams = binding.statusBarSpace.layoutParams as LinearLayout.LayoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layoutParams.height = Math.max(BarUtils.getStatusBarHeight(), dip2px(30))
        }
        defaultStyle()
    }

    private fun defaultStyle() {
        setBackgroundColor(Color.WHITE)
        binding.leftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        binding.leftTV.setTextColor(Color.parseColor("#000000"))
        binding.centerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        binding.centerTV.setTextColor(Color.parseColor("#000000"))
        binding.rightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        binding.rightTV.setTextColor(Color.parseColor("#000000"))
        binding.leftIV.visibility = View.GONE
        binding.leftTV.visibility = View.GONE
        binding.rightTV.visibility = View.GONE
        binding.rightIV.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.centerIV.visibility = View.GONE
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
        binding.barContentRL.visibility = visibility
        return this
    }

    fun setLeftTextStr(s: String?): TopBar {
        if (binding.leftTV != null) {
            binding.leftTV.visibility = View.VISIBLE
            binding.leftTV.text = s
        }
        return this
    }

    fun setLeftImage(@DrawableRes resId: Int): TopBar {
        binding.leftIV.visibility = View.VISIBLE
        binding.leftIV.setImageResource(resId)
        return this
    }

    fun setLeftClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        binding.leftLL.setOnClickListener(onClickListener)
        return this
    }

    fun setCenterTextStr(s: String?): TopBar {
        binding.centerTV.visibility = View.VISIBLE
        binding.centerTV.text = s
        return this
    }

    fun setCenterClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        binding.centerLL.setOnClickListener(onClickListener)
        return this
    }

    fun setRightTextStr(s: String?): TopBar {
        binding.rightTV.visibility = View.VISIBLE
        binding.rightTV.text = s
        return this
    }

    fun setRightImage(@DrawableRes resId: Int): TopBar {
        binding.rightIV.visibility = View.VISIBLE
        binding.rightIV.setImageResource(resId)
        return this
    }

    fun setRightClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        binding.rightLL.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftTextSize(sp: Float): TopBar {
        binding.leftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setLeftTextColor(@ColorInt color: Int): TopBar {
        binding.leftTV.setTextColor(color)
        return this
    }

    fun setCenterTextSize(sp: Float): TopBar {
        binding.centerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setCenterTextColor(@ColorInt color: Int): TopBar {
        binding.centerTV.setTextColor(color)
        return this
    }

    fun setCenterImage(resId: Int): TopBar {
        if (resId == 0) {
            binding.centerIV.visibility = View.GONE
        } else {
            binding.centerIV.visibility = View.VISIBLE
            binding.centerIV.setImageResource(resId)
        }
        return this
    }

    fun setRightTextSize(sp: Float): TopBar {
        binding.rightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setRightTextColor(@ColorInt color: Int): TopBar {
        binding.rightTV.setTextColor(color)
        return this
    }

    fun setDividerVisible(visible: Boolean): TopBar {
        binding.divider.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }
}