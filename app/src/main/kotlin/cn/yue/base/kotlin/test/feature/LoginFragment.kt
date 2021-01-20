package cn.yue.base.kotlin.test.feature

import android.os.Bundle
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.kotlin.test.R
import cn.yue.base.middle.mvvm.components.BaseHintVMFragment
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Description :
 * Created by yue on 2021/1/15
 */
@Route(path = "/app/login")
class LoginFragment: BaseHintVMFragment<LoginViewModel>() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        loginTV.setOnClickListener {
            val account = accountET.text.toString()
            val password = passwordET.text.toString()
            if (account.isEmpty()) {
                ToastUtils.showShortToast("账户输入为空！")
                return@setOnClickListener
            }
            viewModel.login(account, password)
        }
    }
}