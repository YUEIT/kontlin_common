package cn.yue.base.common.widget.emoji

import cn.yue.base.common.widget.keyboard.mode.IEmotionPage

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class EmojiPage : IEmotionPage {

    var index: Int = 0

    var column = 3

    var row = 7

    private var emotionList: MutableList<Emojicon> = ArrayList()

    fun setEmotionList(emotionList: MutableList<Emojicon>) {
        this.emotionList = emotionList
    }

    override fun getEmotionPageIndex(): Int {
        return index
    }

    override fun getEmotionList(): MutableList<Emojicon> {
        return emotionList
    }

    override fun getColumnNum(): Int {
        return column
    }

    override fun getRowNum(): Int {
        return row
    }

    override fun getCount(): Int {
        if (emotionList != null) {
            emotionList!!.size
        }
        return 0
    }
}
