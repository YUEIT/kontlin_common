package cn.yue.base.event

import cn.yue.base.init.CommonApplication
import cn.yue.base.utils.Utils


/**
 * Description :
 * Created by yue on 2022/1/24
 */

object BaseViewModes {

    fun getNotifyViewModel(): NotifyViewModel {
        val application = Utils.getContext() as CommonApplication
        return application.createViewModel(NotifyViewModel::class.java)
    }
}