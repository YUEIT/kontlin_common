package cn.yue.base.net

import java.io.IOException

class ResultException(var code: String = "-1", override var message: String = "")
    : IOException(message) {

    override fun toString(): String {
        return "ResultException(code='$code', message='$message')"
    }
}