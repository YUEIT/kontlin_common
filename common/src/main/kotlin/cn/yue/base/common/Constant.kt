package cn.yue.base.common

import android.os.Build
import android.os.Environment
import cn.yue.base.common.utils.Utils.getContext
import java.io.File

/**
 * Description :
 * Created by yue on 2018/11/12
 */
object Constant {
    private const val COMMON_NAME = "YUE"

    fun getExternalStorage(): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Environment.getExternalStorageDirectory().toString() + File.separator + COMMON_NAME
        } else {
            val filesDir = getContext().getExternalFilesDir("")
                    ?: return (getContext().filesDir.toString() + File.separator + COMMON_NAME)
            filesDir.path + File.separator + COMMON_NAME
        }
    }

    val imagePath = getExternalStorage() + File.separator + "image" + File.separator

    val audioPath = getExternalStorage() + File.separator + "audio" + File.separator

    val cachePath = getExternalStorage() + File.separator + "cache" + File.separator
    
    const val FILE_PROVIDER_AUTHORITY = "cn.yue.base.kotlin.test.fileprovider"
}

