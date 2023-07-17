package cn.yue.base.mvp

import androidx.lifecycle.Lifecycle
import cn.yue.base.activity.rx.ILifecycleProvider

/**
 * Description :
 * Created by yue on 2019/3/13
 */
interface IBaseView : IStatusView, IWaitView {
    fun getLifecycleProvider(): ILifecycleProvider<Lifecycle.Event>
}