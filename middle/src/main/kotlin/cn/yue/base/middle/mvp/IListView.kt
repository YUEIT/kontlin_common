package cn.yue.base.middle.mvp

import cn.yue.base.middle.mvp.components.data.Loader

/**
 * Description :
 * Created by yue on 2022/3/8
 */
interface IListView<S> : IBaseView {
    fun getLoader(): Loader
    fun setData(list: List<S>)
}