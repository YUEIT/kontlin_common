package cn.yue.base.utils.device

import android.provider.Settings
import android.text.TextUtils
import cn.yue.base.utils.Utils.getContext
import cn.yue.base.utils.code.SPUtils
import java.util.*

object PhoneUtils {

    fun getAndroidId(): String? {
        return Settings.System.getString(getContext().contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * device_id 必须是 长度： 16~64位的字符串
     * 万一获取不到device_id
     * 自己构造一个随机字符串
     * 本地持久化
     * 不要每次启动变更
     *
     * @return
     */

    fun getNullDeviceId(deviceId: String?): String? {
        val keyDeviceId = "key_device_id"
        if (TextUtils.isEmpty(deviceId) || deviceId!!.length < 16) {//说明获取不到了，或者小于16
            //先从本地持久化取
            var cstDeviceId = SPUtils.getInstance().getString(keyDeviceId, "")
            if (TextUtils.isEmpty(cstDeviceId)) {//取不到自己构造一个随机字符串，并保存
                cstDeviceId = UUID.randomUUID().toString()
                SPUtils.getInstance().put(keyDeviceId, cstDeviceId)
            }
            return cstDeviceId
        }
        return deviceId
    }

}