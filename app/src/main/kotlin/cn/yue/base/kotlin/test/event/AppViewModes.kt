package cn.yue.base.kotlin.test.event

import cn.yue.base.common.utils.Utils
import cn.yue.base.middle.init.MiddleApplication


/**
 * Description :
 * Created by yue on 2022/1/24
 */

object AppViewModes {

    fun getNotifyViewModel(): NotifyViewModel {
        val application = Utils.getContext() as MiddleApplication
        return application.createViewModel(NotifyViewModel::class.java)
    }
}