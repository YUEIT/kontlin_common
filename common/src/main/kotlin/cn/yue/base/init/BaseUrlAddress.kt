package cn.yue.base.init

/**
 * Description :
 * Created by yue on 2019/3/13
 */
object BaseUrlAddress {

    var baseUrl: String = UrlEnvironment.RELEASE.url

    private fun debugModel() {
        baseUrl = InitConstant.getServiceEnvironment().url
    }

    private fun releaseModel() {
        baseUrl = UrlEnvironment.RELEASE.url
    }

    @JvmStatic
    fun setDebug(debug: Boolean) {
        if (debug) {
            debugModel()
        } else {
            releaseModel()
        }
    }
}

enum class UrlEnvironment(val url: String) {
    TEST("http://101.133.164.46:8080"),
    RELEASE("http://101.133.164.46:8080")
}