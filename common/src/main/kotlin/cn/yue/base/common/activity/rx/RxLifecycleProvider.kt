package cn.yue.base.common.activity.rx

import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * Description :
 * Created by yue on 2020/8/12
 */
class RxLifecycleProvider : ILifecycleProvider<Lifecycle.Event>, LifecycleObserver {

    private val lifecycleSubject: BehaviorSubject<Lifecycle.Event> = BehaviorSubject.create()

    override fun lifecycle(): Observable<Lifecycle.Event> {
        return lifecycleSubject.hide()
    }

    override fun <T> toBindLifecycle(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.compose(bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun <T> toBindLifecycle(e: Lifecycle.Event): SingleTransformer<T, T> {
        return SingleTransformer {
            it.compose(bindUntilEvent(e))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private fun <T> bindUntilEvent(@NonNull event: Lifecycle.Event) : LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_ANY)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_CREATE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_DESTROY)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_START)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_STOP)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_RESUME)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        lifecycleSubject.onNext(Lifecycle.Event.ON_PAUSE)
    }

}