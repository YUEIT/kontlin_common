package cn.yue.test.mvvm

import android.app.Application
import androidx.lifecycle.viewModelScope
import cn.yue.base.mvvm.PullViewModel
import cn.yue.base.mvvm.data.MutableListLiveData
import cn.yue.base.net.coroutine.request
import cn.yue.base.net.observer.BasePullObserver
import cn.yue.test.mode.ApiManager
import cn.yue.test.mode.ItemBean

class TestPullViewModel(application: Application) : PullViewModel(application) {

    var userLiveData = MutableListLiveData<ItemBean>()

    override fun loadData() {
        viewModelScope.request({
            ApiManager.getApi().getAllData()
        }, object : BasePullObserver<List<ItemBean>>(this) {
            override fun onNext(t: List<ItemBean>) {
                userLiveData.setValue(t)
            }
        })
    }
}