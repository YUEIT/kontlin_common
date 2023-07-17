package cn.yue.base.utils.event

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * 管理 CompositeSubscription
 */
object RxSubscriptions {

    private val mSubscriptions = CompositeDisposable()

    fun isDisposed(): Boolean {
        return mSubscriptions.isDisposed
    }

    fun add(s: Disposable?) {
        if (s != null) {
            mSubscriptions.add(s)
        }
    }

    fun remove(s: Disposable?) {
        if (s != null) {
            mSubscriptions.remove(s)
        }
    }

    fun clear() {
        mSubscriptions.clear()
    }

    fun dispose() {
        mSubscriptions.dispose()
    }
}