package cn.yue.base.module

import cn.yue.base.module.manager.IModuleService
import cn.yue.base.router.INavigation

interface IFlutterModule : IModuleService {
    val flutterRouter: INavigation?
}