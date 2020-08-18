package cn.yue.base.middle.init

import android.app.Application
import android.content.Context
import android.util.Log
import cn.yue.base.middle.init.BaseUrlAddress.setDebug
import cn.yue.base.middle.module.IBaseModule
import cn.yue.base.middle.router.FRouter

class BaseModuleService : IBaseModule {

    override fun init(context: Context) {
        FRouter.init(context as Application)
        setDebug(InitConstant.isDebug)
    }
}