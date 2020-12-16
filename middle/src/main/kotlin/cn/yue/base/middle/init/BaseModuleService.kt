package cn.yue.base.middle.init

import android.app.Application
import android.content.Context
import cn.yue.base.common.utils.debug.LogUtils
import cn.yue.base.middle.module.IBaseModule
import cn.yue.base.middle.router.FRouter

class BaseModuleService : IBaseModule {

    override fun init(context: Context) {
        FRouter.init(context as Application)
        BaseUrlAddress.setDebug(InitConstant.isDebug())
        LogUtils.setDebug(InitConstant.isDebug())
        NotificationConfig.initChannel()
    }
}