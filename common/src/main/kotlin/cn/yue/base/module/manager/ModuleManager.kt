package cn.yue.base.module.manager

import android.content.Context
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Description : 各Module通信方案
 * Created by yue on 2020/4/4
 */
class ModuleManager<T : IModuleService> {

    // 类型  接口  实现
    private val serviceMap: MutableMap<KClass<*>, ModuleData<T>> = HashMap()

    private fun registerModule(priority: Int, clazz: KClass<*>, service: T) {
        try {
            val moduleData = ModuleData<T>()
            moduleData.priority = priority
            moduleData.interfaceClass = clazz
            moduleData.service = service
            moduleData.impClass = (service as IModuleService).javaClass.kotlin
            serviceMap[clazz] = moduleData
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getModule(key: KClass<*>): T? {
        val moduleData = serviceMap[key] ?: return null
        if (moduleData.service == null) {
            try {
                val clazz: KClass<*>? = moduleData.impClass
                if (clazz != null) {
                    val service = clazz.createInstance() as T?
                    moduleData.service = service
                    return service
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return moduleData.service
    }

    private fun init(context: Context) {
        val list = ArrayList<Map.Entry<KClass<*>, ModuleData<T>>>(serviceMap.entries)
        list.sortWith(Comparator { o1, o2 ->
            val o1Index = o1.value.priority
            val o2Index = o2.value.priority
            when {
                o1Index < o2Index -> {
                    1
                }
                o1Index > o2Index -> {
                    -1
                }
                else -> {
                    0
                }
            }
        })
        for ((_, moduleData) in list) {
            moduleData.service?.init(context)
        }
    }

    companion object {
        private val instance: ModuleManager<IModuleService> = ModuleManager()

        /**
         * 获取注册的module 接口实现 ，使用该方法必须先通过register注册
         * @param clazz
         * @return
         */
        @JvmStatic
        fun <T : IModuleService> getModuleService(clazz: KClass<T>): T {
            val service = instance.getModule(clazz)
            return if (service != null) {
                service as T
            } else {
                throw IllegalArgumentException("this ModuleService not register")
            }
        }

        /**
         * 注册
         * @param priority
         * @param service
         */
        fun <T : IModuleService> register(priority: Int, clazz: KClass<*>, service: T) {
            instance.registerModule(priority, clazz, service)
        }

        /**
         * 初始化
         * @param context
         */
        fun doInit(context: Context) {
            instance.init(context)
        }
    }
}