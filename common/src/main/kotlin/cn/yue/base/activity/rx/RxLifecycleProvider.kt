package cn.yue.base.activity.rx

import androidx.annotation.NonNull
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * Description :
 * Created by yue on 2020/8/12
 */
class RxLifecycleProvider : ILifecycleProvider<Lifecycle.Event>, DefaultLifecycleObserver {

    private val lifecycleSubject: BehaviorSubject<Lifecycle.Event> = BehaviorSubject.create()

    override fun lifecycle(): Observable<Lifecycle.Event> {
        return lifecycleSubject.hide()
    }

    override fun <T> toBindLifecycle(): RxLifecycleTransformer<T> {
        return RxLifecycleTransformer<T>(bindUntilEvent(Lifecycle.Event.ON_DESTROY))
    }

    override fun <T> toBindLifecycle(e: Lifecycle.Event): RxLifecycleTransformer<T> {
        return RxLifecycleTransformer<T>(bindUntilEvent(e))
    }

    private fun <T> bindUntilEvent(@NonNull event: Lifecycle.Event) : LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    override fun onCreate(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_CREATE)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_DESTROY)
    }

    override fun onStart(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_START)
    }

    override fun onStop(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_STOP)
    }

    override fun onResume(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause(owner: LifecycleOwner) {
        lifecycleSubject.onNext(Lifecycle.Event.ON_PAUSE)
    }

}