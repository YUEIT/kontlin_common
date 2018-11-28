package cn.yue.base.middle.init

/**
 * Description :
 * Created by yue on 2018/11/12
 */
class InitConstant private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        var islogin: Boolean = false

        var loginToken: String? = null

        var versionName: String? = null

        var deviceId: String? = null

        val APP_CLIENT_TYPE = "2"

        val APP_SIGN_KEY = "nK!op4w9lB.alev0"
    }
}
