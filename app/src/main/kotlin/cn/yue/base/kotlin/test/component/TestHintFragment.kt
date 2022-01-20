package cn.yue.base.kotlin.test.component

import android.graphics.Color
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import cn.yue.base.common.widget.TopBar
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.databinding.FragmentTestHintBinding
import cn.yue.base.middle.components.binding.BaseHintBindFragment
import com.alibaba.android.arouter.facade.annotation.Route


/**
 * Description :
 * Created by yue on 2021/11/12
 */

@Route(path = "/app/testHint")
class TestHintFragment: BaseHintBindFragment<FragmentTestHintBinding>() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_hint
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setBackgroundColor(Color.RED)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val rid = findViewById<RadioGroup>(R.id.rg_option)
        rid.check(R.id.rb_accept)
        findViewById<TextView>(R.id.tv).setOnClickListener{
            rid.check(R.id.rb_accept)
        }
    }
}