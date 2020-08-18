package cn.yue.base.common.widget.wheel

interface AbOnWheelScrollListener {
    /**
     * Callback method to be invoked when scrolling started.
     * @param wheel the wheel view whose state has changed.
     */
    fun onScrollingStarted(wheel: WheelView)

    /**
     * Callback method to be invoked when scrolling ended.
     * @param wheel the wheel view whose state has changed.
     */
    fun onScrollingFinished(wheel: WheelView)
}