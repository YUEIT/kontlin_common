package cn.yue.base.event

import cn.yue.base.mvvm.BaseViewModel
import cn.yue.base.utils.event.NoStickyLiveData


/**
 * Description :
 * Created by yue on 2022/1/24
 */

class NotifyViewModel: BaseViewModel() {

    companion object {

        fun getLoadStatus(): NoStickyLiveData<Int> {
            return BaseViewModes.getNotifyViewModel().loginStatusLiveData
        }
    }

    val loginStatusLiveData = NoStickyLiveData<Int>()
}