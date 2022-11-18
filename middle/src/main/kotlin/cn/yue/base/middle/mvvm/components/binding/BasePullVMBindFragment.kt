package cn.yue.base.middle.mvvm.components.binding

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.middle.mvvm.PullViewModel
import cn.yue.base.middle.mvvm.components.BasePullVMFragment

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BasePullVMBindFragment<VM : PullViewModel, T : ViewDataBinding> : BasePullVMFragment<VM>() {

    lateinit var binding: T

    override fun bindLayout(inflated: View) {
        binding = DataBindingUtil.bind(inflated)!!
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}