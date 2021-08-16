package cn.yue.base.kotlin.test.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.lang.Exception
import java.util.ArrayList

open class BusLiveData<T> : LiveData<T>(){

    private val observerVersion = HashMap<Int, Int>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observerVersion[observer.hashCode()] = invokeVersion()
        super.observe(owner, Observer {
            if (invokeVersion() != observerVersion[observer.hashCode()]) {
                observer.onChanged(it)
            }
        })
    }

    override fun observeForever(observer: Observer<in T>) {
        observerVersion[observer.hashCode()] = invokeVersion()
        super.observeForever(Observer {
            if (invokeVersion() != observerVersion[observer.hashCode()]) {
                observer.onChanged(it)
            }
        })
    }

    private fun invokeVersion(): Int {
        try {
            val cls = Class.forName(LiveData::class.java.name)
            val getVersion = cls.getDeclaredMethod("getVersion")
            getVersion.isAccessible = true
            return getVersion.invoke(this) as Int
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }
}