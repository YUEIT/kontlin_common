package cn.yue.base.activity

import cn.yue.base.R

/**
 * Description :
 * Created by yue on 2019/6/17
 */
object TransitionAnimation{
    //根据启动时入场方式设置参数
    const val TRANSITION_RIGHT = 0
    const val TRANSITION_LEFT = 1
    const val TRANSITION_TOP = 2
    const val TRANSITION_BOTTOM = 3
    const val TRANSITION_CENTER = 4

    /**
     * 启动一个activity时，入场的组件的动画
     */
    fun getStartEnterAnim(transition: Int): Int {
        return when (transition) {
            TRANSITION_BOTTOM -> R.anim.bottom_in
            TRANSITION_TOP -> R.anim.top_in
            TRANSITION_LEFT -> R.anim.left_in
            TRANSITION_RIGHT -> R.anim.right_in
            else -> R.anim.right_in
        }
    }

    /**
     * 启动一个activity时，退场的组件的动画
     */
    fun getStartExitAnim(transition: Int): Int {
        return when (transition) {
            TRANSITION_BOTTOM -> R.anim.top_out
            TRANSITION_TOP -> R.anim.bottom_out
            TRANSITION_LEFT -> R.anim.right_out
            TRANSITION_RIGHT -> R.anim.left_out
            else -> R.anim.left_out
        }
    }

    /**
     * 退出activity时，入场的组件的动画
     */
    fun getStopEnterAnim(transition: Int): Int {
        return when (transition) {
            TRANSITION_BOTTOM -> R.anim.top_in
            TRANSITION_TOP -> R.anim.bottom_in
            TRANSITION_LEFT -> R.anim.right_in
            TRANSITION_RIGHT -> R.anim.left_in
            else -> R.anim.left_in
        }
    }

    /**
     * 退出activity时，退出的组件的动画
     */
    fun getStopExitAnim(transition: Int): Int {
        return when (transition) {
            TRANSITION_BOTTOM -> R.anim.bottom_out
            TRANSITION_TOP -> R.anim.top_out
            TRANSITION_LEFT -> R.anim.left_out
            TRANSITION_RIGHT -> R.anim.right_out
            else -> R.anim.right_out
        }
    }

    /**
     * 启动一个window时，入场的组件的动画
     */
    fun getWindowEnterStyle(transition: Int): Int {
        return when (transition) {
            TRANSITION_BOTTOM -> R.style.BottomAnimStyle
            TRANSITION_TOP -> R.style.TopAnimStyle
            TRANSITION_LEFT -> R.style.LeftAnimStyle
            TRANSITION_RIGHT -> R.style.RightAnimStyle
            TRANSITION_CENTER -> R.style.CenterAnimStyle
            else -> R.style.CenterAnimStyle
        }
    }

}