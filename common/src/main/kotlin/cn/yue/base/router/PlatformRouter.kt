package cn.yue.base.router

/**
 * Description : 跨平台路由 支持Flutter页面跳转
 * Created by yue on 2020/4/22
 */
class PlatformRouter() : INavigation() {

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

    override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
        navigation?.apply {
            navigation(context, requestCode, toActivity)
        }
    }

}