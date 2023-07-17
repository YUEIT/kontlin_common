package cn.yue.test

import android.os.Bundle
import android.widget.TextView
import cn.yue.base.activity.BaseDialogFragment
import cn.yue.base.activity.TransitionAnimation.TRANSITION_CENTER


/**
 * Description :
 * Created by yue on 2022/11/17
 */

class UserAuthDialog : BaseDialogFragment(){

    override fun getLayoutId(): Int {
        return R.layout.dialog_user_permission
    }

    override fun initView(savedInstanceState: Bundle?) {
        dialog?.setCanceledOnTouchOutside(false)
        view?.findViewById<TextView>(R.id.tv_close)?.setOnClickListener {
            dismiss()
            confirmBlock?.invoke()
        }
    }

    override fun initEnterStyle() {
        setEnterStyle(TRANSITION_CENTER)
    }

    private var confirmBlock: (()->Unit)? = null

    fun setConfirmListener(confirmBlock: (()->Unit)) {
        this.confirmBlock = confirmBlock
    }
}