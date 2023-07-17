package cn.yue.base.binding.check

import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter

object ViewAdapter {

    @BindingAdapter(value = ["onCheckedChangeListener"])
    @JvmStatic
    fun setOnCheckedChangeListener(checkBox: CheckBox, onCheckedChangeListener: (buttonView: CompoundButton, isChecked: Boolean) -> Unit) {
        checkBox.setOnCheckedChangeListener { v, b ->
            onCheckedChangeListener.invoke(v, b)
        }
    }

    @BindingAdapter(value = ["onCheckedChangeListener"])
    @JvmStatic
    fun setOnCheckedChangeListener(radioGroup: RadioGroup, onCheckedChangeListener: (group: RadioGroup, checkedId: Int) -> Unit) {
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            onCheckedChangeListener(group, checkedId)
        }
    }
}