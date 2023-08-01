package cn.yue.base.wx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

/**
 * Description :
 * Created by yue on 2020/12/14
 */
open class WXHelperActivity: AppCompatActivity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WXHelper.instance.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WXHelper.instance.handleIntent(intent, this)
    }

    override fun onResp(p0: BaseResp?) {
        if (p0 == null) return
        if (p0.type == ConstantsAPI.COMMAND_SENDAUTH) {
            if (p0.errCode == BaseResp.ErrCode.ERR_OK) {
                val code = (p0 as SendAuth.Resp).code
                WXHelper.instance.sendAuthResult(p0.errCode, code)
            } else {
                WXHelper.instance.sendAuthResult(p0.errCode, null)
            }
        } else if (p0.type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            WXHelper.instance.sendShareResult(p0.errCode, null)
        }
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onReq(p0: BaseReq?) {

    }

}