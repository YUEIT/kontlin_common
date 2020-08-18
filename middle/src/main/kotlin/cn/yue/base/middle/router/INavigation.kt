package cn.yue.base.middle.router

import android.app.Activity
import android.content.Context

/**
 * Description : 路由跳转
 * Created by yue on 2020/4/22
 */
interface INavigation {
    fun bindRouterCard(routerCard: RouterCard): INavigation?
    fun navigation(context: Context)
    fun navigation(context: Context, toActivity: String?)
    fun navigation(context: Activity, requestCode: Int)
    fun navigation(context: Activity, toActivity: String?, requestCode: Int)
}