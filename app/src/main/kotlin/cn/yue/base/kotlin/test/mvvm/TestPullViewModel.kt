package cn.yue.base.kotlin.test.mvvm

import android.app.Application
import androidx.lifecycle.viewModelScope
import cn.yue.base.kotlin.test.mode.ApiManager
import cn.yue.base.kotlin.test.mode.UserBean
import cn.yue.base.middle.mvvm.PullViewModel
import cn.yue.base.middle.mvvm.data.MutableListLiveData
import cn.yue.base.middle.net.coroutine.request
import cn.yue.base.middle.net.observer.BasePullObserver

class TestPullViewModel(application: Application) : PullViewModel(application) {

    var userLiveData = MutableListLiveData<UserBean>()

    override fun loadData() {
        viewModelScope.request({
            ApiManager.getApi().getAllUser()
        }, object : BasePullObserver<List<UserBean>>(this) {
            override fun onNext(t: List<UserBean>) {
                userLiveData.setValue(t)
            }
        })
    }
}