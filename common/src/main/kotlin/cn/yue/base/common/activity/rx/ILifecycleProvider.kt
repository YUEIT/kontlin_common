package cn.yue.base.common.activity.rx

import androidx.lifecycle.LifecycleObserver
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue

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