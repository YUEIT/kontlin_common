package cn.yue.base.common.utils.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.provider.Settings
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.debug.LogUtils
import cn.yue.base.common.utils.debug.ToastUtils
import java.io.IOException
import java.util.*

/**
 * 介绍：定位相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class LocationUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    private class MyLocationListener : LocationListener {
        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        override fun onLocationChanged(location: Location) {
            if (mListener != null) {
                mListener!!.onLocationChanged(location)
            }
        }

        /**
         * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            if (mListener != null) {
                mListener!!.onStatusChanged(provider, status, extras)
            }
            when (status) {
                LocationProvider.AVAILABLE -> LogUtils.d("onStatusChanged", "当前GPS状态为可见状态")
                LocationProvider.OUT_OF_SERVICE -> LogUtils.d("onStatusChanged", "当前GPS状态为服务区外状态")
                LocationProvider.TEMPORARILY_UNAVAILABLE -> LogUtils.d("onStatusChanged", "当前GPS状态为暂停服务状态")
            }
        }

        /**
         * provider被enable时触发此函数，比如GPS被打开
         */
        override fun onProviderEnabled(provider: String) {}

        /**
         * provider被disable时触发此函数，比如GPS被关闭
         */
        override fun onProviderDisabled(provider: String) {}
    }

    interface OnLocationChangeListener {

        /**
         * 获取最后一次保留的坐标
         *
         * @param location 坐标
         */
        fun getLastKnownLocation(location: Location)

        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        fun onLocationChanged(location: Location)

        /**
         * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        fun onStatusChanged(provider: String, status: Int, extras: Bundle) //位置状态发生改变
    }

    companion object {

        private var mListener: OnLocationChangeListener? = null
        private var myLocationListener: MyLocationListener? = null
        private var mLocationManager: LocationManager? = null

        /**
         * 判断Gps是否可用
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        val isGpsEnabled: Boolean
            get() {
                val lm = Utils.getContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }

        /**
         * 判断定位是否可用
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        val isLocationEnabled: Boolean
            get() {
                val lm = Utils.getContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }

        /**
         * 打开Gps设置界面
         */
        @JvmStatic
        fun openGpsSettings() {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Utils.getContext().startActivity(intent)
        }

        /**
         * 注册
         *
         * 使用完记得调用[.unregister]
         *
         * 需添加权限 `<uses-permission android:name="android.permission.INTERNET"/>`
         *
         * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>`
         *
         * 需添加权限 `<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>`
         *
         * 如果`minDistance`为0，则通过`minTime`来定时更新；
         *
         * `minDistance`不为0，则以`minDistance`为准；
         *
         * 两者都为0，则随时刷新。
         *
         * @param minTime     位置信息更新周期（单位：毫秒）
         * @param minDistance 位置变化最小距离：当位置距离变化超过此值时，将更新位置信息（单位：米）
         * @param listener    位置刷新的回调接口
         * @return `true`: 初始化成功<br></br>`false`: 初始化失败
         */
        @SuppressLint("MissingPermission")
        @JvmStatic
        fun register(minTime: Long, minDistance: Long, listener: OnLocationChangeListener?): Boolean {
            if (listener == null) return false
            mLocationManager = Utils.getContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mListener = listener
            if (!isLocationEnabled) {
                ToastUtils.showShortToastSafe("无法定位，请打开定位服务")
                return false
            }
            val provider = mLocationManager!!.getBestProvider(criteria, true)
            val location = mLocationManager!!.getLastKnownLocation(provider)
            if (location != null) listener.getLastKnownLocation(location)
            if (myLocationListener == null) myLocationListener = MyLocationListener()
            mLocationManager!!.requestLocationUpdates(provider, minTime, minDistance.toFloat(), myLocationListener)
            return true
        }


        /**
         * 注销
         */
        @SuppressLint("MissingPermission")
        @JvmStatic
        fun unregister() {
            if (mLocationManager != null) {
                if (myLocationListener != null) {
                    mLocationManager!!.removeUpdates(myLocationListener)
                    myLocationListener = null
                }
                mLocationManager = null
            }
        }

        /**
         * 设置定位参数
         *
         * @return [Criteria]
         */
        private//设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        //设置是否要求速度
        // 设置是否允许运营商收费
        //设置是否需要方位信息
        //设置是否需要海拔信息
        // 设置对电源的需求
        val criteria: Criteria
            get() {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                criteria.isSpeedRequired = false
                criteria.isCostAllowed = false
                criteria.isBearingRequired = false
                criteria.isAltitudeRequired = false
                criteria.powerRequirement = Criteria.POWER_LOW
                return criteria
            }

        /**
         * 根据经纬度获取地理位置
         *
         * @param latitude  纬度
         * @param longitude 经度
         * @return [Address]
         */
        @JvmStatic
        fun getAddress(latitude: Double, longitude: Double): Address? {
            val geocoder = Geocoder(Utils.getContext(), Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses.size > 0) return addresses[0]
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * 根据经纬度获取所在国家
         *
         * @param latitude  纬度
         * @param longitude 经度
         * @return 所在国家
         */
        @JvmStatic
        fun getCountryName(latitude: Double, longitude: Double): String {
            val address = getAddress(latitude, longitude)
            return if (address == null) "unknown" else address.countryName
        }

        /**
         * 根据经纬度获取所在地
         *
         * @param latitude  纬度
         * @param longitude 经度
         * @return 所在地
         */
        @JvmStatic
        fun getLocality(latitude: Double, longitude: Double): String {
            val address = getAddress(latitude, longitude)
            return if (address == null) "unknown" else address.locality
        }

        /**
         * 根据经纬度获取所在街道
         *
         * @param latitude  纬度
         * @param longitude 经度
         * @return 所在街道
         */
        @JvmStatic
        fun getStreet(latitude: Double, longitude: Double): String {
            val address = getAddress(latitude, longitude)
            return if (address == null) "unknown" else address.getAddressLine(0)
        }
    }
}