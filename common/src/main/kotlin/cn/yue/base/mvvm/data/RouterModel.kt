package cn.yue.base.mvvm.data

import cn.yue.base.router.RouterCard
/**
 * Description :
 * Created by yue on 2020/8/8
 */
data class RouterModel (var routerCard: RouterCard,
                        var requestCode: Int = 0,
                        var toActivity: String? = null)
