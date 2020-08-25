package cn.yue.base.common.utils.device

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import cn.yue.base.common.utils.Utils

object NetworkUtils {

    /**
     * 获取活动网络信息
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @return NetworkInfo
     */
    private val activeNetworkInfo: NetworkInfo?
        get() = (Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

    /**
     * 判断网络是否连接
     *
     * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>`
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isConnected(): Boolean {
        val info = activeNetworkInfo
        return info != null && info.isConnected
    }
}