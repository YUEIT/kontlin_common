package cn.yue.base.mvvm

import android.app.Application
import cn.yue.base.net.wrapper.BaseListBean
/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class PageViewModel<S>(application: Application) : ListViewModel<BaseListBean<S>, S>(application)