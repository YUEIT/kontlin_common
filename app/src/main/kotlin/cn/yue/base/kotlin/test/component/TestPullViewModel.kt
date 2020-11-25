package cn.yue.base.kotlin.test.component

import android.app.Application
import androidx.lifecycle.viewModelScope
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.middle.mvvm.PullViewModel
import cn.yue.base.middle.net.coroutine.request
import cn.yue.base.middle.net.observer.BasePullObserver
import kotlinx.coroutines.delay

class TestPullViewModel(application: Application) : PullViewModel(application) {
    override fun loadData() {
        viewModelScope.request({
            delay(10000)
            "ssss"
        }, object : BasePullObserver<String>(this@TestPullViewModel) {
            override fun onNext(t: String) {
                ToastUtils.showShortToast(t)
            }
        })
    }
}