package cn.yue.test.nested

import android.os.Bundle
import android.view.View
import cn.yue.base.mvp.components.BaseHintFragment
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestNestBinding
import com.alibaba.android.arouter.facade.annotation.Route


/**
 * Description :
 * Created by yue on 2022/9/2
 */

@Route(path = "/app/testNested")
class TestNestedFragment : BaseHintFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_nest
    }
    
    private lateinit var binding: FragmentTestNestBinding
    
    override fun bindLayout(inflated: View) {
        binding = FragmentTestNestBinding.bind(inflated)
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