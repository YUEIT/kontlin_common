package cn.yue.base.common.utils.event

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.ConcurrentHashMap

class RxBus {
    private val mBus: Subject<Any> = PublishSubject.create<Any>().toSerialized()
    private val mStickyEventMap: MutableMap<Class<*>, Any> = ConcurrentHashMap()

    fun post(event: Any) {
        mBus.onNext(event)
    }

    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return mBus.ofType(eventType)
    }

    fun hasObservers(): Boolean {
        return mBus.hasObservers()
    }

    fun postSticky(event: Any) {
        synchronized(mStickyEventMap) { mStickyEventMap.put(event.javaClass, event) }
        post(event)
    }

    fun <T> toObservableSticky(eventType: Class<T>): Observable<T> {
        synchronized(mStickyEventMap) {
            val observable = mBus.ofType(eventType)
            val event = mStickyEventMap[eventType]
            return if (event != null) {
                Observable.merge(observable, Observable.create(ObservableOnSubscribe {
                    emitter ->
                    emitter.onNext(eventType.cast(event)!!)
                }))
            } else {
                observable
            }
        }
    }

    fun <T> getStickyEvent(eventType: Class<T>): T? {
        synchronized(mStickyEventMap) { return eventType.cast(mStickyEventMap[eventType]) }
    }

    fun <T> removeStickyEvent(eventType: Class<T>): T? {
        synchronized(mStickyEventMap) { return eventType.cast(mStickyEventMap.remove(eventType)) }
    }

    fun removeAllStickyEvents() {
        synchronized(mStickyEventMap) { mStickyEventMap.clear() }
    }

    private object RxBusHolder {
        val instance = RxBus()
    }

    companion object {
        fun getDefault(): RxBus {
            return RxBusHolder.instance
        }
    }
}