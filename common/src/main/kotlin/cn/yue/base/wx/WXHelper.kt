package cn.yue.base.wx

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
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
class WXHelper: DefaultLifecycleObserver {

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
    private val loginResult = MutableLiveData<ResultParams>()
    private var shareCallback: ShareCallback? = null
    private val shareResult = MutableLiveData<ResultParams>()

    init {
        api.registerApp(appId)
        loginResult.observeForever {
            loginCallback?.apply {
                if (it.resultCode == 0) {
                    if (it.code != null) {
                        WXApi.getAccessToken(appId, appSecret, it.code, this)
                    }
                } else {
                    failure("授权失败")
                }
            }
        }
        shareResult.observeForever {
            shareCallback?.apply {
                if (it.resultCode == 0) {
                    success()
                } else {
                    failure("分享失败")
                }
            }
        }
    }

    fun login(context: Context, loginCallback: LoginCallback) {
        this.loginCallback = loginCallback
        if (!isVerify()) {
            return
        }
        if (context is LifecycleOwner) {
            (context as LifecycleOwner).lifecycle.addObserver(this)
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
            val bitmap = if (params.containsKey(WXShareEntity.KEY_WX_IMG_LOCAL)) { //分为本地文件和应用内资源图片
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
        return if (type == null) {
            System.currentTimeMillis().toString()
        } else {
            type + System.currentTimeMillis()
        }
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
    fun sendAuthResult(resultCode: Int, mCode: String?) {
        loginResult.value = ResultParams(resultCode, mCode)
    }

    /**
     * 微信分享，在微信回调到WXEntryActivity的onResp方法中调用
     */
    fun sendShareResult(resultCode: Int, result: String?) {
        shareResult.value = ResultParams(resultCode, result)
    }

    fun handleIntent(intent: Intent?, handler: IWXAPIEventHandler) {
        api.handleIntent(intent, handler)
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        loginCallback = null
        shareCallback = null
    }
    
    data class ResultParams(
        val resultCode: Int,
        val code: String?)
}