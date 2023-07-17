package cn.yue.base.utils.device

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import cn.yue.base.utils.Utils


object NetworkUtils {

    private var networkAvailable = false

    fun register() {
        val manager = Utils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    networkAvailable = true
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    networkAvailable = false
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    networkAvailable = false
                }
            })
        }
    }

    fun isAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return networkAvailable
        }
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