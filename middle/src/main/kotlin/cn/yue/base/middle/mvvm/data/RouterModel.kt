package cn.yue.base.middle.mvvm.data

import cn.yue.base.middle.router.RouterCard

data class RouterModel (var routerCard: RouterCard,
                        var requestCode: Int = 0,
                        var toActivity: String? = null)
