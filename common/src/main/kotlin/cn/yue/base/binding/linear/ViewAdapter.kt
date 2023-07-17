package cn.yue.base.binding.linear

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import cn.yue.base.widget.linear.LinearFillingHelper

object ViewAdapter {

    @BindingAdapter(value = ["adapter"])
    @JvmStatic
    fun setAdapter(linearLayout: LinearLayout, adapter: LinearFillingHelper.Adapter) {
        LinearFillingHelper(linearLayout).setAdapter(adapter)
    }
}