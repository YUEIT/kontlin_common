package cn.yue.base.common.widget.wheel

interface AbOnWheelChangedListener {

    /**
     * Callback method to be invoked when current item changed.
     *
     * @param wheel the wheel view whose state has changed
     * @param oldValue the old value of current item
     * @param newValue the new value of current item
     */
    fun onChanged(wheel: WheelView, oldValue: Int, newValue: Int)
}
