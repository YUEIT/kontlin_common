package cn.yue.base.module

import cn.yue.base.module.manager.IModuleService

interface IAppModule : IModuleService {

    fun loginInvalid()
}
