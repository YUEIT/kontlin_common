package cn.yue.base.wx

interface ShareCallback {
    fun success()
    fun failure(message: String)
}