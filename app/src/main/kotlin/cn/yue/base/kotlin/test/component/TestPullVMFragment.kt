package cn.yue.base.kotlin.test.component

import android.os.Bundle
import cn.yue.base.common.widget.TopBar
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.databinding.FragmentTestPullVmBinding

import cn.yue.base.middle.mvvm.components.binding.BasePullVMBindFragment
import cn.yue.base.middle.mvvm.data.BR
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testPullVM")
class TestPullVMFragment : BasePullVMBindFragment<TestPullViewModel, FragmentTestPullVmBinding>() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_pull_vm
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPullVM")
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.jump1.setOnClickListener {
        }
        binding.jump2.setOnClickListener {
            viewModel.finish()
        }
    }

    override fun variableId(): Int {
        return BR.viewModel
    }
}