package cn.yue.base.kotlin.test

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
        jump4.setOnClickListener {
            FRouter.instance.build("/app/testPageVM").navigation(this)
        }
        jump5.setOnClickListener {
            FRouter.instance.build("/app/testPullVM").navigation(this)
        }
        jump6.setOnClickListener {
            FRouter.instance.build("/common/selectPhoto").navigation(this, 1)
        }
        jump7.setOnClickListener {
            FRouter.instance.build("/common/viewPhoto")
                    .withStringArrayList("list", arrayListOf("http://daidaigoucn.oss-cn-shanghai.aliyuncs.com/static/images/shop/sd1.png"))
                    .navigation(this)
        }
        jump8.setOnClickListener {
            FRouter.instance.build("/app/testCoroutine").navigation(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val photos = data?.getParcelableArrayListExtra<Uri>("photos")
            FRouter.instance.build("/common/viewPhoto")
                    .withParcelableArrayList("uris", photos)
                    .navigation(this)
        }
    }
}

