package cn.yue.base.common.utils.file

import android.annotation.TargetApi
import android.os.Build
import android.os.Environment
import android.os.StatFs
import cn.yue.base.common.utils.constant.ConvertUtils
import cn.yue.base.common.utils.debug.CloseUtils
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * 介绍：SD卡相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class SDCardUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    class SDCardInfo {
        internal var isExist: Boolean = false
        internal var totalBlocks: Long = 0
        internal var freeBlocks: Long = 0
        internal var availableBlocks: Long = 0
        internal var blockByteSize: Long = 0
        internal var totalBytes: Long = 0
        internal var freeBytes: Long = 0
        internal var availableBytes: Long = 0

        override fun toString(): String {
            return "isExist=" + isExist +
                    "\ntotalBlocks=" + totalBlocks +
                    "\nfreeBlocks=" + freeBlocks +
                    "\navailableBlocks=" + availableBlocks +
                    "\nblockByteSize=" + blockByteSize +
                    "\ntotalBytes=" + totalBytes +
                    "\nfreeBytes=" + freeBytes +
                    "\navailableBytes=" + availableBytes
        }
    }

    companion object {

        /**
         * 判断SD卡是否可用
         *
         * @return true : 可用<br></br>false : 不可用
         */
        @JvmStatic
        val isSDCardEnable: Boolean
            get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

        /**
         * 获取SD卡路径
         *
         * 先用shell，shell失败再普通方法获取，一般是/storage/emulated/0/
         *
         * @return SD卡路径
         */
        @JvmStatic
        val sdCardPath: String?
            get() {
                if (!isSDCardEnable) return null
                val cmd = "cat /proc/mounts"
                val run = Runtime.getRuntime()
                var bufferedReader: BufferedReader? = null
                try {
                    val p = run.exec(cmd)
                    bufferedReader = BufferedReader(InputStreamReader(BufferedInputStream(p.inputStream)))
                    var lineStr: String? = null
                    while ({lineStr = bufferedReader.readLine(); lineStr}() != null) {
                        if (lineStr!!.contains("sdcard") && lineStr!!.contains(".android_secure")) {
                            val strArray = lineStr!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            if (strArray.size >= 5) {
                                return strArray[1].replace("/.android_secure", "") + File.separator
                            }
                        }
                        if (p.waitFor() != 0 && p.exitValue() == 1) {
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    CloseUtils.closeIO(bufferedReader!!)
                }
                return Environment.getExternalStorageDirectory().path + File.separator
            }

        /**
         * 获取SD卡data路径
         *
         * @return SD卡data路径
         */
        @JvmStatic
        val dataPath: String?
            get() = if (!isSDCardEnable) null else Environment.getExternalStorageDirectory().path + File.separator + "data" + File.separator

        /**
         * 获取SD卡剩余空间
         *
         * @return SD卡剩余空间
         */
        @JvmStatic
        val freeSpace: String?
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            get() {
                if (!isSDCardEnable) return null
                val stat = StatFs(sdCardPath)
                val blockSize: Long
                val availableBlocks: Long
                availableBlocks = stat.availableBlocksLong
                blockSize = stat.blockSizeLong
                return ConvertUtils.byte2FitMemorySize(availableBlocks * blockSize)
            }

        /**
         * 获取SD卡信息
         *
         * @return SDCardInfo
         */
        @JvmStatic
        val sdCardInfo: String?
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
            get() {
                if (!isSDCardEnable) return null
                val sd = SDCardInfo()
                sd.isExist = true
                val sf = StatFs(Environment.getExternalStorageDirectory().path)
                sd.totalBlocks = sf.blockCountLong
                sd.blockByteSize = sf.blockSizeLong
                sd.availableBlocks = sf.availableBlocksLong
                sd.availableBytes = sf.availableBytes
                sd.freeBlocks = sf.freeBlocksLong
                sd.freeBytes = sf.freeBytes
                sd.totalBytes = sf.totalBytes
                return sd.toString()
            }
    }
}