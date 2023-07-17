package cn.yue.test.event

import android.app.Application
import cn.yue.base.utils.event.NoStickyLiveData
import cn.yue.base.mvvm.BaseViewModel


/**
 * Description :
 * Created by yue on 2022/1/24
 */

class NotifyViewModel(application: Application): BaseViewModel(application) {

    val eventLiveData = NoStickyLiveData<Int>()
}