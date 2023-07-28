package cn.yue.base.mvvm.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.yue.base.utils.code.hasValue

/**
 * Description :
 * Created by yue on 2023/7/26
 */
class CaseLiveData<T>(private val listLiveData: LiveData<List<T>>) : MutableLiveData<T>() {
	
	private val mObservers = HashMap<String, Observer<List<T>>>()
	
	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		super.observe(owner, observer)
		val wrapperObserver = Observer<List<T>> {
			if (it.hasValue()) {
				observer.onChanged(it[0])
			}
		}
		mObservers[observer.hashCode().toString()] = wrapperObserver
		listLiveData.observe(owner, wrapperObserver)
	}
	
	override fun observeForever(observer: Observer<in T>) {
		super.observeForever(observer)
		val wrapperObserver = Observer<List<T>> {
			if (it.hasValue()) {
				observer.onChanged(it[0])
			}
		}
		mObservers[observer.hashCode().toString()] = wrapperObserver
		listLiveData.observeForever(wrapperObserver)
	}
	
	override fun removeObserver(observer: Observer<in T>) {
		super.removeObserver(observer)
		val willRemove = mObservers[observer.hashCode().toString()]
		if (willRemove != null) {
			listLiveData.removeObserver(willRemove)
		}
	}
	
}