package cn.yue.base.init

import cn.yue.base.utils.device.PhoneUtils

/**
 * Description : 初始化
 * Created by yue on 2018/11/12
 */
object InitConstant {

    /**------------------------------------------------------------------------------------------ */
    fun isDebug(): Boolean {
        return mDebug
    }

    fun setDebug(debug: Boolean) {
        mDebug = debug
    }

    private var mDebug = false

    private var mEnvironment: UrlEnvironment = UrlEnvironment.RELEASE

    fun setServiceEnvironment(environment: UrlEnvironment) {
        mEnvironment = environment
    }

    fun getServiceEnvironment(): UrlEnvironment {
        return mEnvironment
    }

    private var mVersionName: String? = null

    fun getVersionName(): String? {
        return mVersionName
    }

    fun setVersionName(mVersionName: String?) {
        InitConstant.mVersionName = mVersionName
    }

    private var mDeviceId: String? = null

    fun getDeviceId(): String? {
        if (mDeviceId == null) {
            mDeviceId = PhoneUtils.getAndroidId()
        }
        return PhoneUtils.getNullDeviceId(mDeviceId).also { mDeviceId = it }
    }

    /**------------------------------------------------------------------------------------------ */

    private var mLogged = false

    fun setLogged(isLogin: Boolean) {
        mLogged = isLogin
    }

    fun isLogged(): Boolean {
        return mLogged
    }

    private var mToken = ""

    fun setToken(token: String) {
        mToken = token
    }

    fun getToken(): String {
        return mToken
    }
}