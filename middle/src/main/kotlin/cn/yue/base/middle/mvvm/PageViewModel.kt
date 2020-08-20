package cn.yue.base.middle.mvvm

import android.app.Application
import cn.yue.base.middle.net.wrapper.BaseListBean

abstract class PageViewModel<S>(application: Application) : ListViewModel<BaseListBean<S>, S>(application)