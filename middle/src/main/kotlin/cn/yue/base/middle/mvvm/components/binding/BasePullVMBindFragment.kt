package cn.yue.base.middle.mvvm.components.binding

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.middle.mvvm.PullViewModel
import cn.yue.base.middle.mvvm.components.BasePullVMFragment
import cn.yue.base.middle.mvvm.data.BR

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BasePullVMBindFragment<VM : PullViewModel, T : ViewDataBinding> : BasePullVMFragment<VM>() {
    lateinit var binding: T
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.setVariable(variableId(), viewModel)
    }

    open fun variableId(): Int = BR.viewModel

    override fun bindLayout(inflated: View) {
        binding = DataBindingUtil.bind(inflated)!!
    }
}