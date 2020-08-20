package cn.yue.base.kotlin.test.component

import android.app.Application
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.middle.mvvm.PullViewModel
import cn.yue.base.middle.net.observer.BasePullSingleObserver
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class TestPullViewModel(application: Application) : PullViewModel(application) {
    override fun loadData() {
        Single.just("ssss")
                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(this.toBindLifecycle())
                .subscribe(object : BasePullSingleObserver<String>(this@TestPullViewModel) {
                    override fun onNext(s: String) {
                        showShortToast(s)
                    }
                })
    }
}