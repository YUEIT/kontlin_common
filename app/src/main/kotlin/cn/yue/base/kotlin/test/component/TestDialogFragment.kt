package cn.yue.base.kotlin.test.component

import android.os.Bundle
import cn.yue.base.common.activity.BaseDialogFragment
import cn.yue.base.common.activity.TransitionAnimation
import cn.yue.base.kotlin.test.R

import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testDialog")
class TestDialogFragment : BaseDialogFragment() {

    override fun getLayoutId(): Int {
        return R.layout.dialog_test
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initEnterStyle() {
        setEnterStyle(TransitionAnimation.TRANSITION_LEFT)
    }
}