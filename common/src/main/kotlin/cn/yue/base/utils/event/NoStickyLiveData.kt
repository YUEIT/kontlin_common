package cn.yue.base.utils.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


/**
 * Description :
 * Created by yue on 2021/12/30
 */

class NoStickyLiveData<T> : MutableLiveData<T>(){

    private var version = -1

    override fun setValue(value: T) {
        version++
        super.setValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, WrapperObserver(version, observer))
    }

    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(WrapperObserver(version, observer))
    }

    inner class WrapperObserver(
        private val bindVersion: Int,
        private val observer: Observer<in T>
    ) : Observer<T> {

        override fun onChanged(t: T) {
            if (bindVersion < version) {
                observer.onChanged(t)
            }
        }
    }
}