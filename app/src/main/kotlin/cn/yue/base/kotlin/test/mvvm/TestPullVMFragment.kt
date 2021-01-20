package cn.yue.base.kotlin.test.mvvm

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.databinding.FragmentTestPullVmBinding
import cn.yue.base.kotlin.test.mode.UserBean

import cn.yue.base.middle.mvvm.components.binding.BasePullVMBindFragment
import cn.yue.base.middle.mvvm.data.BR
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.fragment_test_pull_vm.*

@Route(path = "/app/testPullVM")
class TestPullVMFragment : BasePullVMBindFragment<TestPullViewModel, FragmentTestPullVmBinding>() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_pull_vm
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPullVM")
    }

    private lateinit var adapter: CommonAdapter<UserBean>

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        rv.layoutManager = LinearLayoutManager(mActivity)
        adapter = object : CommonAdapter<UserBean>(mActivity) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: UserBean) {
                holder.setText(R.id.testTV, itemData.name)
            }
        }
    }

    override fun initOther() {
        super.initOther()
        viewModel.userLiveData.observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun variableId(): Int {
        return BR.viewModel
    }
}