package cn.yue.base.common

import android.os.Environment
import java.io.File

/**
 * Description :
 * Created by yue on 2018/11/12
 */
class Constant private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        val COMMON_NAME = "KOTLIN"
        val SDCARD_NAME = Environment.getExternalStorageDirectory().toString() + File.separator + COMMON_NAME
        val IMAGE_PATH = SDCARD_NAME + File.separator + "image" + File.separator
        val AUDIO_PATH = SDCARD_NAME + File.separator + "audio" + File.separator
        val CACHE_PATH = SDCARD_NAME + File.separator + "cache" + File.separator
    }

}
