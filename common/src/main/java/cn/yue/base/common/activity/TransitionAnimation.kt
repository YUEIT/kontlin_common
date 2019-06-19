package cn.yue.base.common.activity

import cn.yue.base.common.R

/**
 * Description :
 * Created by yue on 2019/6/17
 */
object TransitionAnimation{
    //根据启动时入场方式设置参数
    val TRANSITION_RIGHT = 0
    val TRANSITION_LEFT = 1
    val TRANSITION_TOP = 2
    val TRANSITION_BOTTOM = 3
    val TRANSITION_CENTER = 4

    /**
     * 启动一个activitiy时，入场的组件的动画
     * @param transition
     * @return
     */
    fun getStartEnterAnim(transition: Int): Int {
        when (transition) {
            TRANSITION_BOTTOM -> return R.anim.bottom_in
            TRANSITION_TOP -> return R.anim.top_in
            TRANSITION_LEFT -> return R.anim.left_in
            TRANSITION_RIGHT -> return R.anim.right_in
            else -> return R.anim.right_in
        }
    }

    /**
     * 启动一个activity时，退场的组件的动画
     * @param transition
     * @return
     */
    fun getStartExitAnim(transition: Int): Int {
        when (transition) {
            TRANSITION_BOTTOM -> return R.anim.top_out
            TRANSITION_TOP -> return R.anim.bottom_out
            TRANSITION_LEFT -> return R.anim.right_out
            TRANSITION_RIGHT -> return R.anim.left_out
            else -> return R.anim.left_out
        }
    }

    /**
     * 退出activity时，入场的activity的动画
     * @param transition
     * @return
     */
    fun getStopEnterAnim(transition: Int): Int {
        when (transition) {
            TRANSITION_BOTTOM -> return R.anim.top_in
            TRANSITION_TOP -> return R.anim.bottom_in
            TRANSITION_LEFT -> return R.anim.right_in
            TRANSITION_RIGHT -> return R.anim.left_in
            else -> return R.anim.left_in
        }
    }

    /**
     * 退出activity时，退出的activity的动画
     * @param transition
     * @return
     */
    fun getStopExitAnim(transition: Int): Int {
        when (transition) {
            TRANSITION_BOTTOM -> return R.anim.bottom_out
            TRANSITION_TOP -> return R.anim.top_out
            TRANSITION_LEFT -> return R.anim.left_out
            TRANSITION_RIGHT -> return R.anim.right_out
            else -> return R.anim.right_out
        }
    }

    /**
     * 启动一个activitiy时，入场的组件的动画
     * @param transition
     * @return
     */
    fun getWindowEnterStyle(transition: Int): Int {
        when (transition) {
            TRANSITION_BOTTOM -> return R.style.BottomAnimStyle
            TRANSITION_TOP -> return R.style.TopAnimStyle
            TRANSITION_LEFT -> return R.style.LeftAnimStyle
            TRANSITION_RIGHT -> return R.style.RightAnimStyle
            TRANSITION_CENTER -> return R.style.CenterAnimStyle
            else -> return R.style.CenterAnimStyle
        }
    }

}