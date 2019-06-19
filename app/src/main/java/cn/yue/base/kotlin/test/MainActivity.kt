package cn.yue.base.kotlin.test

import cn.yue.base.common.activity.BaseActivity
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * Description :
 * Created by yue on 2019/6/19
 */
@Route(path = "/app/main")
class MainActivity : BaseActivity() {

    override val layoutId: Int = R.layout.activity_main

    override fun initView() {

    }

}