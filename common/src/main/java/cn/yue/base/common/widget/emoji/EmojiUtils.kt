package cn.yue.base.common.widget.emoji

import java.util.*

/**
 * Description :
 * Created by yue on 2018/11/16
 */
class EmojiUtils {

    companion object {
        private var mEmojiSort: EmojiSort? = null

        fun getEmojiSort(): EmojiSort? {
            return mEmojiSort
        }

        fun clearEmojiSort() {
            mEmojiSort = null
        }

        fun initEmojiSort(position: Int) {
            val sort = EmojiSort()
            val emojiList = Emoji.getEmojiList()
            val pageList = ArrayList<EmojiPage>()
            var i = 0
            while (i < emojiList.size) {
                val emojiPage = EmojiPage()
                var end = i + emojiPage.getRowNum() * emojiPage.getColumnNum()
                if (end > emojiList.size) {
                    end = emojiList.size
                }
                emojiPage.index = i / emojiPage.getRowNum() * emojiPage.getColumnNum() + 1
                emojiPage.setEmotionList(emojiList.subList(i, end))
                pageList.add(emojiPage)
                i = end
            }
            sort.pageList = pageList
            sort.setFirstPagePosition(position)
            sort.setCount(pageList.size)
            sort.setSortIndex(0)
            sort.setSortName("emoji")
            sort.setIconUrl("")
            mEmojiSort = sort
        }

        fun initEmojiSortTest(position: Int, sortId: Int): EmojiSort {
            val sort = EmojiSort()
            val emojiList = Emoji.getEmojiList()
            val pageList = ArrayList<EmojiPage>()
            var i = 0
            while (i < emojiList.size) {
                val emojiPage = EmojiPage()
                var end = i + emojiPage.getRowNum() * emojiPage.getColumnNum()
                if (end > emojiList.size) {
                    end = emojiList.size
                }
                emojiPage.index = i / emojiPage.getRowNum() * emojiPage.getColumnNum() + 1
                emojiPage.setEmotionList(emojiList.subList(i, end))
                pageList.add(emojiPage)
                i = end
            }
            sort.pageList = pageList
            sort.setFirstPagePosition(position)
            sort.setCount(pageList.size)
            sort.setSortIndex(sortId)
            sort.setSortName("emoji")
            sort.setIconUrl("http://pic.imcoming.cn/5868e9ffd932493dabac02bc305d7595_277x277.jpg")
            return sort
        }
    }

}
