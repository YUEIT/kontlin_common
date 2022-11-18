package cn.yue.base.middle.net.coroutine

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.net.observer.BaseNetObserver
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

/**
 * Description :
 * Created by yue on 2020/8/25
 */

object CoroutineManager {

    fun <T> request(scope: CoroutineScope, block:  () -> T, observable: BaseNetObserver<T>) {
        val handler = CoroutineExceptionHandler { _, exception ->
            if (InitConstant.isDebug()) {
                exception.printStackTrace()
            }
            observable.onError(exception)
        }
        scope.launch(handler) {
            try {
                observable.onStart()
                val deferred = withContext(Dispatchers.IO) {
                    block.invoke()
                }
                observable.onSuccess(deferred)
            } catch (e : Exception) {
                if (InitConstant.isDebug()) {
                    e.printStackTrace()
                }
                observable.onError(e)
            }
        }
    }

    fun request(scope: CoroutineScope, block: List<suspend () -> Any>, observable: BaseNetObserver<ArrayList<*>>) {
        val handler = CoroutineExceptionHandler { _, exception ->
            observable.onError(exception)
        }
        scope.launch(handler) {
            observable.onStart()
            val deferredArray = ArrayList<Deferred<*>>()
            block.forEach {
                val data = async(Dispatchers.IO) {
                    it.invoke()
                }
                deferredArray.add(data)
            }
            val resultArray = ArrayList<Any?>()
            deferredArray.forEach {
                resultArray.add(it.await())
            }
            observable.onSuccess(resultArray)
        }
    }

}

fun <T> CoroutineScope.request(block: suspend () -> T, observable: BaseNetObserver<T>) {
    val handler = CoroutineExceptionHandler { _, exception ->
        if (InitConstant.isDebug()) {
            exception.printStackTrace()
        }
        observable.onError(exception)
    }
    this.launch(handler) {
        try {
            observable.onStart()
            val deferred = withContext(Dispatchers.IO) {
                block.invoke()
            }
            observable.onSuccess(deferred)
        } catch (e : Exception) {
            if (InitConstant.isDebug()) {
                e.printStackTrace()
            }
            observable.onError(e)
        }
    }
}

fun CoroutineScope.request(block: List<suspend () -> Any>, observable: BaseNetObserver<ArrayList<*>>) {
    val handler = CoroutineExceptionHandler { _, exception ->
        observable.onError(exception)
    }
    this.launch(handler) {
        observable.onStart()
        val deferredArray = ArrayList<Deferred<*>>()
        block.forEach {
            val data = async(Dispatchers.IO) {
                it.invoke()
            }
            deferredArray.add(data)
        }
        val resultArray = ArrayList<Any?>()
        deferredArray.forEach {
            resultArray.add(it.await())
        }
        observable.onSuccess(resultArray)
    }
}

object ThreadDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        thread {
            block.run()
        }
    }
}

object RxDispatcher : CoroutineDispatcher() {
    @SuppressLint("CheckResult")
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Observable.fromCallable { block.run() }
                .subscribeOn(Schedulers.io())
                .subscribe {}
    }
}

object UIDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Handler(Looper.getMainLooper()).post {
            block.run()
        }
    }
}