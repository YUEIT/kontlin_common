package cn.yue.base.middle.mvvm

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import cn.yue.base.common.activity.rx.RxLifecycleTransformer
import cn.yue.base.middle.mvvm.data.RouterModel
/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class ItemViewModel(private val parentViewModel: BaseViewModel)
    : BaseViewModel(parentViewModel.getApplication()) {

    open fun getItemType(): Int = this::class.hashCode() % 100
    abstract fun getLayoutId(): Int
    override fun <T> toBindLifecycle(): RxLifecycleTransformer<T> {
        return parentViewModel.toBindLifecycle()
    }

    override fun <T> toBindLifecycle(e: Lifecycle.Event): RxLifecycleTransformer<T> {
        return parentViewModel.toBindLifecycle(e)
    }

    override fun showWaitDialog(title: String) {
        parentViewModel.showWaitDialog(title)
    }

    override fun dismissWaitDialog() {
        parentViewModel.dismissWaitDialog()
    }

    override fun navigation(routerModel: RouterModel) {
        parentViewModel.navigation(routerModel)
    }

    override fun finish() {
        parentViewModel.finish()
    }

    override fun finishForResult(resultCode: Int, bundle: Bundle?) {
        parentViewModel.finishForResult(resultCode, bundle)
    }

}