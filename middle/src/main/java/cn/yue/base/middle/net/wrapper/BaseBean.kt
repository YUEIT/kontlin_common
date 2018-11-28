package cn.yue.base.middle.net.wrapper

import com.google.gson.annotations.SerializedName

/**
 * 包装类：用于接受接口返回的包装 data结构的bean
 * Created by zhangxutong .
 * Date: 16/04/10
 */
data class BaseBean<T>(var message: String, @SerializedName("flag")var code: String, var data: T) {

    override fun toString(): String {
        return "BaseBean{" +
                "message='" + message + '\''.toString() +
                ", code='" + code + '\''.toString() +
                ", data=" + data +
                '}'.toString()
    }
}
