package cn.yue.base.mvvm.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
/**
 * Description :
 * Created by yue on 2020/8/8
 */
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

    fun isLoading(): Boolean {
        return loadStatus == LoadStatus.LOAD_MORE
                || loadStatus == LoadStatus.REFRESH
                || pageStatus == PageStatus.REFRESH
    }
}