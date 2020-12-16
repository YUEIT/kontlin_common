package cn.yue.base.middle.wx

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        WXHelper.getInstance().handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        WXHelper.getInstance().handleIntent(intent, this)
    }

    override fun onResp(p0: BaseResp?) {
        Log.d("luobiao", "onResp: " + p0?.type + " ; " + p0?.errCode)
        if (p0 == null) return
        if (p0.type == ConstantsAPI.COMMAND_SENDAUTH) {
            if (p0.errCode == BaseResp.ErrCode.ERR_OK) {
                val code = (p0 as SendAuth.Resp).code
                WXHelper.getInstance().sendAuthBackBroadcast(this, code)
            } else {
                WXHelper.getInstance().sendAuthBackBroadcast(this, null)
            }
        } else if (p0.type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            if (p0.errCode == BaseResp.ErrCode.ERR_OK) {
                WXHelper.getInstance().sendShareBackBroadcast(this, true)
            } else {
                WXHelper.getInstance().sendShareBackBroadcast(this, false)
            }
        }
        onBackPressed()
    }

    override fun onReq(p0: BaseReq?) {

    }

}