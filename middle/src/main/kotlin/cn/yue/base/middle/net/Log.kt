package cn.yue.base.middle.net

import cn.yue.base.common.utils.debug.LogUtils

/**
 * Description :
 * Created by yue on 2020/8/19
 */

fun String.netLog() {
    LogUtils.i("okhttp", this)
}