package cn.yue.base.common.utils.app

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cn.yue.base.common.utils.debug.LogUtils
import java.util.*

/**
 * 介绍：Fragment相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class FragmentUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    internal class Args(var id: Int, var isHide: Boolean, var isAddStack: Boolean)

    class SharedElement(internal var sharedElement: View, internal var name: String)

    class FragmentNode(var fragment: Fragment, var next: List<FragmentNode>?) {

        override fun toString(): String {
            return fragment.javaClass.simpleName + "->" + if (next == null || next!!.isEmpty()) "no child" else next!!.toString()
        }
    }

    interface OnBackClickListener {
        fun onBackClick(): Boolean
    }

    companion object {

        private val TYPE_ADD_FRAGMENT = 0x01
        private val TYPE_CHECK_ADD_FRAGMENT = 0x01 shl 1
        private val TYPE_REMOVE_FRAGMENT = 0x01 shl 2
        private val TYPE_REMOVE_TO_FRAGMENT = 0x01 shl 3
        private val TYPE_REPLACE_FRAGMENT = 0x01 shl 4
        private val TYPE_POP_ADD_FRAGMENT = 0x01 shl 5
        private val TYPE_HIDE_FRAGMENT = 0x01 shl 6
        private val TYPE_SHOW_FRAGMENT = 0x01 shl 7
        private val TYPE_HIDE_SHOW_FRAGMENT = 0x01 shl 8

        private val ARGS_ID = "args_id"
        private val ARGS_IS_HIDE = "args_is_hide"
        private val ARGS_IS_ADD_STACK = "args_is_add_stack"

        /**
         * 新增fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param fragment        fragment
         * @param isHide          是否隐藏
         * @param isAddStack      是否入回退栈
         * @return fragment
         */
        @JvmStatic
        fun addFragment(fragmentManager: FragmentManager,
                        fragment: Fragment,
                        @IdRes containerId: Int,
                        isHide: Boolean = false,
                        isAddStack: Boolean = false): Fragment? {
            putArgs(fragment, Args(containerId, isHide, isAddStack))
            return operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT)
        }

        /**
         * 新增fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param fragment        fragment
         * @param isHide          是否隐藏
         * @param isAddStack      是否入回退栈
         * @return fragment
         */
        @JvmStatic
        fun addFragment(fragmentManager: FragmentManager,
                        fragment: Fragment,
                        @IdRes containerId: Int,
                        isHide: Boolean,
                        isAddStack: Boolean,
                        vararg sharedElement: SharedElement): Fragment? {
            putArgs(fragment, Args(containerId, isHide, isAddStack))
            return operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT, *sharedElement)
        }

        /**
         * 新增fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param fragment        fragment
         * @param isHide          是否隐藏
         * @param isAddStack      是否入回退栈
         * @return fragment
         */
        @JvmStatic
        fun checkAddFragment(fragmentManager: FragmentManager,
                             fragment: Fragment,
                             @IdRes containerId: Int,
                             isHide: Boolean,
                             isAddStack: Boolean): Fragment? {
            putArgs(fragment, Args(containerId, isHide, isAddStack))
            return operateFragment(fragmentManager, null, fragment, TYPE_CHECK_ADD_FRAGMENT)
        }

        /**
         * 新增fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param hideFragment    要隐藏的fragment
         * @param addFragment     新增的fragment
         * @param isHide          是否隐藏
         * @param isAddStack      是否入回退栈
         * @return fragment
         */
        @JvmStatic
        fun hideAddFragment(fragmentManager: FragmentManager,
                            hideFragment: Fragment,
                            addFragment: Fragment,
                            @IdRes containerId: Int,
                            isHide: Boolean,
                            isAddStack: Boolean,
                            vararg sharedElement: SharedElement): Fragment? {
            putArgs(addFragment, Args(containerId, isHide, isAddStack))
            return operateFragment(fragmentManager, hideFragment, addFragment, TYPE_ADD_FRAGMENT, *sharedElement)
        }

        /**
         * 新增多个fragment
         *
         * @param fragmentManager fragment管理器
         * @param fragments       fragments
         * @param containerId     布局Id
         * @param showIndex       要显示的fragment索引
         * @return 要显示的fragment
         */
        @JvmStatic
        fun addFragments(fragmentManager: FragmentManager,
                         fragments: List<Fragment>,
                         @IdRes containerId: Int,
                         showIndex: Int): Fragment {
            var i = 0
            val size = fragments.size
            while (i < size) {
                val fragment = fragments[i]
                if (fragment != null) {
                    addFragment(fragmentManager, fragment, containerId, showIndex != i, false)
                }
                ++i
            }
            return fragments[showIndex]
        }

        /**
         * 新增多个fragment
         *
         * @param fragmentManager fragment管理器
         * @param fragments       fragments
         * @param containerId     布局Id
         * @param showIndex       要显示的fragment索引
         * @return 要显示的fragment
         */
        @JvmStatic
        fun addFragments(fragmentManager: FragmentManager,
                         fragments: List<Fragment>,
                         @IdRes containerId: Int,
                         showIndex: Int,
                         vararg lists: List<SharedElement>): Fragment? {
            var i = 0
            val size = fragments.size
            while (i < size) {
                val fragment = fragments[i]
                val list = lists[i]
                if (fragment != null) {
                    if (list != null) {
                        putArgs(fragment, Args(containerId, showIndex != i, false))
                        return operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT, *list.toTypedArray())
                    }
                }
                ++i
            }
            return fragments[showIndex]
        }

        /**
         * 移除fragment
         *
         * @param fragment fragment
         */
        @JvmStatic
        fun removeFragment(fragment: Fragment) {
            operateFragment(fragment.fragmentManager!!, null, fragment, TYPE_REMOVE_FRAGMENT)
        }

        /**
         * 移除到指定fragment
         *
         * @param fragment      fragment
         * @param isIncludeSelf 是否包括Fragment类自己
         */
        @JvmStatic
        fun removeToFragment(fragment: Fragment, isIncludeSelf: Boolean) {
            operateFragment(fragment.fragmentManager!!, if (isIncludeSelf) fragment else null, fragment, TYPE_REMOVE_TO_FRAGMENT)
        }

        /**
         * 移除同级别fragment
         */
        @JvmStatic
        fun removeFragments(fragmentManager: FragmentManager) {
            val fragments = getFragments(fragmentManager)
            if (fragments.isEmpty()) return
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) removeFragment(fragment)
            }
        }

        /**
         * 移除所有fragment
         */
        @JvmStatic
        fun removeAllFragments(fragmentManager: FragmentManager) {
            val fragments = getFragments(fragmentManager)
            if (fragments.isEmpty()) return
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) {
                    removeAllFragments(fragment.childFragmentManager)
                    removeFragment(fragment)
                }
            }
        }

        /**
         * 替换fragment
         *
         * @param srcFragment  源fragment
         * @param destFragment 目标fragment
         * @param isAddStack   是否入回退栈
         * @return 目标fragment
         */
        @JvmStatic
        fun replaceFragment(srcFragment: Fragment,
                            destFragment: Fragment,
                            isAddStack: Boolean): Fragment? {
            if (srcFragment.arguments == null) return null
            val containerId = srcFragment.arguments!!.getInt(ARGS_ID)
            return if (containerId == 0) null else replaceFragment(srcFragment.fragmentManager!!, destFragment, containerId, isAddStack)
        }

        /**
         * 替换fragment
         *
         * @param srcFragment   源fragment
         * @param destFragment  目标fragment
         * @param isAddStack    是否入回退栈
         * @param sharedElement 共享元素
         * @return 目标fragment
         */
        @JvmStatic
        fun replaceFragment(srcFragment: Fragment,
                            destFragment: Fragment,
                            isAddStack: Boolean,
                            vararg sharedElement: SharedElement): Fragment? {
            if (srcFragment.arguments == null) return null
            val containerId = srcFragment.arguments!!.getInt(ARGS_ID)
            return if (containerId == 0) null else replaceFragment(srcFragment.fragmentManager!!, destFragment, containerId, isAddStack, *sharedElement)
        }

        /**
         * 替换fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param fragment        fragment
         * @param isAddStack      是否入回退栈
         * @return fragment
         */
        @JvmStatic
        fun replaceFragment(fragmentManager: FragmentManager,
                            fragment: Fragment,
                            @IdRes containerId: Int,
                            isAddStack: Boolean): Fragment? {
            putArgs(fragment, Args(containerId, false, isAddStack))
            return operateFragment(fragmentManager, null, fragment, TYPE_REPLACE_FRAGMENT)
        }

        /**
         * 替换fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param fragment        fragment
         * @param isAddStack      是否入回退栈
         * @param sharedElement   共享元素
         * @return fragment
         */
        @JvmStatic
        fun replaceFragment(fragmentManager: FragmentManager,
                            fragment: Fragment,
                            @IdRes containerId: Int,
                            isAddStack: Boolean,
                            vararg sharedElement: SharedElement): Fragment? {
            putArgs(fragment, Args(containerId, false, isAddStack))
            return operateFragment(fragmentManager, null, fragment, TYPE_REPLACE_FRAGMENT, *sharedElement)
        }

        /**
         * 出栈fragment
         *
         * @param fragmentManager fragment管理器
         * @return `true`: 出栈成功<br></br>`false`: 出栈失败
         */
        @JvmStatic
        fun popFragment(fragmentManager: FragmentManager): Boolean {
            return fragmentManager.popBackStackImmediate()
        }

        /**
         * 出栈到指定fragment
         *
         * @param fragmentManager fragment管理器
         * @param fragmentClass   Fragment类
         * @param isIncludeSelf   是否包括Fragment类自己
         * @return `true`: 出栈成功<br></br>`false`: 出栈失败
         */
        @JvmStatic
        fun popToFragment(fragmentManager: FragmentManager,
                          fragmentClass: Class<out Fragment>,
                          isIncludeSelf: Boolean): Boolean {
            return fragmentManager.popBackStackImmediate(fragmentClass.name, if (isIncludeSelf) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0)
        }

        /**
         * 出栈同级别fragment
         *
         * @param fragmentManager fragment管理器
         */
        @JvmStatic
        fun popFragments(fragmentManager: FragmentManager) {
            while (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStackImmediate()
            }
        }

        /**
         * 出栈所有fragment
         *
         * @param fragmentManager fragment管理器
         */
        @JvmStatic
        fun popAllFragments(fragmentManager: FragmentManager) {
            val fragments = getFragments(fragmentManager)
            if (fragments.isEmpty()) return
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) popAllFragments(fragment.childFragmentManager)
            }
            while (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStackImmediate()
            }
        }

        /**
         * 先出栈后新增fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param fragment        fragment
         * @param isAddStack      是否入回退栈
         * @return fragment
         */
        @JvmStatic
        fun popAddFragment(fragmentManager: FragmentManager,
                           fragment: Fragment,
                           @IdRes containerId: Int,
                           isAddStack: Boolean): Fragment? {
            putArgs(fragment, Args(containerId, false, isAddStack))
            return operateFragment(fragmentManager, null, fragment, TYPE_POP_ADD_FRAGMENT)
        }

        /**
         * 先出栈后新增fragment
         *
         * @param fragmentManager fragment管理器
         * @param containerId     布局Id
         * @param fragment        fragment
         * @param isAddStack      是否入回退栈
         * @return fragment
         */
        @JvmStatic
        fun popAddFragment(fragmentManager: FragmentManager,
                           fragment: Fragment,
                           @IdRes containerId: Int,
                           isAddStack: Boolean,
                           vararg sharedElements: SharedElement): Fragment? {
            putArgs(fragment, Args(containerId, false, isAddStack))
            return operateFragment(fragmentManager, null, fragment, TYPE_POP_ADD_FRAGMENT, *sharedElements)
        }

        /**
         * 隐藏fragment
         *
         * @param fragment fragment
         * @return 隐藏的Fragment
         */
        @JvmStatic
        fun hideFragment(fragment: Fragment): Fragment? {
            val args = getArgs(fragment)
            if (args != null) {
                putArgs(fragment, Args(args.id, true, args.isAddStack))
            }
            return operateFragment(fragment.fragmentManager!!, null, fragment, TYPE_HIDE_FRAGMENT)
        }

        /**
         * 隐藏同级别fragment
         *
         * @param fragmentManager fragment管理器
         */
        @JvmStatic
        fun hideFragments(fragmentManager: FragmentManager) {
            val fragments = getFragments(fragmentManager)
            if (fragments.isEmpty()) return
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) hideFragment(fragment)
            }
        }

        /**
         * 显示fragment
         *
         * @param fragment fragment
         * @return show的Fragment
         */
        @JvmStatic
        fun showFragment(fragment: Fragment): Fragment? {
            val args = getArgs(fragment)
            if (args != null) {
                putArgs(fragment, Args(args.id, false, args.isAddStack))
            }
            return operateFragment(fragment.fragmentManager!!, null, fragment, TYPE_SHOW_FRAGMENT)
        }

        /**
         * 显示fragment
         *
         * @param fragment fragment
         * @return show的Fragment
         */
        @JvmStatic
        fun hideAllShowFragment(fragment: Fragment): Fragment? {
            hideFragments(fragment.fragmentManager!!)
            return operateFragment(fragment.fragmentManager!!, null, fragment, TYPE_SHOW_FRAGMENT)
        }

        /**
         * 先隐藏后显示fragment
         *
         * @param hideFragment 需要隐藏的Fragment
         * @param showFragment 需要显示的Fragment
         * @return 显示的Fragment
         */
        @JvmStatic
        fun hideShowFragment(hideFragment: Fragment,
                             showFragment: Fragment): Fragment? {
            var args = getArgs(hideFragment)
            if (args != null) {
                putArgs(hideFragment, Args(args.id, true, args.isAddStack))
            }
            args = getArgs(showFragment)
            if (args != null) {
                putArgs(showFragment, Args(args.id, false, args.isAddStack))
            }
            return operateFragment(showFragment.fragmentManager!!, hideFragment, showFragment, TYPE_HIDE_SHOW_FRAGMENT)
        }

        /**
         * 传参
         *
         * @param fragment fragment
         * @param args     参数
         */
        @JvmStatic
        private fun putArgs(fragment: Fragment, args: Args) {
            var bundle = fragment.arguments
            if (bundle == null) {
                bundle = Bundle()
                fragment.arguments = bundle
            }
            bundle.putInt(ARGS_ID, args.id)
            bundle.putBoolean(ARGS_IS_HIDE, args.isHide)
            bundle.putBoolean(ARGS_IS_ADD_STACK, args.isAddStack)
        }

        /**
         * 获取参数
         *
         * @param fragment fragment
         */
        @JvmStatic
        private fun getArgs(fragment: Fragment): Args? {
            val bundle = fragment.arguments
            return if (bundle == null || bundle.getInt(ARGS_ID) == 0) null else Args(bundle.getInt(ARGS_ID), bundle.getBoolean(ARGS_IS_HIDE), bundle.getBoolean(ARGS_IS_ADD_STACK))
        }

        /**
         * 操作fragment
         *
         * @param fragmentManager fragment管理器
         * @param srcFragment     源fragment
         * @param destFragment    目标fragment
         * @param type            操作类型
         * @param sharedElements  共享元素
         * @return destFragment
         */
        @JvmStatic
        private fun operateFragment(fragmentManager: FragmentManager,
                                    srcFragment: Fragment?,
                                    destFragment: Fragment,
                                    type: Int,
                                    vararg sharedElements: SharedElement): Fragment? {
            if (srcFragment === destFragment) return null
            if (srcFragment != null && srcFragment.isRemoving) {
                LogUtils.e(srcFragment.javaClass.name + " is isRemoving")
                return null
            }
            val name = destFragment.javaClass.name
            val args = destFragment.arguments

            val ft = fragmentManager.beginTransaction()
            if (sharedElements == null || sharedElements.size == 0) {
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            } else {
                for (element in sharedElements) {// 添加共享元素动画
                    ft.addSharedElement(element.sharedElement, element.name)
                }
            }
            when (type) {
                TYPE_ADD_FRAGMENT -> {
                    if (srcFragment != null) ft.hide(srcFragment)
                    ft.add(args!!.getInt(ARGS_ID), destFragment, name)
                    if (args.getBoolean(ARGS_IS_HIDE)) ft.hide(destFragment)
                    if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
                }
                TYPE_CHECK_ADD_FRAGMENT -> {
                    val fragmentList = getFragments(fragmentManager)
                    for (i in fragmentList.indices.reversed()) {
                        val fragment = fragmentList[i]
                        if (fragment === destFragment) {
                            if (srcFragment != null) ft.remove(fragment)
                            break
                        }
                    }
                    ft.add(args!!.getInt(ARGS_ID), destFragment, name)
                    if (args.getBoolean(ARGS_IS_HIDE)) ft.hide(destFragment)
                    if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
                }
                TYPE_REMOVE_FRAGMENT -> ft.remove(destFragment)
                TYPE_REMOVE_TO_FRAGMENT -> {
                    val fragments = getFragments(fragmentManager)
                    for (i in fragments.indices.reversed()) {
                        val fragment = fragments[i]
                        if (fragment === destFragment) {
                            if (srcFragment != null) ft.remove(fragment)
                            break
                        }
                        ft.remove(fragment)
                    }
                }
                TYPE_REPLACE_FRAGMENT -> {
                    ft.replace(args!!.getInt(ARGS_ID), destFragment, name)
                    if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
                }
                TYPE_POP_ADD_FRAGMENT -> {
                    popFragment(fragmentManager)
                    ft.add(args!!.getInt(ARGS_ID), destFragment, name)
                    if (args.getBoolean(ARGS_IS_ADD_STACK)) ft.addToBackStack(name)
                }
                TYPE_HIDE_FRAGMENT -> ft.hide(destFragment)
                TYPE_SHOW_FRAGMENT -> ft.show(destFragment)
                TYPE_HIDE_SHOW_FRAGMENT -> ft.hide(srcFragment!!).show(destFragment)
            }
            ft.commitAllowingStateLoss()
            return destFragment
        }

        /**
         * 获取同级别最后加入的fragment
         *
         * @param fragmentManager fragment管理器
         * @return 最后加入的fragment
         */
        @JvmStatic
        fun getLastAddFragment(fragmentManager: FragmentManager): Fragment? {
            return getLastAddFragmentIsInStack(fragmentManager, false)
        }

        /**
         * 获取栈中同级别最后加入的fragment
         *
         * @param fragmentManager fragment管理器
         * @return 最后加入的fragment
         */
        @JvmStatic
        fun getLastAddFragmentInStack(fragmentManager: FragmentManager): Fragment? {
            return getLastAddFragmentIsInStack(fragmentManager, true)
        }

        /**
         * 根据栈参数获取同级别最后加入的fragment
         *
         * @param fragmentManager fragment管理器
         * @param isInStack       是否是栈中的
         * @return 栈中最后加入的fragment
         */
        @JvmStatic
        private fun getLastAddFragmentIsInStack(fragmentManager: FragmentManager,
                                                isInStack: Boolean): Fragment? {
            val fragments = getFragments(fragmentManager)
            if (fragments.isEmpty()) return null
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) {
                    if (isInStack) {
                        if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                            return fragment
                        }
                    } else {
                        return fragment
                    }
                }
            }
            return null
        }

        /**
         * 获取顶层可见fragment
         *
         * @param fragmentManager fragment管理器
         * @return 顶层可见fragment
         */
        @JvmStatic
        fun getTopShowFragment(fragmentManager: FragmentManager): Fragment? {
            return getTopShowFragmentIsInStack(fragmentManager, null, false)
        }

        /**
         * 获取栈中顶层可见fragment
         *
         * @param fragmentManager fragment管理器
         * @return 栈中顶层可见fragment
         */
        @JvmStatic
        fun getTopShowFragmentInStack(fragmentManager: FragmentManager): Fragment? {
            return getTopShowFragmentIsInStack(fragmentManager, null, true)
        }

        /**
         * 根据栈参数获取顶层可见fragment
         *
         * @param fragmentManager fragment管理器
         * @param parentFragment  父fragment
         * @param isInStack       是否是栈中的
         * @return 栈中顶层可见fragment
         */
        @JvmStatic
        private fun getTopShowFragmentIsInStack(fragmentManager: FragmentManager,
                                                parentFragment: Fragment?,
                                                isInStack: Boolean): Fragment? {
            val fragments = getFragments(fragmentManager)
            if (fragments.isEmpty()) return parentFragment
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null && fragment.isResumed && fragment.isVisible && fragment.userVisibleHint) {
                    if (isInStack) {
                        if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                            return getTopShowFragmentIsInStack(fragment.childFragmentManager, fragment, true)
                        }
                    } else {
                        return getTopShowFragmentIsInStack(fragment.childFragmentManager, fragment, false)
                    }
                }
            }
            return parentFragment
        }

        /**
         * 获取同级别fragment
         *
         * @param fragmentManager fragment管理器
         * @return 同级别的fragments
         */
        @JvmStatic
        fun getFragments(fragmentManager: FragmentManager): List<Fragment> {
            return getFragmentsIsInStack(fragmentManager, false)
        }

        /**
         * 获取栈中同级别fragment
         *
         * @param fragmentManager fragment管理器
         * @return 栈中同级别fragment
         */
        @JvmStatic
        fun getFragmentsInStack(fragmentManager: FragmentManager): List<Fragment> {
            return getFragmentsIsInStack(fragmentManager, true)
        }

        /**
         * 根据栈参数获取同级别fragment
         *
         * @param fragmentManager fragment管理器
         * @param isInStack       是否是栈中的
         * @return 栈中同级别fragment
         */
        @JvmStatic
        private fun getFragmentsIsInStack(fragmentManager: FragmentManager, isInStack: Boolean): List<Fragment> {
            val fragments = fragmentManager.fragments
            if (fragments == null || fragments.isEmpty()) return emptyList()
            val result = ArrayList<Fragment>()
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) {
                    if (isInStack) {
                        if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                            result.add(fragment)
                        }
                    } else {
                        result.add(fragment)
                    }
                }
            }
            return result
        }

        /**
         * 获取所有fragment
         *
         * @param fragmentManager fragment管理器
         * @return 所有fragment
         */
        @JvmStatic
        fun getAllFragments(fragmentManager: FragmentManager): List<FragmentNode> {
            return getAllFragmentsIsInStack(fragmentManager, ArrayList(), false)
        }

        /**
         * 获取栈中所有fragment
         *
         * @param fragmentManager fragment管理器
         * @return 所有fragment
         */
        fun getAllFragmentsInStack(fragmentManager: FragmentManager): List<FragmentNode> {
            return getAllFragmentsIsInStack(fragmentManager, ArrayList(), true)
        }

        /**
         * 根据栈参数获取所有fragment
         *
         * 需之前对fragment的操作都借助该工具类
         *
         * @param fragmentManager fragment管理器
         * @param result          结果
         * @param isInStack       是否是栈中的
         * @return 栈中所有fragment
         */
        private fun getAllFragmentsIsInStack(fragmentManager: FragmentManager,
                                             result: MutableList<FragmentNode>,
                                             isInStack: Boolean): List<FragmentNode> {
            val fragments = fragmentManager.fragments
            if (fragments == null || fragments.isEmpty()) return emptyList()
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null) {
                    if (isInStack) {
                        if (fragment.arguments!!.getBoolean(ARGS_IS_ADD_STACK)) {
                            result.add(FragmentNode(fragment, getAllFragmentsIsInStack(fragment.childFragmentManager, ArrayList(), true)))
                        }
                    } else {
                        result.add(FragmentNode(fragment, getAllFragmentsIsInStack(fragment.childFragmentManager, ArrayList(), false)))
                    }
                }
            }
            return result
        }

        /**
         * 获取目标fragment的前一个fragment
         *
         * @param destFragment 目标fragment
         * @return 目标fragment的前一个fragment
         */
        @JvmStatic
        fun getPreFragment(destFragment: Fragment): Fragment? {
            val fragmentManager = destFragment.fragmentManager ?: return null
            val fragments = getFragments(fragmentManager)
            var flag = false
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (flag && fragment != null) {
                    return fragment
                }
                if (fragment === destFragment) {
                    flag = true
                }
            }
            return null
        }

        /**
         * 查找fragment
         *
         * @param fragmentManager fragment管理器
         * @param fragmentClass   fragment类
         * @return 查找到的fragment
         */
        @JvmStatic
        fun findFragment(fragmentManager: FragmentManager, fragmentClass: Class<out Fragment>): Fragment? {
            val fragments = getFragments(fragmentManager)
            return if (fragments.isEmpty()) null else fragmentManager.findFragmentByTag(fragmentClass.name)
        }

        /**
         * 处理fragment回退键
         *
         * 如果fragment实现了OnBackClickListener接口，返回`true`: 表示已消费回退键事件，反之则没消费
         *
         * 具体示例见FragmentActivity
         *
         * @param fragment fragment
         * @return 是否消费回退事件
         */
        @JvmStatic
        fun dispatchBackPress(fragment: Fragment): Boolean {
            return dispatchBackPress(fragment.fragmentManager!!)
        }

        /**
         * 处理fragment回退键
         *
         * 如果fragment实现了OnBackClickListener接口，返回`true`: 表示已消费回退键事件，反之则没消费
         *
         * 具体示例见FragmentActivity
         *
         * @param fragmentManager fragment管理器
         * @return 是否消费回退事件
         */
        @JvmStatic
        fun dispatchBackPress(fragmentManager: FragmentManager): Boolean {
            val fragments = fragmentManager.fragments
            if (fragments == null || fragments.isEmpty()) return false
            for (i in fragments.indices.reversed()) {
                val fragment = fragments[i]
                if (fragment != null
                        && fragment.isResumed
                        && fragment.isVisible
                        && fragment.userVisibleHint
                        && fragment is OnBackClickListener
                        && (fragment as OnBackClickListener).onBackClick()) {
                    return true
                }
            }
            return false
        }

        /**
         * 设置背景色
         *
         * @param fragment fragment
         * @param color    背景色
         */
        @JvmStatic
        fun setBackgroundColor(fragment: Fragment, @ColorInt color: Int) {
            val view = fragment.view
            view?.setBackgroundColor(color)
        }

        /**
         * 设置背景资源
         *
         * @param fragment fragment
         * @param resId    资源Id
         */
        @JvmStatic
        fun setBackgroundResource(fragment: Fragment, @DrawableRes resId: Int) {
            val view = fragment.view
            view?.setBackgroundResource(resId)
        }

        /**
         * 设置背景
         *
         * @param fragment   fragment
         * @param background 背景
         */
        @JvmStatic
        fun setBackground(fragment: Fragment, background: Drawable) {
            val view = fragment.view
            if (view != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.background = background
                } else {
                    view.setBackgroundDrawable(background)
                }
            }
        }
    }
}
