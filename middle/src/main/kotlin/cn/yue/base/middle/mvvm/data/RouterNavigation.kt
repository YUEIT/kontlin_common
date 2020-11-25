package cn.yue.base.middle.mvvm.data

import cn.yue.base.middle.router.RouterCard

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