package cn.yue.base.middle.init

import cn.yue.base.common.utils.device.PhoneUtils

/**
 * Description : 初始化
 * Created by yue on 2018/11/12
 */
object InitConstant {
    /**------------------------------------------------------------------------------------------ */
    @JvmField
    var isDebug = false
    var logMode = false

    private var mVersionName: String? = null

    fun getVersionName(): String? {
        return mVersionName
    }

    fun setVersionName(mVersionName: String?) {
        this.mVersionName = mVersionName
    }

    private var mDeviceId: String? = null

    fun getDeviceId(): String? {
        if (mDeviceId == null) {
            mDeviceId = PhoneUtils.getAndroidId()
        }
        return PhoneUtils.getNullDeviceId(mDeviceId).also { mDeviceId = it }
    }

    const val APP_CLIENT_TYPE = "2"
    const val APP_SIGN_KEY = "nK!op4w9lB.alev0"

}