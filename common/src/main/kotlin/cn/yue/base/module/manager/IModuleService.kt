package cn.yue.base.module.manager

import android.content.Context

/**
 * Description : 组件服务需继承，可夸模块通信接口
 * Created by yue on 2020/4/4
 */
interface IModuleService {
    fun init(context: Context)
}