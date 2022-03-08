package cn.yue.base.middle.mvp.components.binding

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.middle.mvp.components.BaseHintFragment

/**
 * Description :
 * Created by yue on 2019/3/11
 */
abstract class BaseHintBindFragment<T : ViewDataBinding> : BaseHintFragment() {
    lateinit var binding: T
    override fun bindLayout(inflated: View) {
        super.bindLayout(inflated)
        binding = DataBindingUtil.bind(inflated)!!
    }
}