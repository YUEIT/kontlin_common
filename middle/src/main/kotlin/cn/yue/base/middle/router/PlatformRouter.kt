package cn.yue.base.middle.router

import android.app.Activity
import android.content.Context
import cn.yue.base.middle.module.IFlutterModule
import cn.yue.base.middle.module.manager.IModuleService
import cn.yue.base.middle.module.manager.ModuleManager.Companion.getModuleService

/**
 * Description : 跨平台路由 支持Flutter页面跳转
 * Created by yue on 2020/4/22
 */
class PlatformRouter : INavigation {

    companion object {
        val instance: PlatformRouter = PlatformRouterHolder.instance
    }
    
    private object PlatformRouterHolder {
        val instance = PlatformRouter()
    }

    private var navigation: INavigation? = null
    private var routerCard: RouterCard? = null

    @Synchronized
    fun build(pactUrl: String): RouterCard {
        if (pactUrl.startsWith(IRouterPath.FLUTTER)) {
//            navigation = getModuleService<IModuleService>(IFlutterModule::class).getFlutterRouter()
        } else if (pactUrl.startsWith(IRouterPath.NATIVE)) {
            navigation = instance
        }
        routerCard = RouterCard(navigation)
        routerCard!!.setPactUrl(pactUrl)
        navigation!!.bindRouterCard(routerCard!!)
        return routerCard!!
    }

    override fun bindRouterCard(routerCard: RouterCard): INavigation? {
        this.routerCard = routerCard
        this.routerCard!!.setNavigationImpl(this)
        return this
    }

    override fun navigation(context: Context) {
        if (navigation != null) {
            navigation!!.navigation(context)
        }
    }

    override fun navigation(context: Context, toActivity: String?) {
        if (navigation != null) {
            navigation!!.navigation(context, toActivity)
        }
    }

    override fun navigation(context: Activity, requestCode: Int) {
        if (navigation != null) {
            navigation!!.navigation(context, requestCode)
        }
    }

    override fun navigation(context: Activity, toActivity: String?, requestCode: Int) {
        if (navigation != null) {
            navigation!!.navigation(context, toActivity, requestCode)
        }
    }

}