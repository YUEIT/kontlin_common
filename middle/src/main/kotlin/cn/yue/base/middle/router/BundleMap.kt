package cn.yue.base.middle.router

import java.io.Serializable

/**
 * Description :
 * Created by yue on 2020/4/22
 */
class BundleMap(var map: Map<*, *>) : Serializable {

    override fun toString(): String {
        return "BundleMap{" +
                "map=" + map +
                '}'
    }

    companion object {
        private const val serialVersionUID = 4502025925715013070L
    }

}