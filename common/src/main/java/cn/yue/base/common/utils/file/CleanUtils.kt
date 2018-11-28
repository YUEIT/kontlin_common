package cn.yue.base.common.utils.file


import cn.yue.base.common.utils.Utils
import java.io.File

/**
 * 介绍：清除相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object CleanUtils {

    /**
     * 清除内部缓存
     *
     * /data/data/com.xxx.xxx/cache
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanInternalCache(): Boolean {
        return FileUtils.deleteFilesInDir(Utils.getContext().cacheDir)
    }

    /**
     * 清除内部文件
     *
     * /data/data/com.xxx.xxx/files
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanInternalFiles(): Boolean {
        return FileUtils.deleteFilesInDir(Utils.getContext().filesDir)
    }

    /**
     * 清除内部数据库
     *
     * /data/data/com.xxx.xxx/databases
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanInternalDbs(): Boolean {
        return FileUtils.deleteFilesInDir(Utils.getContext().filesDir.parent + File.separator + "databases")
    }

    /**
     * 根据名称清除数据库
     *
     * /data/data/com.xxx.xxx/databases/dbName
     *
     * @param dbName  数据库名称
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanInternalDbByName(dbName: String): Boolean {
        return Utils.getContext().deleteDatabase(dbName)
    }

    /**
     * 清除内部SP
     *
     * /data/data/com.xxx.xxx/shared_prefs
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanInternalSP(): Boolean {
        return FileUtils.deleteFilesInDir(Utils.getContext().filesDir.parent + File.separator + "shared_prefs")
    }

    /**
     * 清除外部缓存
     *
     * /storage/emulated/0/android/data/com.xxx.xxx/cache
     *
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanExternalCache(): Boolean {
        return SDCardUtils.isSDCardEnable && FileUtils.deleteFilesInDir(Utils.getContext().externalCacheDir)
    }

    /**
     * 清除自定义目录下的文件
     *
     * @param dirPath 目录路径
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanCustomCache(dirPath: String): Boolean {
        return FileUtils.deleteFilesInDir(dirPath)
    }

    /**
     * 清除自定义目录下的文件
     *
     * @param dir 目录
     * @return `true`: 清除成功<br></br>`false`: 清除失败
     */
    @JvmStatic
    fun cleanCustomCache(dir: File): Boolean {
        return FileUtils.deleteFilesInDir(dir)
    }

}
