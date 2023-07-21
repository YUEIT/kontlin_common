package cn.yue.base.event

import android.app.Application
import cn.yue.base.mvvm.BaseViewModel
import cn.yue.base.utils.event.NoStickyLiveData


/**
 * Description :
 * Created by yue on 2022/1/24
 */

class NotifyViewModel(application: Application): BaseViewModel(application) {

    val loginStatusLiveData = NoStickyLiveData<Int>()
}