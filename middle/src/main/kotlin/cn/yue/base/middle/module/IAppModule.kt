package cn.yue.base.middle.module

import cn.yue.base.middle.module.manager.IModuleService

interface IAppModule : IModuleService  {
    fun test(str: String)
}
