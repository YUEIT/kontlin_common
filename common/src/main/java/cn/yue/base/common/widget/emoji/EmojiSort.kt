package cn.yue.base.common.widget.emoji

import cn.yue.base.common.widget.keyboard.mode.IEmotionSort

/**
 * Description :
 * Created by yue on 2018/11/16
 */
class EmojiSort : IEmotionSort {

    private var sortIndex: Int = 0
    private var firstPagePosition: Int = 0
    private var sortName: String? = null
    private var iconUrl: String? = null
    private var count: Int = 0
    var pageList: MutableList<EmojiPage>? = null

    fun setSortIndex(sortIndex: Int) {
        this.sortIndex = sortIndex
    }

    fun setFirstPagePosition(firstPagePosition: Int) {
        this.firstPagePosition = firstPagePosition
    }

    fun setSortName(sortName: String) {
        this.sortName = sortName
    }

    fun setIconUrl(iconUrl: String) {
        this.iconUrl = iconUrl
    }

    fun setCount(count: Int) {
        this.count = count
    }

    override fun getSortIndex(): Int {
        return sortIndex
    }

    override fun getFirstPagePosition(): Int {
        return firstPagePosition
    }

    override fun getSortName(): String {
        return sortName?:""
    }

    override fun getIconUrl(): String {
        return iconUrl?:""
    }

    override fun getCount(): Int {
        return count
    }

    override fun getEmotionPage(): MutableList<EmojiPage> {
        return pageList?:ArrayList()
    }
}
