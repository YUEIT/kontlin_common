package cn.yue.base.kotlin.test.utils

import cn.yue.base.common.utils.code.MMKVUtils
import cn.yue.base.middle.init.UrlEnvironment

/**
 * Description :
 * Created by yue on 2020/10/20
 */
object LocalStorage {

    private const val SERVICE_ENVIRONMENT = "service_environment"

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