package cn.yue.base.utils.debug

import android.util.Log

object LogUtils {

    private var logSwitch = true
    private var logFilter = 'v'
    private var tag = "LOG"
    private var stackIndex = 0

    public fun setDebug(debug: Boolean) {
        logSwitch = debug
    }

    /**
     * Verbose日志
     *
     * @param msg 消息
     */
    
    fun v(msg: Any) {
        log(tag, msg.toString(), null, 'i')
    }

    /**
     * Verbose日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    
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
    
    fun v(tag: String, msg: Any, tr: Throwable) {
        log(tag, msg.toString(), tr, 'v')
    }

    /**
     * Debug日志
     *
     * @param msg 消息
     */
    
    fun d(msg: Any) {
        log(tag, msg.toString(), null, 'd')
    }

    /**
     * Debug日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    
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
    
    fun d(tag: String, msg: Any, tr: Throwable) {
        log(tag, msg.toString(), tr, 'd')
    }

    /**
     * Info日志
     *
     * @param msg 消息
     */
    
    fun i(msg: Any) {
        log(tag, msg.toString(), null, 'i')
    }

    /**
     * Info日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    
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
    
    fun i(tag: String, msg: Any, tr: Throwable) {
        log(tag, msg.toString(), tr, 'i')
    }

    /**
     * Warn日志
     *
     * @param msg 消息
     */
    
    fun w(msg: Any) {
        log(tag, msg.toString(), null, 'w')
    }

    /**
     * Warn日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    
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
    
    fun w(tag: String, msg: Any, tr: Throwable) {
        log(tag, msg.toString(), tr, 'w')
    }

    /**
     * Error日志
     *
     * @param msg 消息
     */
    
    fun e(msg: Any) {
        log(tag, msg.toString(), null, 'e')
    }

    /**
     * Error日志
     *
     * @param tag 标签
     * @param msg 消息
     */
    
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
    
    private fun log(tag: String, msg: String?, tr: Throwable?, type: Char) {
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
    
    private fun printLog(tag: String, msg: String, tr: Throwable?, type: Char) {
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
     * 产生tag
     *
     * @return tag
     */
    private fun generateTag(tag: String): String {
        val stacks = Thread.currentThread().stackTrace
        if (stackIndex == 0) {
            while (stacks.size > stackIndex && stacks[stackIndex].methodName != "generateTag") {
                ++stackIndex
            }
            stackIndex += 3
            if (stackIndex >= stacks.size) {
                stackIndex = stacks.size - 1
            }
        }
        val caller = stacks[stackIndex]
        var callerClazzName = caller.className
        val format = "Tag[$tag] %s[%s, %d]"
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        return String.format(format, callerClazzName, caller.methodName, caller.lineNumber)
    }
    
}