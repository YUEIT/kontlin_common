package cn.yue.base.wx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import cn.yue.base.utils.Utils
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Description :
 * Created by yue on 2020/12/13
 */
class WXHelper: LifecycleObserver {

    companion object {
        const val WX_AUTH_RECEIVER_ACTION = "wx_auth_receiver_action"
        const val WX_SHARE_RECEIVER_ACTION = "wx_auth_receiver_action"
        const val KEY_WX_AUTH_CODE = "key_wx_auth_code"
        const val KEY_WX_AUTH_CANCEL_CODE = "key_wx_auth_cancel_code"
        const val KEY_WX_SHARE_CALL_BACK = "key_wx_share_call_back"

        val instance by lazy { WXHelper() }
    }

    private var appId: String = ""
    private var appSecret: String = ""
    private var api: IWXAPI = WXAPIFactory.createWXAPI(Utils.getContext(), appId, true)
    private var loginCallback: LoginCallback? = null
    private var wxAuthReceiver: BroadcastReceiver? = null
    private var shareCallback: ShareCallback? = null
    private var wxShareReceiver: BroadcastReceiver? = null

    init {
        api.registerApp(appId)
    }

    fun login(context: Context, loginCallback: LoginCallback) {
        this.loginCallback = loginCallback
        if (!isVerify()) {
            return
        }
        if (context is LifecycleOwner) {
            (context as LifecycleOwner).lifecycle.addObserver(this)
        }
        if (wxAuthReceiver == null) {
            wxAuthReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val code = intent?.getStringExtra(KEY_WX_AUTH_CODE)
                    if (code.equals(KEY_WX_AUTH_CANCEL_CODE)) {
                        if (context != null) {
                            loginCallback.failure("已取消")
                        }
                        return
                    }
                    if (code != null) {
	                    WXApi.getAccessToken(appId, appSecret, code, loginCallback)
                    }
                }
            }
            LocalBroadcastManager.getInstance(context).registerReceiver(wxAuthReceiver!!, IntentFilter(
	            WX_AUTH_RECEIVER_ACTION
            ))
        }
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = getAppStateName(context) + "_app"
        api.sendReq(req)
    }

    private fun isVerify(): Boolean {
        if (!api.isWXAppInstalled) {
            loginCallback?.failure("微信未安装")
            return false
        }
        return true
    }

    fun share(context: Context, shareInfo: WXShareEntity, shareCallback: ShareCallback) {
        this.shareCallback = shareCallback
        if (!isVerify()) {
            shareCallback.failure("微信未安装")
            return
        }
        if (context is LifecycleOwner) {
            (context as LifecycleOwner).lifecycle.addObserver(this)
        }
        val isSharePYQ = shareInfo.type == WXShareEntity.SHARE_TO_PYQ
        if (isSharePYQ && api.wxAppSupportAPI < 0x21020001) {
            shareCallback.failure("微信4.2以上才支持分享朋友圈，请升级微信")
            return
        }
        if (wxShareReceiver == null) {
            wxShareReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val shareSuccess = intent?.getBooleanExtra(KEY_WX_SHARE_CALL_BACK, false)?: false
                    if (shareSuccess) {
                        shareCallback.success()
                    } else {
                        shareCallback.failure("分享失败")
                    }
                }
            }
            LocalBroadcastManager.getInstance(context).registerReceiver(wxShareReceiver!!, IntentFilter(
	            WX_SHARE_RECEIVER_ACTION
            ))
        }
        val req = SendMessageToWX.Req()
        req.message = createMessage(req, shareInfo.params)
        if (req.message == null) {
            return
        }
        req.scene = if (isSharePYQ) {
            SendMessageToWX.Req.WXSceneTimeline
        } else {
            SendMessageToWX.Req.WXSceneSession
        }
        api.sendReq(req)
    }

    private fun createMessage(req: SendMessageToWX.Req, params: Bundle): WXMediaMessage? {
        return when (params.getInt(WXShareEntity.KEY_WX_TYPE)) {
            WXShareEntity.TYPE_TEXT -> textContent(req, params)
            WXShareEntity.TYPE_IMAGE -> imageContent(req, params)
            WXShareEntity.TYPE_MUSIC -> musicContent(req, params)
            WXShareEntity.TYPE_VIDEO -> videoContent(req, params)
            WXShareEntity.TYPE_WEB -> webContent(req, params)
            else -> null
        }
    }

    private fun textContent(req: SendMessageToWX.Req, params: Bundle): WXMediaMessage {
        val msg = WXMediaMessage()
        val textObj = WXTextObject()
        textObj.text = params.getString(WXShareEntity.KEY_WX_TEXT)
        msg.mediaObject = textObj
        msg.description = textObj.text
        req.transaction = buildTransaction("text")
        return msg
    }

    private fun imageContent(req: SendMessageToWX.Req, params: Bundle): WXMediaMessage? {
        val msg = WXMediaMessage()
        val imgObj: WXImageObject
        val bitmap: Bitmap
        //分为本地文件和应用内资源图片
        if (params.containsKey(WXShareEntity.KEY_WX_IMG_LOCAL)) {
            val imgUrl = params.getString(WXShareEntity.KEY_WX_IMG_LOCAL, "")
            if (notFoundFile(imgUrl)) {
                return null
            }
            imgObj = WXImageObject()
            imgObj.imagePath = imgUrl
            bitmap = BitmapFactory.decodeFile(imgUrl)
        } else {
            bitmap = BitmapFactory.decodeResource(
	            Utils.getContext().resources, params.getInt(
		            WXShareEntity.KEY_WX_IMG_RES
            ))
            imgObj = WXImageObject(bitmap)
        }
        msg.mediaObject = imgObj
        msg.thumbData = bmpToByteArray(bitmap, true)
        req.transaction = buildTransaction("img")
        return msg
    }

    private fun musicContent(req: SendMessageToWX.Req, params: Bundle): WXMediaMessage? {
        val msg = WXMediaMessage()
        val musicObject = WXMusicObject()
        musicObject.musicUrl = params.getString(WXShareEntity.KEY_WX_MUSIC_URL)
        msg.mediaObject = musicObject
        if (addTitleSummaryAndThumb(msg, params)) return null
        req.transaction = buildTransaction("music")
        return msg
    }

    private fun videoContent(req: SendMessageToWX.Req, params: Bundle): WXMediaMessage? {
        val msg = WXMediaMessage()
        val musicObject = WXVideoObject()
        musicObject.videoUrl = params.getString(WXShareEntity.KEY_WX_VIDEO_URL)
        msg.mediaObject = musicObject
        if (addTitleSummaryAndThumb(msg, params)) return null
        req.transaction = buildTransaction("video")
        return msg
    }

    private fun webContent(req: SendMessageToWX.Req, params: Bundle): WXMediaMessage? {
        val msg = WXMediaMessage()
        val musicObject = WXWebpageObject()
        musicObject.webpageUrl = params.getString(WXShareEntity.KEY_WX_WEB_URL)
        msg.mediaObject = musicObject
        if (addTitleSummaryAndThumb(msg, params)) return null
        req.transaction = buildTransaction("webpage")
        return msg
    }

    private fun addTitleSummaryAndThumb(msg: WXMediaMessage, params: Bundle): Boolean {
        if (params.containsKey(WXShareEntity.KEY_WX_TITLE)) {
            msg.title = params.getString(WXShareEntity.KEY_WX_TITLE)
        }
        if (params.containsKey(WXShareEntity.KEY_WX_SUMMARY)) {
            msg.description = params.getString(WXShareEntity.KEY_WX_SUMMARY)
        }
        if (params.containsKey(WXShareEntity.KEY_WX_IMG_LOCAL) || params.containsKey(WXShareEntity.KEY_WX_IMG_RES)) {
            val bitmap: Bitmap
            bitmap = if (params.containsKey(WXShareEntity.KEY_WX_IMG_LOCAL)) { //分为本地文件和应用内资源图片
                val imgUrl = params.getString(WXShareEntity.KEY_WX_IMG_LOCAL, "")
                if (notFoundFile(imgUrl)) {
                    return true
                }
                BitmapFactory.decodeFile(imgUrl)
            } else {
                BitmapFactory.decodeResource(
	                Utils.getContext().resources, params.getInt(
		                WXShareEntity.KEY_WX_IMG_RES
                ))
            }
            msg.thumbData = bmpToByteArray(bitmap, true)
        }
        return false
    }

    private fun notFoundFile(filePath: String): Boolean {
        if (!TextUtils.isEmpty(filePath)) {
            val file = File(filePath)
            if (!file.exists()) {
                shareCallback?.failure("分享图片未找到")
                return true
            }
        } else {
            if (shareCallback != null) {
                shareCallback?.failure("分享图片未找到")
            }
            return true
        }
        return false
    }

    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }

    private fun bmpToByteArray(bmp: Bitmap, needThumb: Boolean): ByteArray? {
        val newBmp: Bitmap
        if (needThumb) {
            var width = bmp.width
            var height = bmp.height
            if (width > height) {
                height = height * 150 / width
                width = 150
            } else {
                width = width * 150 / height
                height = 150
            }
            newBmp = Bitmap.createScaledBitmap(bmp, width, height, true)
        } else {
            newBmp = bmp
        }
        val output = ByteArrayOutputStream()
        newBmp.compress(Bitmap.CompressFormat.JPEG, 100, output)
        val result = output.toByteArray()
        try {
            output.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (!bmp.isRecycled) {
                bmp.recycle()
            }
            if (!newBmp.isRecycled) {
                newBmp.recycle()
            }
        }
        return result
    }

    private fun getAppStateName(context: Context): String? {
        val packageName = context.packageName
        var beginIndex = 0
        if (packageName.contains(".")) {
            beginIndex = packageName.lastIndexOf(".")
        }
        return packageName.substring(beginIndex)
    }

    /**
     * 微信登录，在微信回调到WXEntryActivity的onResp方法中调用
     */
    fun sendAuthBackBroadcast(context: Context?, mCode: String?) {
        var code = mCode
        val intent = Intent(WX_AUTH_RECEIVER_ACTION)
        if (TextUtils.isEmpty(code)) {
            code = KEY_WX_AUTH_CANCEL_CODE
        }
        intent.putExtra(KEY_WX_AUTH_CODE, code)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    /**
     * 微信分享，在微信回调到WXEntryActivity的onResp方法中调用
     */
    fun sendShareBackBroadcast(context: Context?, success: Boolean) {
        val intent = Intent(WX_SHARE_RECEIVER_ACTION)
        intent.putExtra(KEY_WX_SHARE_CALL_BACK, success)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    fun handleIntent(intent: Intent?, handler: IWXAPIEventHandler) {
        api.handleIntent(intent, handler)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unregisterReceiver() {
        if (wxShareReceiver != null) {
            LocalBroadcastManager.getInstance(Utils.getContext()).unregisterReceiver(wxShareReceiver!!)
        }
        if (wxAuthReceiver != null) {
            LocalBroadcastManager.getInstance(Utils.getContext()).unregisterReceiver(wxAuthReceiver!!)
        }
    }
}