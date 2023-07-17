package cn.yue.base.utils.file

import android.content.Context
import android.os.Environment
import java.io.File
import java.math.BigDecimal

object CleanUtils {

    fun getTotalCacheSize(context: Context): String? {
        var cacheSize: Long = getFolderSize(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheSize += getFolderSize(context.externalCacheDir)
        }
        return getFormatSize(cacheSize)
    }

    private fun getFolderSize(file: File?): Long {
        var size: Long = 0
        if (file != null) {
            val fileList: Array<File>? = file.listFiles()
            if (fileList != null && fileList.isNotEmpty()) {
                for (i in fileList.indices) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory) {
                        size += getFolderSize(fileList[i])
                    } else {
                        size += fileList[i].length()
                    }
                }
            }
        }
        return size
    }

    /**
     * 格式化单位
     */
    private fun getFormatSize(size: Long): String? {
        val kiloByte = size / 1024
        val megaByte = kiloByte / 1024
        if (kiloByte < 1) {
            val result = BigDecimal(size)
            return result.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString().toString() + "B"
        }
        if (megaByte < 1) {
            val result2 = BigDecimal(kiloByte)
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString().toString() + "KB"
        }

        return "$size B"
    }

    fun clearAllCache(context: Context) {
        deleteDir(context.cacheDir)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteDir(context.externalCacheDir)
        }
    }

    /**
     * 删除某个文件
     */
    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }
            return dir.delete()
        }
        return dir?.delete() ?: false
    }
}