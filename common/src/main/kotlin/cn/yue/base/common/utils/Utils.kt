package cn.yue.base.common.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.yue.base.common.utils.app.ActivityLifecycleImpl
import cn.yue.base.common.utils.code.MMKVUtils
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.utils.file.BitmapFileUtils


/**
 * Utils 包括以下几个部分
 * app: 应用相关
 * ActivityUtils
 * AppUtils
 * BarUtils
 * FragmentUtils
 * IntentUtils
 *
 * code: 代码相关
 * HandlerUtils
 * ProcessUtils 进程工具
 * ServiceUtils   服务工具
 * ShellUtils  root工具
 * SPUtils    sharedPreference工具
 * ThreadPoolUtils    线程池工具
 *
 * Constant:
 * ConstantUtils  常量工具
 * ConvertUtils   基本类型转换工具
 * EncodeUtils    编码解码工具
 * EncryptUtils   加密解密工具
 * ImageUtils     图片类型转换
 * LunarUtils     阴历相关
 * PinyinUtils    拼音
 * RegexUtils     正则
 * SpannableStringUtils   SpannableString
 * StringUtils
 * TimeUtils  时间格式
 *
 * debug
 * CloseUtils
 * CrashUtils
 * EmptyUtils
 * LogUtils
 * ToastUtils
 *
 * device:
 * *CameraUtils
 * ClipboardUtils  粘贴板
 * DeviceUtils
 * KeyboardUtils   软键盘
 * LocationUtils
 * PhoneUtils
 * ScreenUtils
 * *VibrationUtils
 *
 * file
 * CleanUtils   清除内存缓存
 * FileUtils
 * SDCardUtils
 * ZipUtils
 */
@SuppressLint("StaticFieldLeak")
object Utils {

    private var mContext: Context? = null

    /**
     * 初始化工具类
     *
     * @param mContext 上下文
     */
    @JvmStatic
    fun init(mContext: Context) {
        Utils.mContext = mContext.applicationContext
        BitmapFileUtils.init()
        MMKVUtils.init()
        ActivityLifecycleImpl.INSTANCE.init(mContext as Application)
        NetworkUtils.register()
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    @JvmStatic
    fun getContext(): Context {
        if (mContext != null) return mContext!!
        throw NullPointerException("u should init first")
    }

}