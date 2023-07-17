package cn.yue.base.net.wrapper

data class BaseBean<T>(var message: String?,
                       var code: String?,
                       var data: T?) {

    override fun toString(): String {
        return "BaseBean{" +
                "message='" + message + '\''.toString() +
                ", code='" + code + '\''.toString() +
                ", data=" + data +
                '}'.toString()
    }
}
