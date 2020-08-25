package cn.yue.base.common.utils.device

import android.provider.Settings
import android.text.TextUtils
import cn.yue.base.common.utils.Utils.getContext
import cn.yue.base.common.utils.code.SPUtils
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
    var KEY_CST_DEVICE_ID = "key_cst_device_id"

    fun getNullDeviceId(deviceId: String?): String? {
        if (TextUtils.isEmpty(deviceId) || deviceId!!.length < 16) {//说明获取不到了，或者小于16
            //先从本地持久化取
            var cstDeviceId = SPUtils.getInstance().getString(KEY_CST_DEVICE_ID, "")
            if (TextUtils.isEmpty(cstDeviceId)) {//娶不到自己构造一个随机字符串，并保存
                cstDeviceId = UUID.randomUUID().toString()
                SPUtils.getInstance().put(KEY_CST_DEVICE_ID, cstDeviceId)
            }
            return cstDeviceId
        }
        return deviceId
    }

}