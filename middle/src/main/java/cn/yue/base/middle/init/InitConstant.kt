package cn.yue.base.middle.init

import cn.yue.base.common.utils.device.DeviceUtils
import cn.yue.base.common.utils.device.PhoneUtils

/**
 * Description :
 * Created by yue on 2018/11/12
 */
class InitConstant private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**------------------------------------------------------------------------------------------ */
        //version.properties文件中修改对应值（正式编译版本无需修改，自动设置false）

        var isDebug: Boolean = false

        var logMode: Boolean = false

        /**------------------------------------------------------------------------------------------ */

        var isLogin: Boolean = false

        var loginToken: String? = null

        var loginTokenSecret: String? = null

        var loginTokenForWallet: String? = null // 钱包的tk

        /**------------------------------------------------------------------------------------------ */

        var latitude = 0.0     //纬度

        var longitude = 0.0    //经度

        var versionName: String? = null

        private var mDeviceId: String? = null

        fun getDeviceId(): String? {
            if (InitConstant.mDeviceId == null) {
                mDeviceId = PhoneUtils.imei
            }
            mDeviceId = DeviceUtils.getNullDeviceId(mDeviceId)
            return mDeviceId
        }

        val APP_CLIENT_TYPE = "2"

        val APP_SIGN_KEY = "nK!op4w9lB.alev0"

        /**------------------------------------------------------------------------------------------ */

        val WX_APP_ID = "wx00915cf45667d83a"
        val WX_APP_SECRET = "97b811646e4ed103675c27448d1f081c"
        val WX_USER_NAME = "gh_b57767e56326"

    }
}
