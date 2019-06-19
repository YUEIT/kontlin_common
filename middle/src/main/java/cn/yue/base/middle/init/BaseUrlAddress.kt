package cn.yue.base.middle.init

/**
 * Description :
 * Created by yue on 2019/6/18
 */
object BaseUrlAddress {
    private var UP_LOAD_URL: String = ""

    private fun debugModel() {
        UP_LOAD_URL = "http://imgsvc.imcoming.com"
    }

    private fun releaseModel() {
        UP_LOAD_URL = "https://imgsvc.anlaiye.com.cn"
    }

    /** */

    fun getUpLoadUrl(): String {
        return UP_LOAD_URL
    }

    /** */

    fun setDebug(debug: Boolean) {
        if (debug) {
            debugModel()
        } else {
            releaseModel()
        }
    }

    //开发环境，着急调试的话加上，一般不用
    private fun developModel() {
        UP_LOAD_URL = "https://imgsvc.anlaiye.com.cn"
    }

}