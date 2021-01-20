package cn.yue.base.kotlin.test.utils

import cn.yue.base.common.utils.code.MMKVUtils
import cn.yue.base.kotlin.test.mode.UserBean
import cn.yue.base.middle.init.UrlEnvironment
import com.google.gson.Gson

/**
 * Description :
 * Created by yue on 2020/10/20
 */
object LocalStorage {

    private const val SERVICE_ENVIRONMENT = "service_environment"
    private const val USER = "user"
    private const val TOKEN = "token"

    fun getServiceEnvironment(): UrlEnvironment {
        val environmentName = MMKVUtils.getString(SERVICE_ENVIRONMENT)
        UrlEnvironment.values().forEach {
            if (it.name == environmentName) {
                return it
            }
        }
        return UrlEnvironment.RELEASE
    }

    fun setServiceEnvironment(serviceEnvironment: UrlEnvironment) {
        MMKVUtils.put(SERVICE_ENVIRONMENT, serviceEnvironment.name)
    }

    fun getUserInfo(): UserBean? {
        val userBean = MMKVUtils.getString(USER)
        if (userBean.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(userBean, UserBean::class.java)
    }

    fun setUserInfo(userBean: UserBean) {
        MMKVUtils.put(USER, Gson().toJson(userBean))
    }

    fun clearUserInfo() {
        MMKVUtils.remove(USER)
    }

    fun getToken(): String? {
        return MMKVUtils.getString(TOKEN)
    }

    fun setToken(token: String) {
        MMKVUtils.put(TOKEN, token)
    }

    fun clearToken() {
        MMKVUtils.remove(TOKEN)
    }
}