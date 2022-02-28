package cn.yue.test.components

import android.os.Bundle
import cn.yue.base.common.activity.BaseFragment
import cn.yue.test.R
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testWidget")
class TestWidgetFragment: BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_test_widget
    }

    override fun initView(savedInstanceState: Bundle?) {

    }
}