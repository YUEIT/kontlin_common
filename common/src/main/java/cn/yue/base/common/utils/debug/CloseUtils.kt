package cn.yue.base.common.utils.debug

import java.io.Closeable
import java.io.IOException

/**
 * 介绍：关闭相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object CloseUtils {

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    @JvmStatic
    fun closeIO(vararg closeables: Closeable) {
        if (closeables == null) return
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 安静关闭IO
     *
     * @param closeables closeable
     */
    @JvmStatic
    fun closeIOQuietly(vararg closeables: Closeable) {
        if (closeables == null) return
        for (closeable in closeables) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (ignored: IOException) {
                }

            }
        }
    }
}

