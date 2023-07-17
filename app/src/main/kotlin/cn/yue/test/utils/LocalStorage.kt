package cn.yue.test.utils

import cn.yue.base.utils.code.MMKVUtils
import cn.yue.base.init.UrlEnvironment

/**
 * Description :
 * Created by yue on 2020/10/20
 */
object LocalStorage {

    private const val USER_PERMISSION = "user_permission"
    private const val SERVICE_ENVIRONMENT = "service_environment"

    fun getUserPermission(): Boolean {
        return MMKVUtils.getBoolean(USER_PERMISSION)
    }

    fun setUserPermission(boolean: Boolean) {
        MMKVUtils.put(USER_PERMISSION, boolean)
    }

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

}