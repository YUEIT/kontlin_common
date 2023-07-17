package cn.yue.base.module

/**
 * Description : 组件类型 , 初始化时根据值的大小排序进行初始化
 * Created by yue on 2020/4/4
 */
annotation class ModuleType {
    companion object {
        var MODULE_BASE = 0
        var MODULE_APP = 1
    }
}