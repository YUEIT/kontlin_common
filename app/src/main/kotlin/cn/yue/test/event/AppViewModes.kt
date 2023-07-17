package cn.yue.test.event

import cn.yue.base.utils.Utils
import cn.yue.base.init.CommonApplication


/**
 * Description :
 * Created by yue on 2022/1/24
 */

object AppViewModes {

    fun getNotifyViewModel(): NotifyViewModel {
        val application = Utils.getContext() as CommonApplication
        return application.createViewModel(NotifyViewModel::class.java)
    }
}