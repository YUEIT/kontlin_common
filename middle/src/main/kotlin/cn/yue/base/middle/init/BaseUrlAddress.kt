package cn.yue.base.middle.init

/**
 * Description :
 * Created by yue on 2019/3/13
 */
object BaseUrlAddress {
    /** */
    var upLoadUrl: String = ""

    private fun debugModel() {
        upLoadUrl = "http://imgsvc.imcoming.com"
    }

    private fun releaseModel() {
        upLoadUrl = "https://imgsvc.anlaiye.com.cn"
    }

    /** */
    @JvmStatic
    fun setDebug(debug: Boolean) {
        if (debug) {
            debugModel()
        } else {
            releaseModel()
        }
    }

    //开发环境，着急调试的话加上，一般不用
    private fun developModel() {
        upLoadUrl = "https://imgsvc.anlaiye.com.cn"
    }
}