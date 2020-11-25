package cn.yue.base.common.utils.device

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import cn.yue.base.common.utils.Utils


object NetworkUtils {

    fun isAvailable(): Boolean {
        val manager = Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        val info = manager.activeNetworkInfo
        return null != info && info.isConnected && info.isAvailable
    }

    fun isWifi(): Boolean {
        val connectivityManager = Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val activeNetInfo = connectivityManager.activeNetworkInfo
            activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI
        }

    }

    fun isMobile(): Boolean {
        val connectivityManager = Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val activeNetInfo = connectivityManager.activeNetworkInfo
            activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_MOBILE
        }
    }
}