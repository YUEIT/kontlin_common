package cn.yue.base.net.observer

import io.reactivex.rxjava3.observers.DisposableSingleObserver

/**
 * Description :
 * Created by yue on 2023/5/9
 */
class BaseObserver<T>: DisposableSingleObserver<T>() {
	
	override fun onSuccess(t: T) {
		TODO("Not yet implemented")
	}
	
	override fun onError(e: Throwable) {
		TODO("Not yet implemented")
	}
	
}