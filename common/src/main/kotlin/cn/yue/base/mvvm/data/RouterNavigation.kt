package cn.yue.base.mvvm.data

import cn.yue.base.router.RouterCard

/**
 * Description :
 * Created by yue on 2020/10/20
 */
interface IRouterNavigation {
    fun navigation(routerModel: RouterModel)
}

fun RouterCard.navigation(iRouterNavigation: IRouterNavigation, requestCode: Int = 0, toActivity: String? = null) {
    val routerModel = RouterModel(this, requestCode, toActivity)
    iRouterNavigation.navigation(routerModel)
}