package cn.yue.base.wx

interface LoginCallback {
    fun success(entity: WXInfoEntity)
    fun failure(message: String)
}