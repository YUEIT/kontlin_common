package cn.yue.base.middle.init

import android.content.Context
import cn.yue.base.common.utils.Utils
import cn.yue.base.middle.module.IBaseModule

class BaseModuleService : IBaseModule {

    override fun init(context: Context) {
        NotificationConfig.initChannel()
        Utils.initAfterAuth()
    }
}