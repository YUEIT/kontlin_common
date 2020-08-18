package cn.yue.base.common.utils.debug

import android.os.Environment
import android.util.Log
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.file.FileUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 介绍：日志相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class LogUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    class Builder {

        private var logSwitch = true
        private var log2FileSwitch = false
        private var logFilter = 'v'
        private var tag = "TAG"

        fun setLogSwitch(logSwitch: Boolean): Builder {
            this.logSwitch = logSwitch
            return this
        }

        fun setLog2FileSwitch(log2FileSwitch: Boolean): Builder {
            this.log2FileSwitch = log2FileSwitch
            return this
        }

        fun setLogFilter(logFilter: Char): Builder {
            this.logFilter = logFilter
            return this
        }

        fun setTag(tag: String): Builder {
            this.tag = tag
            return this
        }

        fun create() {
            LogUtils.logSwitch = logSwitch
            LogUtils.log2FileSwitch = log2FileSwitch
            LogUtils.logFilter = logFilter
            LogUtils.tag = tag
        }
    }

    companion object {

        private var logSwitch = true
        private var log2FileSwitch = false
        private var logFilter = 'v'
        private var tag = "YUE"
        private var dir: String? = null
        private var stackIndex = 0

        /**
         * 初始化函数
         *
         * 与[.getBuilder]两者选其一
         *
         * @param logSwitch      日志总开关
         * @param log2FileSwitch 日志写入文件开关，设为true需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
         * @param logFilter      输入日志类型有`v, d, i, w, e`<br></br>v代表输出所有信息，w则只输出警告...
         * @param tag            标签
         */
        @JvmStatic
        fun init(logSwitch: Boolean, log2FileSwitch: Boolean, logFilter: Char, tag: String) {
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                dir = Utils.getContext().externalCacheDir!!.path + File.separator
            } else {
                dir = Utils.getContext().cacheDir.path + File.separator
            }
            LogUtils.logSwitch = logSwitch
            LogUtils.log2FileSwitch = log2FileSwitch
            LogUtils.logFilter = logFilter
            LogUtils.tag = tag
        }

        /**
         * 获取LogUtils建造者
         *
         * 与[.init]两者选其一
         *
         * @return Builder对象
         */
        val builder: Builder
            get() {
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                    dir = Utils.getContext().externalCacheDir!!.path + File.separator + "log" + File.separator
                } else {
                    dir = Utils.getContext().cacheDir.path + File.separator + "log" + File.separator
                }
                return Builder()
            }

        /**
         * Verbose日志
         *
         * @param msg 消息
         */
        @JvmStatic
        fun v(msg: Any) {
            log(tag, msg.toString(), null, 'i')
        }

        /**
         * Verbose日志
         *
         * @param tag 标签
         * @param msg 消息
         */
        @JvmStatic
        fun v(tag: String, msg: Any) {
            log(tag, msg.toString(), null, 'i')
        }

        /**
         * Verbose日志
         *
         * @param tag 标签
         * @param msg 消息
         * @param tr  异常
         */
        @JvmStatic
        fun v(tag: String, msg: Any, tr: Throwable) {
            log(tag, msg.toString(), tr, 'v')
        }

        /**
         * Debug日志
         *
         * @param msg 消息
         */
        @JvmStatic
        fun d(msg: Any) {
            log(tag, msg.toString(), null, 'd')
        }

        /**
         * Debug日志
         *
         * @param tag 标签
         * @param msg 消息
         */
        @JvmStatic
        fun d(tag: String, msg: Any) {
            log(tag, msg.toString(), null, 'd')
        }

        /**
         * Debug日志
         *
         * @param tag 标签
         * @param msg 消息
         * @param tr  异常
         */
        @JvmStatic
        fun d(tag: String, msg: Any, tr: Throwable) {
            log(tag, msg.toString(), tr, 'd')
        }

        /**
         * Info日志
         *
         * @param msg 消息
         */
        @JvmStatic
        fun i(msg: Any) {
            log(tag, msg.toString(), null, 'i')
        }

        /**
         * Info日志
         *
         * @param tag 标签
         * @param msg 消息
         */
        @JvmStatic
        fun i(tag: String, msg: Any) {
            log(tag, msg.toString(), null, 'i')
        }

        /**
         * Info日志
         *
         * @param tag 标签
         * @param msg 消息
         * @param tr  异常
         */
        @JvmStatic
        fun i(tag: String, msg: Any, tr: Throwable) {
            log(tag, msg.toString(), tr, 'i')
        }

        /**
         * Warn日志
         *
         * @param msg 消息
         */
        @JvmStatic
        fun w(msg: Any) {
            log(tag, msg.toString(), null, 'w')
        }

        /**
         * Warn日志
         *
         * @param tag 标签
         * @param msg 消息
         */
        @JvmStatic
        fun w(tag: String, msg: Any) {
            log(tag, msg.toString(), null, 'w')
        }

        /**
         * Warn日志
         *
         * @param tag 标签
         * @param msg 消息
         * @param tr  异常
         */
        @JvmStatic
        fun w(tag: String, msg: Any, tr: Throwable) {
            log(tag, msg.toString(), tr, 'w')
        }

        /**
         * Error日志
         *
         * @param msg 消息
         */
        @JvmStatic
        fun e(msg: Any) {
            log(tag, msg.toString(), null, 'e')
        }

        /**
         * Error日志
         *
         * @param tag 标签
         * @param msg 消息
         */
        @JvmStatic
        fun e(tag: String, msg: Any) {
            log(tag, msg.toString(), null, 'e')
        }

        /**
         * Error日志
         *
         * @param tag 标签
         * @param msg 消息
         * @param tr  异常
         */
        @JvmStatic
        fun e(tag: String, msg: Any, tr: Throwable) {
            log(tag, msg.toString(), tr, 'e')
        }

        /**
         * 根据tag, msg和等级，输出日志
         *
         * @param tag  标签
         * @param msg  消息
         * @param tr   异常
         * @param type 日志类型
         */
        private @JvmStatic
        fun log(tag: String, msg: String?, tr: Throwable?, type: Char) {
            if (msg == null || msg.isEmpty()) return
            if (logSwitch) {
                if ('e' == type && ('e' == logFilter || 'v' == logFilter)) {
                    printLog(generateTag(tag), msg, tr, 'e')
                } else if ('w' == type && ('w' == logFilter || 'v' == logFilter)) {
                    printLog(generateTag(tag), msg, tr, 'w')
                } else if ('d' == type && ('d' == logFilter || 'v' == logFilter)) {
                    printLog(generateTag(tag), msg, tr, 'd')
                } else if ('i' == type && ('d' == logFilter || 'v' == logFilter)) {
                    printLog(generateTag(tag), msg, tr, 'i')
                }
                if (log2FileSwitch) {
                    log2File(type, generateTag(tag), msg + '\n'.toString() + Log.getStackTraceString(tr))
                }
            }
        }

        /**
         * 根据tag, msg和等级，输出日志
         *
         * @param tag  标签
         * @param msg  消息
         * @param tr   异常
         * @param type 日志类型
         */
        private @JvmStatic
        fun printLog(tag: String, msg: String, tr: Throwable?, type: Char) {
            val maxLen = 4000
            var i = 0
            val len = msg.length
            while (i * maxLen < len) {
                val subMsg = msg.substring(i * maxLen, if ((i + 1) * maxLen < len) (i + 1) * maxLen else len)
                when (type) {
                    'e' -> Log.e(tag, subMsg, tr)
                    'w' -> Log.w(tag, subMsg, tr)
                    'd' -> Log.d(tag, subMsg, tr)
                    'i' -> Log.i(tag, subMsg, tr)
                }
                ++i
            }
        }

        /**
         * 打开日志文件并写入日志
         *
         * @param type 日志类型
         * @param tag  标签
         * @param msg  信息
         */
        @Synchronized
        private
        @JvmStatic
        fun log2File(type: Char, tag: String, msg: String) {
            val now = Date()
            val date = SimpleDateFormat("MM-dd", Locale.getDefault()).format(now)
            val fullPath = "$dir$date.txt"
            if (!FileUtils.createOrExistsFile(fullPath)) return
            val time = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(now)
            val dateLogContent = time + ":" + type + ":" + tag + ":" + msg + '\n'.toString()
            Thread(Runnable {
                var bw: BufferedWriter? = null
                try {
                    bw = BufferedWriter(FileWriter(fullPath, true))
                    bw.write(dateLogContent)
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    CloseUtils.closeIO(bw!!)
                }
            }).start()
        }

        /**
         * 产生tag
         *
         * @return tag
         */
        private @JvmStatic
        fun generateTag(tag: String): String {
            val stacks = Thread.currentThread().stackTrace
            if (stackIndex == 0) {
                while (stacks[stackIndex].methodName != "generateTag") {
                    ++stackIndex
                }
                stackIndex += 3
            }
            val caller = stacks[stackIndex]
            var callerClazzName = caller.className
            val format = "Tag[$tag] %s[%s, %d]"
            callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
            return String.format(format, callerClazzName, caller.methodName, caller.lineNumber)
        }
    }
}