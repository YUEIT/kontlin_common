package cn.yue.base.common.activity

/**
 * Description :
 * Created by yue on 2018/11/12
 */
interface PermissionCallBack {
    fun requestSuccess(permission: String)
    fun requestFailed(permission: String)
}
