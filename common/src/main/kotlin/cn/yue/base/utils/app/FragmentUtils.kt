package cn.yue.base.utils.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Description : Fragment 创建工具类
 * Created by yue on 2020/4/9
 */
object FragmentUtils {
    fun instantiate(context: Context, fname: String,
                    args: Bundle? = null): Fragment {
        if (context is FragmentActivity) {
            val f = context.supportFragmentManager.fragmentFactory.instantiate(context.getClassLoader(), fname)
            if (args != null) {
                args.classLoader = f.javaClass.classLoader
                f.arguments = args
            }
            return f
        }
        throw RuntimeException("context not instanceof FragmentActivity")
    }

    fun <T : Fragment?> instantiate(context: Context, fname: Class<T>,
                                    args: Bundle? = null): T {
        if (context is FragmentActivity) {
            val f = context.supportFragmentManager.fragmentFactory.instantiate(context.getClassLoader(), fname.name)
            if (args != null) {
                args.classLoader = f.javaClass.classLoader
                f.arguments = args
            }
            return f as T
        }
        throw RuntimeException("context not instanceof FragmentActivity")
    }
}


