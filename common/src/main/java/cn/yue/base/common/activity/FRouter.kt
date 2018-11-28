package cn.yue.base.common.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.SparseArray
import cn.yue.base.common.Constant
import cn.yue.base.common.R
import com.alibaba.android.arouter.launcher.ARouter
import java.io.Serializable
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
        
    private var isInterceptLogin: Boolean = false //是否登录拦截
    private var loginUri: String? = null //登录路径

    private fun getRealEnterAnim(): Int = if (enterAnim <= 0) R.anim.right_in else enterAnim

    private fun getRealExitAnim(): Int = if (exitAnim <= 0) R.anim.left_out else exitAnim

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
        this.loginUri = null
    }

    private fun clear() {
        this.extras = Bundle()
        this.path = null
        this.isInterceptLogin = false
        this.loginUri = null
        this.flags = 0
        this.enterAnim = 0
        this.exitAnim = 0
    }

    fun getTag(): Any? {
        return this.tag
    }

    fun setTag(tag: Any): FRouter {
        this.tag = tag
        return this
    }


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

    fun navigation(context: Context) {
        this.navigation(context, null)
    }

    fun navigation(context: Context, toActivity: Class<*>?) {
        if (isInterceptLogin && interceptLogin(context)) {
            return
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

    override fun toString(): String {
        return "FRouter{uri=" + this.uri + ", tag=" + this.tag + ", mBundle=" + this.extras + ", flags=" + this.flags + ", timeout=" + this.timeout + ", provider=" + ", greenChannel=" + ", enterAnim=" + this.enterAnim + ", exitAnim=" + this.exitAnim + "}\n" + super.toString()
    }

    @JvmOverloads
    fun setInterceptLogin(loginUri: String? = null): FRouter {
        isInterceptLogin = true
        this.loginUri = loginUri
        return this
    }

    private fun interceptLogin(context: Context): Boolean {
        if (!Constant.LOGINED) {
            ARouter.getInstance().build(if (TextUtils.isEmpty(loginUri)) "/app/login" else loginUri)
                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .navigation(context)
            return true
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
    }

    constructor(source: Parcel) {
        uri = source.readParcelable(Uri::class.java.classLoader)
        //mBundle = in.readBundle();
        path = source.readString()
        flags = source.readInt()
        timeout = source.readInt()
        enterAnim = source.readInt()
        exitAnim = source.readInt()
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
    }



}

