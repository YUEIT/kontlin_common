package cn.yue.base.kotlin.test.component

import android.os.Bundle
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.kotlin.test.R
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testWidget")
class TestWidgetFragment: BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_test_widget
    }

    override fun initView(savedInstanceState: Bundle?) {

    }
}