package cn.yue.base.widget.emoji

import cn.yue.base.widget.keyboard.mode.IEmotionPage

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class EmojiPage : IEmotionPage {

    var index: Int = 0

    var column = 3

    var row = 7

    private var emotionList: MutableList<EmojiEmotion> = ArrayList()

    fun setEmotionList(emotionList: MutableList<EmojiEmotion>) {
        this.emotionList = emotionList
    }

    override fun getEmotionPageIndex(): Int {
        return index
    }

    override fun getEmotionList(): MutableList<EmojiEmotion> {
        return emotionList
    }

    override fun getColumnNum(): Int {
        return column
    }

    override fun getRowNum(): Int {
        return row
    }

    override fun getCount(): Int {
        return emotionList.size
    }
}
