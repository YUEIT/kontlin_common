package cn.yue.test.mvvm

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.mvvm.components.binding.BasePullVMBindFragment
import cn.yue.test.BR
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestPullVmBinding
import cn.yue.test.mode.ItemBean
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

    private lateinit var adapter: CommonAdapter<ItemBean>

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.setVariable(BR.viewModel, viewModel)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(mActivity)
            adapter = object : CommonAdapter<ItemBean>(mActivity) {
                override fun getLayoutIdByType(viewType: Int): Int {
                    return R.layout.item_test
                }

                override fun bindData(holder: CommonViewHolder, position: Int, itemData: ItemBean) {
                    holder.setText(R.id.testTV, itemData.name)
                }
            }
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.userLiveData.observe(this, Observer {
            adapter.setList(it)
        })
    }

}