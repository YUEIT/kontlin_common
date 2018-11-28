package cn.yue.base.common

/**
 * Description :
 * Created by yue on 2018/11/12
 */
class Constant private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        var LOGINED: Boolean = false
    }

}
