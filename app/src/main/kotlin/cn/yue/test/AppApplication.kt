package cn.yue.test

import cn.yue.base.init.CommonApplication
import cn.yue.base.init.InitConstant
import cn.yue.base.module.IAppModule
import cn.yue.base.module.ModuleType
import cn.yue.base.module.manager.ModuleManager
import cn.yue.test.utils.LocalStorage


/**
 * Description :
 * Created by yue on 2018/11/14
 */
class AppApplication : CommonApplication() {
    
    override fun preInit() {
        InitConstant.setDebug(BuildConfig.DEBUG_MODE)
        InitConstant.setVersionName(BuildConfig.VERSION_NAME)
        InitConstant.setServiceEnvironment(LocalStorage.getServiceEnvironment())
    }
    
    override fun onInit() {
        super.onInit()
        ModuleManager.register(ModuleType.MODULE_APP, IAppModule::class, AppModuleService())
    }

}