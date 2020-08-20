package cn.yue.base.middle.mvvm.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.yue.base.middle.components.load.LoadStatus
import cn.yue.base.middle.components.load.PageStatus

class LoaderLiveData {
    var isFirstLoad = true
    private val pageStatusLiveData = MutableLiveData<PageStatus>()
    private val loadStatusLiveData = MutableLiveData<LoadStatus>()


    var pageStatus: PageStatus?
        set(value) {
            pageStatusLiveData.value = value
        }
        get() {
            return pageStatusLiveData.value
        }

    var loadStatus: LoadStatus?
        set(value) {
            loadStatusLiveData.value = value
        }
        get() {
            return loadStatusLiveData.value
        }

    fun observePage(lifecycleOwner: LifecycleOwner, observer: Observer<in PageStatus>) {
        pageStatusLiveData.observe(lifecycleOwner, observer)
    }

    fun observeLoad(lifecycleOwner: LifecycleOwner, observer: Observer<in LoadStatus>) {
        loadStatusLiveData.observe(lifecycleOwner, observer)
    }

}