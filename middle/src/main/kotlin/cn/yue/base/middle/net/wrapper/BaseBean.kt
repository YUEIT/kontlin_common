package cn.yue.base.middle.net.wrapper

import com.google.gson.annotations.SerializedName


data class BaseBean<T>(var message: String,
                       @SerializedName("flag")var code: String,
                       var data: T) {

    override fun toString(): String {
        return "BaseBean{" +
                "message='" + message + '\''.toString() +
                ", code='" + code + '\''.toString() +
                ", data=" + data +
                '}'.toString()
    }
}
