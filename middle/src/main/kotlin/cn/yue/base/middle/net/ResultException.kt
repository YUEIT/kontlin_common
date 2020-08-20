package cn.yue.base.middle.net

class ResultException(var code: String = "-1", override var message: String = "")
    : RuntimeException(message) {

    override fun toString(): String {
        return "ResultException(code='$code', message='$message')"
    }
}