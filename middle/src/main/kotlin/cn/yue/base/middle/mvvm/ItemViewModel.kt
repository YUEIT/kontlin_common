package cn.yue.base.middle.mvvm

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import cn.yue.base.middle.router.RouterCard
import io.reactivex.SingleTransformer

abstract class ItemViewModel(private val parentViewModel: BaseViewModel)
    : BaseViewModel(parentViewModel.getApplication()) {

    abstract val itemType: Int
    abstract val layoutId: Int
    override fun <T> toBindLifecycle(): SingleTransformer<T, T> {
        return parentViewModel.toBindLifecycle()
    }

    override fun <T> toBindLifecycle(event: Lifecycle.Event): SingleTransformer<T, T> {
        return parentViewModel.toBindLifecycle(event)
    }

    override fun showWaitDialog(title: String?) {
        parentViewModel.showWaitDialog(title)
    }

    override fun dismissWaitDialog() {
        parentViewModel.dismissWaitDialog()
    }

    override fun navigation(routerCard: RouterCard) {
        parentViewModel.navigation(routerCard)
    }

    override fun navigation(routerCard: RouterCard, requestCode: Int) {
        parentViewModel.navigation(routerCard, requestCode)
    }

    override fun navigation(routerCard: RouterCard, requestCode: Int, toActivity: String?) {
        parentViewModel.navigation(routerCard, requestCode, toActivity)
    }

    override fun finish() {
        parentViewModel.finish()
    }

    override fun finishForResult(resultCode: Int, bundle: Bundle?) {
        parentViewModel.finishForResult(resultCode, bundle)
    }

}