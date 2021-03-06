package cn.yue.base.middle.mvvm

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.lifecycle.*
import cn.yue.base.common.activity.rx.ILifecycleProvider
import cn.yue.base.common.activity.rx.LifecycleTransformer
import cn.yue.base.common.activity.rx.RxLifecycle.bindUntilEvent
import cn.yue.base.common.activity.rx.RxLifecycleTransformer
import cn.yue.base.middle.mvp.IWaitView
import cn.yue.base.middle.mvvm.data.FinishModel
import cn.yue.base.middle.mvvm.data.IRouterNavigation
import cn.yue.base.middle.mvvm.data.LoaderLiveData
import cn.yue.base.middle.mvvm.data.RouterModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

/**
 * Description :
 * Created by yue on 2020/8/8
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application),
        ILifecycleProvider<Lifecycle.Event>, IWaitView, IRouterNavigation {
    @JvmField
    var loader = LoaderLiveData()
    @JvmField
    var waitEvent = MutableLiveData<String?>()
    @JvmField
    var routerEvent = MutableLiveData<RouterModel>()
    @JvmField
    var finishEvent = MutableLiveData<FinishModel>()
    var childViewModels: MutableList<BaseViewModel> = ArrayList()

    fun addLifecycle(childViewModel: BaseViewModel) {
        childViewModels.add(childViewModel)
    }

    private val lifecycleSubject = BehaviorSubject.create<Lifecycle.Event>()

    @CheckResult
    override fun lifecycle(): Observable<Lifecycle.Event> {
        return lifecycleSubject.hide()
    }

    @CheckResult
    private fun <T> bindUntilEvent(event: Lifecycle.Event): LifecycleTransformer<T> {
        return bindUntilEvent(lifecycleSubject, event)
    }

    override fun <T> toBindLifecycle(): RxLifecycleTransformer<T> {
        return RxLifecycleTransformer<T>(bindUntilEvent(Lifecycle.Event.ON_DESTROY))
    }

    override fun <T> toBindLifecycle(e: Lifecycle.Event): RxLifecycleTransformer<T> {
        return RxLifecycleTransformer<T>(bindUntilEvent(e))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    protected fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_ANY)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onAny(owner, event)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected fun onCreate() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_CREATE)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onCreate()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected fun onDestroy() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_DESTROY)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onDestroy()
            }
        }
        childViewModels.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun onStart() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_START)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onStart()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected fun onStop() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_STOP)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onStop()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected fun onResume() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_RESUME)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onResume()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected fun onPause() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_PAUSE)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onPause()
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun showWaitDialog(title: String) {
        waitEvent.postValue(title)
    }

    override fun dismissWaitDialog() {
        waitEvent.postValue(null)
    }

    override fun navigation(routerModel: RouterModel) {
        routerEvent.postValue(routerModel)
    }

    open fun finish() {
        finishEvent.postValue(FinishModel())
    }

    open fun finishForResult(resultCode: Int, bundle: Bundle? = null) {
        val finishModel = FinishModel()
        finishModel.resultCode = resultCode
        finishModel.bundle = bundle
        finishEvent.postValue(finishModel)
    }

}