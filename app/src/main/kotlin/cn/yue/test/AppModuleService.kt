package cn.yue.test

import android.app.Application
import android.content.Context
import cn.yue.base.middle.init.AutoSizeInitUtils
import cn.yue.base.middle.module.IAppModule

/**
 * Description :
 * Created by yue on 2020/8/17
 */
class AppModuleService : IAppModule {

    override fun init(context: Context) {
        AutoSizeInitUtils.init(context as Application)
    }

    override fun loginInvalid() {

    }
}