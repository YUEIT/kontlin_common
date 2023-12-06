package cn.yue.base.mvvm.data

import androidx.lifecycle.MutableLiveData

/**
 * Description :
 * Created by yue on 2020/8/8
 */
class MutableListLiveData<T> : MutableLiveData<ArrayList<T>>(ArrayList<T>()) {

    override fun postValue(value: ArrayList<T>) {
        super.postValue(ArrayList(value))
    }

    fun postValue(value: List<T>?) {
        if(value == null) {
            super.postValue(ArrayList())
        } else {
            super.postValue(ArrayList(value))
        }
    }

    override fun setValue(value: ArrayList<T>) {
        super.setValue(ArrayList(value))
    }

    fun setValue(value: List<T>?) {
        if(value == null) {
            super.setValue(ArrayList())
        } else {
            super.setValue(ArrayList(value))
        }
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