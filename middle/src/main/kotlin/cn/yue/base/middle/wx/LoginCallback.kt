package cn.yue.base.middle.wx

interface LoginCallback {
    fun success(entity: WXInfoEntity)
    fun failure(message: String)
}