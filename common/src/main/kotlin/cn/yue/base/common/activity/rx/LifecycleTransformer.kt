package cn.yue.base.common.activity.rx

import cn.yue.base.common.activity.rx.Functions.CANCEL_COMPLETABLE
import io.reactivex.*
import org.reactivestreams.Publisher
import java.lang.NullPointerException

class LifecycleTransformer<T> internal constructor(private val observable: Observable<*>)
    : ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>, CompletableTransformer {


    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.takeUntil(observable)
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.takeUntil(observable.toFlowable(BackpressureStrategy.LATEST))
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.takeUntil(observable.firstOrError())
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.takeUntil(observable.firstElement())
    }

    override fun apply(upstream: Completable): CompletableSource {
        return Completable.ambArray(upstream, observable.flatMapCompletable(CANCEL_COMPLETABLE))
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val that = o as LifecycleTransformer<*>
        return observable == that.observable
    }

    override fun hashCode(): Int {
        return observable.hashCode()
    }

    override fun toString(): String {
        return "LifecycleTransformer{" +
                "observable=" + observable +
                '}'
    }

}