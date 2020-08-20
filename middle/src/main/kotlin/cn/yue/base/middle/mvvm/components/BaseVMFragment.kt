package cn.yue.base.middle.mvvm.components

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.activity.rx.ILifecycleProvider
import cn.yue.base.common.widget.dialog.WaitDialog
import cn.yue.base.middle.mvp.IWaitView
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.router.FRouter
import java.lang.reflect.ParameterizedType

abstract class BaseVMFragment<VM : BaseViewModel> : BaseFragment(), IWaitView {

    lateinit var viewModel: VM
    override fun onCreate(savedInstanceState: Bundle?) {
        var viewModel = initViewModel()
        if (viewModel == null) {
            val modelClass: Class<VM>
            val type = javaClass.genericSuperclass
            modelClass = if (type is ParameterizedType) {
                type.actualTypeArguments[0] as Class<VM>
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                BaseViewModel::class.java as Class<VM>
            }
            viewModel = createViewModel(modelClass)
        }
        this.viewModel = viewModel
        super.onCreate(savedInstanceState)
    }

    override fun initLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return viewModel
    }

    open fun initViewModel(): VM? {
        return null
    }

    open fun createViewModel(cls: Class<VM>): VM {
        return ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(mActivity.application))[cls]
    }

    override fun initOther() {
        super.initOther()
        viewModel.waitEvent.observe(this, Observer { s ->
            if (TextUtils.isEmpty(s)) {
                dismissWaitDialog()
            } else {
                showWaitDialog(s)
            }
        })
        viewModel.routerEvent.observe(this, Observer { (routerCard, requestCode, toActivity) ->
            FRouter.instance
                    .bindRouterCard(routerCard)
                    .navigation(mActivity, toActivity, requestCode)
        })
        viewModel.finishEvent.observe(this, Observer { (resultCode, bundle) ->
            if (resultCode < 0) {
                finishAll()
            } else {
                val intent = Intent()
                bundle?.let {
                    intent.putExtras(it)
                }
                finishAllWithResult(resultCode, intent)
            }
        })
    }

    private var waitDialog: WaitDialog? = null
    override fun showWaitDialog(title: String?) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(mActivity)
        }
        waitDialog!!.show(title, true, null)
    }

    override fun dismissWaitDialog() {
        if (waitDialog != null && waitDialog!!.isShowing()) {
            waitDialog!!.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }
}