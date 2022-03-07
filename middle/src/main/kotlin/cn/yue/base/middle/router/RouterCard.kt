package cn.yue.base.middle.router

import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.SparseArray
import cn.yue.base.common.activity.TransitionAnimation.getStartEnterAnim
import cn.yue.base.common.activity.TransitionAnimation.getStartExitAnim
import java.io.Serializable
import java.lang.ref.WeakReference

/**
 * Description : 路由数据
 * Created by yue on 2020/4/22
 */
class RouterCard() : INavigation(), Parcelable {

    constructor(navigation: INavigation?): this() {
        this.navigation = WeakReference<INavigation>(navigation)
    }

    private var uri: Uri? = null

    private var tag: Any? = null

    private var extras: Bundle = Bundle()

    private var pactUrl: String? = null

    private var path: String? = null

    private var flags = 0

    private var timeout = 0

    private var enterAnim = 0

    private var exitAnim = 0

    private var transition = 0 //入场方式

    private var isInterceptLogin = false //是否登录拦截

    private var navigation: WeakReference<INavigation>? = null

    fun setNavigationImpl(navigation: INavigation) {
        this.navigation = WeakReference<INavigation>(navigation)
    }

    fun clear() {
        extras = Bundle()
        path = null
        isInterceptLogin = false
        flags = 0
        enterAnim = 0
        exitAnim = 0
        transition = 0
    }

    fun setTag(tag: Any): RouterCard {
        this.tag = tag
        return this
    }

    fun getRealEnterAnim(): Int {
        return if (enterAnim > 0) {
            enterAnim
        } else {
            getStartEnterAnim(transition)
        }
    }

    fun getRealExitAnim(): Int {
        return if (exitAnim > 0) {
            exitAnim
        } else {
            getStartExitAnim(transition)
        }
    }

    fun setTimeout(timeout: Int): RouterCard {
        this.timeout = timeout
        return this
    }

    fun setPactUrl(pactUrl: String?): RouterCard {
        this.pactUrl = pactUrl
        return this
    }

    fun setPath(path: String?): RouterCard {
        this.path = path
        return this
    }

    fun getPath(): String? {
        if (TextUtils.isEmpty(path) && !TextUtils.isEmpty(pactUrl)) {
            val split = pactUrl!!.split("://".toRegex()).toTypedArray()
            if (split.size == 2) {
                path = "/" + split[1]
            }
        }
        return path
    }

    fun setUri(uri: Uri?): RouterCard {
        this.uri = uri
        return this
    }

    fun with(bundle: Bundle?): RouterCard {
        if (null != bundle) {
            extras = bundle
        }
        return this
    }

    fun withFlags(flag: Int): RouterCard {
        flags = flag
        return this
    }

    fun withString(key: String?, value: String?): RouterCard {
        extras.putString(key, value)
        return this
    }

    fun withBoolean(key: String?, value: Boolean): RouterCard {
        extras.putBoolean(key, value)
        return this
    }

    fun withShort(key: String?, value: Short): RouterCard {
        extras.putShort(key, value)
        return this
    }

    fun withInt(key: String?, value: Int): RouterCard {
        extras.putInt(key, value)
        return this
    }

    fun withLong(key: String?, value: Long): RouterCard {
        extras.putLong(key, value)
        return this
    }

    fun withDouble(key: String?, value: Double): RouterCard {
        extras.putDouble(key, value)
        return this
    }

    fun withByte(key: String?, value: Byte): RouterCard {
        extras.putByte(key, value)
        return this
    }

    fun withChar(key: String?, value: Char): RouterCard {
        extras.putChar(key, value)
        return this
    }

    fun withFloat(key: String?, value: Float): RouterCard {
        extras.putFloat(key, value)
        return this
    }

    fun withCharSequence(key: String?, value: CharSequence?): RouterCard {
        extras.putCharSequence(key, value)
        return this
    }

    fun withParcelable(key: String?, value: Parcelable?): RouterCard {
        extras.putParcelable(key, value)
        return this
    }

    fun withParcelableArray(key: String?, value: Array<Parcelable?>?): RouterCard {
        extras.putParcelableArray(key, value)
        return this
    }

    fun withParcelableArrayList(key: String?, value: ArrayList<out Parcelable?>?): RouterCard {
        extras.putParcelableArrayList(key, value)
        return this
    }

    fun withSparseParcelableArray(key: String?, value: SparseArray<out Parcelable?>?): RouterCard {
        extras.putSparseParcelableArray(key, value)
        return this
    }

    fun withIntegerArrayList(key: String?, value: ArrayList<Int?>?): RouterCard {
        extras.putIntegerArrayList(key, value)
        return this
    }

    fun withStringArrayList(key: String?, value: ArrayList<String>): RouterCard {
        extras.putStringArrayList(key, value)
        return this
    }

    fun withCharSequenceArrayList(key: String?, value: ArrayList<CharSequence?>?): RouterCard {
        extras.putCharSequenceArrayList(key, value)
        return this
    }

    fun withSerializable(key: String?, value: Serializable?): RouterCard {
        extras.putSerializable(key, value)
        return this
    }

    fun withByteArray(key: String?, value: ByteArray?): RouterCard {
        extras.putByteArray(key, value)
        return this
    }

    fun withShortArray(key: String?, value: ShortArray?): RouterCard {
        extras.putShortArray(key, value)
        return this
    }

    fun withCharArray(key: String?, value: CharArray?): RouterCard {
        extras.putCharArray(key, value)
        return this
    }

    fun withFloatArray(key: String?, value: FloatArray?): RouterCard {
        extras.putFloatArray(key, value)
        return this
    }

    fun withCharSequenceArray(key: String?, value: Array<CharSequence?>?): RouterCard {
        extras.putCharSequenceArray(key, value)
        return this
    }

    fun withBundle(key: String?, value: Bundle?): RouterCard {
        extras.putBundle(key, value)
        return this
    }

    fun withMap(map: Map<String, Any?>): RouterCard {
        for ((key, value) in map) {
            putAny(key, value)
        }
        return this
    }

    private fun putAny(key: String, any: Any?) {
        if (any == null) {
            return
        }
        if (any is String) {
            extras.putString(key, any)
        } else if (any is Boolean) {
            extras.putBoolean(key, any)
        } else if (any is Int) {
            extras.putInt(key, any)
        } else if (any is Float) {
            extras.putFloat(key, any)
        } else if (any is Double) {
            extras.putDouble(key, any)
        } else if (any is Long) {
            extras.putLong(key, any)
        } else if (any is Map<*, *>) {
            extras.putSerializable(key, BundleMap(any))
        } else if (any is ArrayList<*>) {
            if (any.isNotEmpty()) {
                val ob = any[0]
                if (ob is String) {
                    extras.putStringArrayList(key, any as ArrayList<String>)
                } else if (ob is Int) {
                    extras.putIntegerArrayList(key, any as ArrayList<Int>)
                } else {
                    extras.putParcelableArrayList(key, any as ArrayList<out Parcelable>)
                }
            } else {
                extras.putParcelableArrayList(key, any as ArrayList<out Parcelable>)
            }
        }
    }

    fun withTransition(enterAnim: Int, exitAnim: Int): RouterCard {
        this.enterAnim = enterAnim
        this.exitAnim = exitAnim
        return this
    }

    fun withTransitionStyle(transitionStyle: Int): RouterCard {
        transition = transitionStyle
        return this
    }

    override fun toString(): String {
        return "RouterCard{" +
                "uri=" + uri +
                ", tag=" + tag +
                ", mBundle=" + extras +
                ", pactUrl='" + pactUrl + '\'' +
                ", path='" + path + '\'' +
                ", flags=" + flags +
                ", timeout=" + timeout +
                ", enterAnim=" + enterAnim +
                ", exitAnim=" + exitAnim +
                ", transition=" + transition +
                ", isInterceptLogin=" + isInterceptLogin +
                ", navigation=" + navigation +
                '}'
    }

    fun setInterceptLogin(): RouterCard {
        isInterceptLogin = true
        return this
    }

    fun isInterceptLogin(): Boolean {
        return isInterceptLogin
    }

    fun getFlags(): Int {
        return flags
    }

    fun getExtras(): Bundle {
        return extras
    }

    fun getTimeout(): Int {
        return timeout
    }

    fun getTransition(): Int {
        return transition
    }

    override fun bindRouterCard(routerCard: RouterCard): INavigation? {
        return null
    }

    override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
        navigation?.get()?.apply {
            navigation(context, requestCode, toActivity)
        }
    }

    constructor(source: Parcel) : this() {
        uri = source.readParcelable(Uri::class.java.classLoader)
        extras = source.readBundle()?: Bundle()
        path = source.readString()
        flags = source.readInt()
        timeout = source.readInt()
        enterAnim = source.readInt()
        exitAnim = source.readInt()
        transition = source.readInt()
        isInterceptLogin = source.readByte().toInt() != 0
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(uri, flags)
        dest.writeBundle(extras)
        dest.writeString(path)
        dest.writeInt(flags)
        dest.writeInt(timeout)
        dest.writeInt(enterAnim)
        dest.writeInt(exitAnim)
        dest.writeInt(transition)
        dest.writeByte((if (isInterceptLogin) 1 else 0).toByte())
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RouterCard> = object : Parcelable.Creator<RouterCard> {
            override fun createFromParcel(source: Parcel): RouterCard = RouterCard(source)
            override fun newArray(size: Int): Array<RouterCard?> = arrayOfNulls(size)
        }

        const val TAG = "RouterCard"
    }
}