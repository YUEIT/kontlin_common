package cn.yue.base.middle.wx

interface ShareCallback {
    fun success()
    fun failure(message: String)
}