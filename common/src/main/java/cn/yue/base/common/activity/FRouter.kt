package cn.yue.base.common.activity

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.SparseArray
import cn.yue.base.common.utils.debug.ToastUtils
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter
import java.io.Serializable
import java.net.NoRouteToHostException
import java.util.*


/**
 * Created by yue on 2018/6/5.
 */

class FRouter : Parcelable {
    private var uri: Uri? = null
    private var tag: Any? = null
    private lateinit var extras: Bundle
    
    private var path: String? = null
    private var flags: Int = 0
    
    private var timeout: Int = 0
    private var enterAnim: Int = 0
    private var exitAnim: Int = 0
    private var transition: Int = 0
        
    private var isInterceptLogin: Boolean = false //是否登录拦截
    private var loginUri: String? = null //登录路径

    private object FRouterHolder {
        val instance = FRouter()
    }

    @JvmOverloads constructor(path: String? = null, uri: Uri? = null, bundle: Bundle? = null) {
        this.flags = -1
        this.timeout = 300
        this.setPath(path)
        this.setUri(uri)
        this.extras = bundle ?: Bundle()
        this.isInterceptLogin = false
        this.transition = 0
    }

    private fun clear() {
        this.extras = Bundle()
        this.path = null
        this.isInterceptLogin = false
        this.flags = 0
        this.enterAnim = 0
        this.exitAnim = 0
        this.transition = 0
    }

    fun getTag(): Any? {
        return this.tag
    }

    fun setTag(tag: Any): FRouter {
        this.tag = tag
        return this
    }

    private fun getRealEnterAnim(): Int = if (enterAnim > 0) enterAnim else TransitionAnimation.getStartEnterAnim(transition)

    private fun getRealExitAnim(): Int = if (exitAnim > 0) exitAnim else TransitionAnimation.getStartExitAnim(transition)

    fun getTransition(): Int = transition

    fun getTimeout(): Int {
        return this.timeout
    }

    fun setTimeout(timeout: Int): FRouter {
        this.timeout = timeout
        return this
    }

    fun setPath(path: String?): FRouter {
        this.path = path
        return this
    }

    fun build(path: String): FRouter {
        this.path = path
        return this
    }

    fun getPath(): String? {
        return path
    }

    fun getUri(): Uri? {
        return this.uri
    }

    fun setUri(uri: Uri?): FRouter {
        this.uri = uri
        return this
    }

    private var targetActivity: Class<*>? = null

    fun setTargetActivity(targetActivity: Class<*>?) {
        this.targetActivity = targetActivity
    }

    private fun getRouteType(): RouteType {
        if (TextUtils.isEmpty(path)) {
            throw NullPointerException("path is null")
        }
        val postcard: Postcard = ARouter.getInstance().build(path)
        try {
            LogisticsCenter.completion(postcard)
        } catch (e : NoRouteToHostException) {
            return RouteType.UNKNOWN
        }
        return postcard.type
    }

    fun navigation(context: Context) {
        this.navigation(context, null)
    }

    fun navigation(context: Context, toActivity: Class<*>?) {
        if (isInterceptLogin && interceptLogin(context)) {
            return
        }
        if (getRouteType() == RouteType.ACTIVITY) {
            jumpToActivity(context)
        } else if (getRouteType() == RouteType.FRAGMENT) {
            jumpToFragment(context, toActivity)
        } else {
            ToastUtils.showShortToast("找不到页面")
        }

        if (context is Activity) {
            context.overridePendingTransition(getRealEnterAnim(), getRealExitAnim())
        }
        val intent = Intent()
        intent.putExtra(TAG, this)
        intent.putExtras(extras)
        intent.flags = flags
        if (toActivity == null) {
            intent.setClass(context, CommonActivity::class.java)
        } else {
            intent.setClass(context, toActivity)
        }
        context.startActivity(intent)
    }

    fun navigation(context: Activity, requestCode: Int) {
        this.navigation(context, null, requestCode)
    }

    fun navigation(context: Activity, toActivity: Class<*>?, requestCode: Int) {
        if (isInterceptLogin && interceptLogin(context)) {
            return
        }
        context.overridePendingTransition(getRealEnterAnim(), getRealExitAnim())
        val intent = Intent()
        intent.putExtra(TAG, this)
        intent.putExtras(extras)
        intent.flags = flags
        if (toActivity == null) {
            intent.setClass(context, CommonActivity::class.java)
        } else {
            intent.setClass(context, toActivity)
        }
        context.startActivityForResult(intent, requestCode)
    }

    private fun jumpToActivity(context: Context) {
        jumpToActivity(context)
    }

    private fun jumpToActivity(context: Context, requestCode: Int) {
        val postcard: Postcard = ARouter.getInstance()
                .build(path)
                .withFlags(flags)
                .with(extras)
                .withTransition(getRealEnterAnim(), getRealExitAnim())
                .setTimeout(getTimeout())
        if (requestCode <= 0 || context !is Activity) {
            postcard.navigation(context)
        } else {
            postcard.navigation(context, requestCode)
        }
    }

    private fun jumpToFragment(context: Context) {
        jumpToFragment(context, null)
    }

    private fun jumpToFragment(context: Context, toActivity: Class<*>?) {
        jumpToFragment(context, toActivity, -1)
    }

    private fun jumpToFragment(context: Context, toActivity: Class<*>?, requestCode: Int) {
        val intent: Intent = Intent()
        intent.putExtra(TAG, this)
        intent.putExtras(extras)
        intent.flags = flags
        if (toActivity == null) {
            if (targetActivity == null) {
                intent.setClass(context, CommonActivity::class.java)
            } else {
                intent.setClass(context, targetActivity)
            }
        } else {
            intent.setClass(context, toActivity)
        }
        if (requestCode <= 0) {
            context.startActivity(intent)
        } else {
            if (context is Activity) {
                context.startActivityForResult(intent, requestCode)
            }
        }
        if (context is Activity) {
            context.overridePendingTransition(getRealEnterAnim(), getRealExitAnim())
        }
    }

    fun with(bundle: Bundle?): FRouter {
        if (null != bundle) {
            this.extras = bundle
        }

        return this
    }

    fun withFlags(flag: Int): FRouter {
        this.flags = flag
        return this
    }

    fun withString(key: String?, value: String?): FRouter {
        this.extras.putString(key, value)
        return this
    }

    fun withBoolean(key: String, value: Boolean): FRouter {
        this.extras.putBoolean(key, value)
        return this
    }

    fun withShort(key: String, value: Short): FRouter {
        this.extras.putShort(key, value)
        return this
    }

    fun withInt(key: String, value: Int): FRouter {
        this.extras.putInt(key, value)
        return this
    }

    fun withLong(key: String, value: Long): FRouter {
        this.extras.putLong(key, value)
        return this
    }

    fun withDouble(key: String, value: Double): FRouter {
        this.extras.putDouble(key, value)
        return this
    }

    fun withByte(key: String, value: Byte): FRouter {
        this.extras.putByte(key, value)
        return this
    }

    fun withChar(key: String, value: Char): FRouter {
        this.extras.putChar(key, value)
        return this
    }

    fun withFloat(key: String, value: Float): FRouter {
        this.extras.putFloat(key, value)
        return this
    }

    fun withCharSequence(key: String, value: CharSequence?): FRouter {
        this.extras.putCharSequence(key, value)
        return this
    }

    fun withParcelable(key: String, value: Parcelable?): FRouter {
        this.extras.putParcelable(key, value)
        return this
    }

    fun withParcelableArray(key: String, value: Array<Parcelable>?): FRouter {
        this.extras.putParcelableArray(key, value)
        return this
    }

    fun withParcelableArrayList(key: String, value: ArrayList<out Parcelable>?): FRouter {
        this.extras.putParcelableArrayList(key, value)
        return this
    }

    fun withSparseParcelableArray(key: String, value: SparseArray<out Parcelable>?): FRouter {
        this.extras.putSparseParcelableArray(key, value)
        return this
    }

    fun withIntegerArrayList(key: String, value: ArrayList<Int>?): FRouter {
        this.extras.putIntegerArrayList(key, value)
        return this
    }

    fun withStringArrayList(key: String, value: ArrayList<String>?): FRouter {
        this.extras.putStringArrayList(key, value)
        return this
    }

    fun withCharSequenceArrayList(key: String, value: ArrayList<CharSequence>?): FRouter {
        this.extras.putCharSequenceArrayList(key, value)
        return this
    }

    fun withSerializable(key: String, value: Serializable?): FRouter {
        this.extras.putSerializable(key, value)
        return this
    }

    fun withByteArray(key: String, value: ByteArray?): FRouter {
        this.extras.putByteArray(key, value)
        return this
    }

    fun withShortArray(key: String, value: ShortArray?): FRouter {
        this.extras.putShortArray(key, value)
        return this
    }

    fun withCharArray(key: String, value: CharArray?): FRouter {
        this.extras.putCharArray(key, value)
        return this
    }

    fun withFloatArray(key: String, value: FloatArray?): FRouter {
        this.extras.putFloatArray(key, value)
        return this
    }

    fun withCharSequenceArray(key: String, value: Array<CharSequence>?): FRouter {
        this.extras.putCharSequenceArray(key, value)
        return this
    }

    fun withBundle(key: String, value: Bundle?): FRouter {
        this.extras.putBundle(key, value)
        return this
    }

    fun withTransition(enterAnim: Int, exitAnim: Int): FRouter {
        this.enterAnim = enterAnim
        this.exitAnim = exitAnim
        return this
    }

    fun withTransitionStyle(transitionStyle: Int): FRouter {
        this.transition = transitionStyle
        return this
    }

    override fun toString(): String {
        return "FRouter{uri=" + this.uri + ", tag=" + this.tag + ", mBundle=" + this.extras + ", flags=" + this.flags + ", timeout=" + this.timeout + ", provider=" + ", greenChannel=" + ", enterAnim=" + this.enterAnim + ", exitAnim=" + this.exitAnim + "}\n" + super.toString()
    }

    @JvmOverloads
    fun setInterceptLogin(): FRouter {
        isInterceptLogin = true
        return this
    }

    private var onInterceptLoginListener : ((context: Context) -> Boolean)? = null

    fun setOnInterceptLoginlistener(onInterceptLoginListener: ((context: Context) -> Boolean)?) {
        this.onInterceptLoginListener = onInterceptLoginListener
    }

    private fun interceptLogin(context: Context) : Boolean {
        if (onInterceptLoginListener != null) {
            return onInterceptLoginListener!!(context)
        }
        return false
    }

    companion object {

        val TAG = "FRouter"

        @JvmStatic
        val instance: FRouter
            get() {
                val fRouter = FRouterHolder.instance
                fRouter.clear()
                return fRouter
            }
        @JvmField
        val CREATOR: Parcelable.Creator<FRouter> = object : Parcelable.Creator<FRouter> {
            override fun createFromParcel(source: Parcel): FRouter = FRouter(source)
            override fun newArray(size: Int): Array<FRouter?> = arrayOfNulls(size)
        }

        @JvmStatic
        fun init(application: Application) {
            ARouter.init(application)
        }

        @JvmStatic
        fun debug() {
            ARouter.openLog()
            ARouter.openDebug()
        }
    }

    constructor(source: Parcel) {
        uri = source.readParcelable(Uri::class.java.classLoader)
        //mBundle = in.readBundle();
        path = source.readString()
        flags = source.readInt()
        timeout = source.readInt()
        enterAnim = source.readInt()
        exitAnim = source.readInt()
        transition = source.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest){
        dest.writeParcelable(uri, flags)
        //dest.writeBundle(mBundle);
        dest.writeString(path)
        dest.writeInt(flags)
        dest.writeInt(timeout)
        dest.writeInt(enterAnim)
        dest.writeInt(exitAnim)
        dest.writeInt(transition)
    }



}

