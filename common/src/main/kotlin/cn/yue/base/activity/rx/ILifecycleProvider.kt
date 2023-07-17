package cn.yue.base.activity.rx

import androidx.lifecycle.LifecycleObserver
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.core.Observable

/**
 * Description :
 * Created by yue on 2019/6/17
 */
interface ILifecycleProvider<E> : LifecycleObserver {

    @CheckReturnValue
    fun lifecycle(): Observable<E>

    @CheckReturnValue
    fun <T> toBindLifecycle(): RxLifecycleTransformer<T>

    @CheckReturnValue
    fun <T> toBindLifecycle(e: E): RxLifecycleTransformer<T>
}