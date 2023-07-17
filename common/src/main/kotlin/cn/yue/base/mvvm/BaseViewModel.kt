package cn.yue.base.mvvm

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.lifecycle.*
import cn.yue.base.activity.rx.ILifecycleProvider
import cn.yue.base.activity.rx.LifecycleTransformer
import cn.yue.base.activity.rx.RxLifecycle.bindUntilEvent
import cn.yue.base.activity.rx.RxLifecycleTransformer
import cn.yue.base.mvp.IWaitView
import cn.yue.base.mvvm.data.FinishModel
import cn.yue.base.mvvm.data.IRouterNavigation
import cn.yue.base.mvvm.data.LoaderLiveData
import cn.yue.base.mvvm.data.RouterModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * Description :
 * Created by yue on 2020/8/8
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application),
	ILifecycleProvider<Lifecycle.Event>, DefaultLifecycleObserver, IWaitView,
	IRouterNavigation {

    val coroutineScope by lazy { viewModelScope }
    var loader = LoaderLiveData()
    var waitEvent = MutableLiveData<String?>()
    var routerEvent = MutableLiveData<RouterModel>()
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

    override fun onCreate(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_CREATE)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onCreate(owner)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_DESTROY)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onDestroy(owner)
            }
        }
        childViewModels.clear()
    }

    override fun onStart(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_START)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onStart(owner)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_STOP)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onStop(owner)
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_RESUME)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onResume(owner)
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_PAUSE)
        if (childViewModels.isNotEmpty()) {
            for (childViewModel in childViewModels) {
                childViewModel.onPause(owner)
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