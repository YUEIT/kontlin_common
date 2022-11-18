package cn.yue.test.nested

import android.os.Bundle
import cn.yue.base.middle.mvp.components.binding.BaseHintBindFragment
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestNestBinding
import com.alibaba.android.arouter.facade.annotation.Route


/**
 * Description :
 * Created by yue on 2022/9/2
 */

@Route(path = "/app/testNested")
class TestNestedFragment : BaseHintBindFragment<FragmentTestNestBinding>() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_nest
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.svMain.setScrollContainer(object : PriorityNestedScrollView.ScrollContainer {
            override fun computeScrollOffset(): Int {
                return binding.svChild.scrollY
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}