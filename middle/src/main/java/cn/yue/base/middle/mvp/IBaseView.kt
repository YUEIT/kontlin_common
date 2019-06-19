package cn.yue.base.middle.mvp

import cn.yue.base.common.activity.ILifecycleProvider
import com.trello.rxlifecycle2.android.FragmentEvent

/**
 * Description :
 * Created by yue on 2019/6/18
 */
interface IBaseView : IStatusView, IWaitView, ILifecycleProvider<FragmentEvent>