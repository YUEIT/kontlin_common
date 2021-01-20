package cn.yue.base.kotlin.test.feature

import android.app.Application
import androidx.lifecycle.viewModelScope
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.kotlin.test.mode.ApiManager
import cn.yue.base.kotlin.test.mode.TokenBean
import cn.yue.base.kotlin.test.mode.UserBean
import cn.yue.base.kotlin.test.utils.LocalStorage
import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.coroutine.request
import cn.yue.base.middle.net.observer.BaseNetObserver

/**
 * Description :
 * Created by yue on 2021/1/15
 */
class LoginViewModel(application: Application): BaseViewModel(application) {

    fun login(phone: String, password: String) {
        viewModelScope.request({
            val body = mutableMapOf(
                    Pair("phone", phone),
                    Pair("password", password)
            )
            ApiManager.getApi().login(body)
        }, object : BaseNetObserver<TokenBean>() {

            override fun onStart() {
                super.onStart()
                showWaitDialog()
            }

            override fun onException(e: ResultException) {
                dismissWaitDialog()
                ToastUtils.showShortToast(e.message)
            }

            override fun onSuccess(t: TokenBean) {
                InitConstant.setToken(t.token)
                getUserInfo()
            }
        })
    }

    fun getUserInfo() {
        viewModelScope.request({
            ApiManager.getApi().getUserInfo()
        }, object : BaseNetObserver<UserBean>() {

            override fun onException(e: ResultException) {
                dismissWaitDialog()
                ToastUtils.showShortToast(e.message)
            }

            override fun onSuccess(t: UserBean) {
                ToastUtils.showShortToast("登录成功")
                LocalStorage.setUserInfo(t)
                LocalStorage.setToken(InitConstant.getToken())
                finish()
            }

        })
    }
}