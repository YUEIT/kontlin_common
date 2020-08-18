package cn.yue.base.kotlin.test

import cn.yue.base.common.activity.BaseActivity
import cn.yue.base.kotlin.test.component.TestDialogFragment
import cn.yue.base.middle.module.IAppModule
import cn.yue.base.middle.module.manager.ModuleManager
import cn.yue.base.middle.router.FRouter
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Description :
 * Created by yue on 2019/6/19
 */
@Route(path = "/app/main")
class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        jump1.setOnClickListener {
            FRouter.instance.build("/app/testPull").withString("test", "hehe").navigation(this)
        }
        jump2.setOnClickListener {
            FRouter.instance.build("/app/testPullList").navigation(this)
        }
        jump3.setOnClickListener {
            ModuleManager.getModuleService(IAppModule::class).test("hehe")
            TestDialogFragment().show(supportFragmentManager, "")
        }
    }

}

