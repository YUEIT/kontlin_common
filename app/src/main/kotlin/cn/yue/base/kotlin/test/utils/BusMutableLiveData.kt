package cn.yue.base.kotlin.test.utils

class BusMutableLiveData<T> : BusLiveData<T>() {

    public override fun postValue(value: T) {
        super.postValue(value)
    }

    public override fun setValue(value: T) {
        super.setValue(value)
    }
}