package cn.yue.base.module.manager

import kotlin.reflect.KClass

class ModuleData<T> {
    var priority = 0
    var interfaceClass: KClass<*>? = null
    var impClass: KClass<*>? = null
    var service: T? = null
}