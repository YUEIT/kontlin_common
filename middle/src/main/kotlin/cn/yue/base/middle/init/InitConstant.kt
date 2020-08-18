package cn.yue.base.middle.init

import cn.yue.base.common.utils.device.DeviceUtils.getNullDeviceId
import cn.yue.base.common.utils.device.PhoneUtils

/**
 * Description : 初始化
 * Created by yue on 2018/11/12
 */
object InitConstant {
    /**------------------------------------------------------------------------------------------ */ //version.properties文件中修改对应值（正式编译版本无需修改，自动设置false）
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
        return getNullDeviceId(mDeviceId).also { mDeviceId = it }
    }

    const val APP_CLIENT_TYPE = "2"
    const val APP_SIGN_KEY = "nK!op4w9lB.alev0"

}