package cn.yue.base.common.widget.wheel

interface AbWheelAdapter {

    var data: List<*>
    /**
     * Gets items count.
     *
     * @return the count of wheel items
     */
    val itemsCount: Int

    /**
     * Gets maximum item length. It is used to determine the wheel width. If -1
     * is returned there will be used the default wheel width.
     *
     * @return the maximum item length or -1
     */
    val maximumLength: Int

    /**
     * Gets a wheel item by index.
     *
     * @param index
     * the item index
     * @return the wheel item text or null
     */
    fun getItem(index: Int): String

}