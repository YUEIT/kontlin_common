package cn.yue.base.router

/**
 * Description : 路由跳转
 * Created by yue on 2020/4/22
 */
open class INavigation {

    open fun bindRouterCard(routerCard: RouterCard): INavigation? {
        return null
    }
    
    @JvmOverloads
    open fun navigation(context: Any, requestCode: Int = 0, toActivity: String? = null) {

    }
}