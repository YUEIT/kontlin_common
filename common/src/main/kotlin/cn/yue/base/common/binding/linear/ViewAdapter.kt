package cn.yue.base.common.binding.linear

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import cn.yue.base.common.widget.linear.LinearFillingHelper

object ViewAdapter {

    @BindingAdapter(value = ["adapter"])
    @JvmStatic
    fun setAdapter(linearLayout: LinearLayout, adapter: LinearFillingHelper.Adapter) {
        LinearFillingHelper(linearLayout).setAdapter(adapter)
    }
}