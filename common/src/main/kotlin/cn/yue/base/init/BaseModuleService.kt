package cn.yue.base.init

import android.content.Context
import cn.yue.base.module.IBaseModule
import cn.yue.base.utils.Utils

class BaseModuleService : IBaseModule {

    override fun init(context: Context) {
	    NotificationConfig.initChannel()
        Utils.initAfterAuth()
    }
}