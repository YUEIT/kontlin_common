package cn.yue.base.middle.mvvm.data

import androidx.lifecycle.MutableLiveData
import java.util.*

class MutableListLiveData<T> : MutableLiveData<ArrayList<T>>(ArrayList<T>()) {

    override fun postValue(value: ArrayList<T>) {
        super.postValue(value)
    }

    override fun setValue(value: ArrayList<T>) {
        super.setValue(value)
    }

    fun add(t: T) {
        var list = value
        if (list == null) {
            list = ArrayList()
        }
        list.add(t)
        postValue(list)
    }

    fun addAll(c: Collection<T>?) {
        var list = value
        if (list == null) {
            list = ArrayList()
        }
        list.addAll(c!!)
        postValue(list)
    }

    fun remove(index: Int): T {
        var list = value
        if (list == null) {
            list = ArrayList()
        }
        val t = list.removeAt(index)
        postValue(list)
        return t
    }

    fun clear() {
        var list = value
        if (list == null) {
            list = ArrayList()
        }
        list.clear()
        postValue(list)
    }

    override fun getValue(): ArrayList<T>? {
        return super.getValue()
    }

    fun size(): Int {
        return value?.size?: 0
    }
}