package cn.yue.base.activity.rx

import java.lang.IllegalStateException

/**
 * Description :
 * Created by yue on 2020/8/12
 */
class OutsideLifecycleException(detailMessage: String) : IllegalStateException(detailMessage) {
}