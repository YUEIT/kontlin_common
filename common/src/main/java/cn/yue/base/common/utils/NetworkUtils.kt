package cn.yue.base.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

object NetworkUtils {
    private var mContext: Context? = null

    /*网络状态，下载只需要区分wifi和非wifi即可*/
    val NETWORK_NONE = 1
    val NETWORK_WIFI = 2
    val NETWORK_OTHER = 3

    /**
     * 是否联网
     *
     * @return
     */
    @JvmStatic
    fun isNetwork(): Boolean {
        if (null != mContext) {
            val cm = mContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (null == cm || null != cm && null == cm.activeNetworkInfo) {
                return false
            }
            val info = cm.activeNetworkInfo ?: return false
            return info.isAvailable
        }
        return false
    }


    /**
     * 是否连接WIFI
     *
     * @return
     */
    @JvmStatic
    fun isWifiNetwork(): Boolean {
        if (null != mContext) {
            val connectivityManager = mContext!!
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            if (activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI) {
                return true
            }
        }
        return false
    }

    /**
     * 获取当前是否网络状态－WIFI/非WIFI/无网络
     *
     * @return
     */
    @JvmStatic
    fun getNetworkStatus(): Int {
        if (isNetwork(mContext)) {
            if (isWifiNetwork()) {
                return NETWORK_WIFI
            } else {
                return NETWORK_OTHER
            }
        } else {
            return NETWORK_NONE
        }
    }

    // 判断移动网络
    @JvmStatic
    fun isMobileConnected(): Boolean {
        if (mContext != null) {
            val mConnectivityManager = mContext!!
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable
            }
        }
        return false
    }


    //在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
    @JvmStatic
    fun getMacAddress(): String {
        var macAddress = "default"
        val ip: String? = null
        val wifiMgr = mContext!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiMgr?.connectionInfo
        if (null != info) {
            macAddress = info.macAddress

        }
        return macAddress
    }

    @JvmStatic
    fun setContext(Context: Context) {
        NetworkUtils.mContext = Context
    }

    /**
     * 是否联网
     *
     * @return
     */
    @JvmStatic
    fun isNetwork(mContext: Context?): Boolean {
        if (null != mContext) {
            val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (null == cm || null != cm && null == cm.activeNetworkInfo) {
                return false
            }
            val info = cm.activeNetworkInfo ?: return false
            return info.isAvailable
        }
        return false
    }

}