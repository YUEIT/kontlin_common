package cn.yue.base.middle.mvvm.components.binding

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.components.BaseHintVMFragment

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BaseHintVMBindFragment<VM : BaseViewModel, T : ViewDataBinding> : BaseHintVMFragment<VM>() {

    lateinit var binding: T

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.setVariable(variableId(), viewModel)
    }

    abstract fun variableId(): Int

    override fun bindLayout(inflated: View) {
        binding = DataBindingUtil.bind(inflated)!!
    }
}