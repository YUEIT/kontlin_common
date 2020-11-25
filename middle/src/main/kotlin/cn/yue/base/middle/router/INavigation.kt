package cn.yue.base.middle.router

import android.content.Context

/**
 * Description : 路由跳转
 * Created by yue on 2020/4/22
 */
interface INavigation {
    fun bindRouterCard(routerCard: RouterCard): INavigation?
    fun navigation(context: Context, requestCode: Int = 0, toActivity: String? = null)
}