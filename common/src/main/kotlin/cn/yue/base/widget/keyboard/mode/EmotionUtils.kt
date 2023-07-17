package cn.yue.base.widget.keyboard.mode

import cn.yue.base.widget.emoji.EmojiUtils
import java.util.*

/**
 * Description :
 * Created by yue on 2018/11/16
 */
object EmotionUtils {
    private var mAllEmotionSort: MutableList<IEmotionSort>? = null

    fun getAllEmotionPage(): List<IEmotionPage> {
        val list = ArrayList<IEmotionPage>()
        for (sort in getAllEmotionSort()) {
            list.addAll(sort.getEmotionPage())
        }
        return list
    }

    fun initAllEmotion() {
        EmojiUtils.initEmojiSort(0)
        val list = ArrayList<IEmotionSort>()
        if (EmojiUtils.getEmojiSort() != null) {
            list.add(EmojiUtils.getEmojiSort()!!)
        }
        val sort1 = EmojiUtils.initEmojiSortTest(EmojiUtils.getEmojiSort()!!.getCount(), 1)
        val sort2 = EmojiUtils.initEmojiSortTest(sort1.getFirstPagePosition() + sort1.getCount(), 2)
        val sort3 = EmojiUtils.initEmojiSortTest(sort2.getFirstPagePosition() + sort2.getCount(), 3)
        val sort4 = EmojiUtils.initEmojiSortTest(sort3.getFirstPagePosition() + sort3.getCount(), 4)
        val sort5 = EmojiUtils.initEmojiSortTest(sort4.getFirstPagePosition() + sort4.getCount(), 5)
        val sort6 = EmojiUtils.initEmojiSortTest(sort5.getFirstPagePosition() + sort5.getCount(), 6)
        val sort7 = EmojiUtils.initEmojiSortTest(sort6.getFirstPagePosition() + sort6.getCount(), 7)
        val sort8 = EmojiUtils.initEmojiSortTest(sort7.getFirstPagePosition() + sort7.getCount(), 8)
        val sort9 = EmojiUtils.initEmojiSortTest(sort8.getFirstPagePosition() + sort8.getCount(), 9)
        list.add(sort1)
        list.add(sort2)
        list.add(sort3)
        list.add(sort4)
        list.add(sort5)
        list.add(sort6)
        list.add(sort7)
        list.add(sort8)
        list.add(sort9)
        mAllEmotionSort = list
    }

    fun clearAllEmotion() {
        EmojiUtils.clearEmojiSort()
    }

    fun getAllEmotionSort(): MutableList<IEmotionSort> {
        return if (mAllEmotionSort != null) {
            mAllEmotionSort!!
        } else ArrayList()
    }

    fun isSameSort(position: Int, lastPosition: Int): Boolean {
        return getSortIndexByPosition(position) == getSortIndexByPosition(lastPosition)
    }

    fun getSortIndexByPosition(position: Int): Int {
        return if (getEmotionSortByPosition(position) != null) {
            getEmotionSortByPosition(position)!!.getSortIndex()
        } else -1
    }

    fun getEmotionSortByPosition(position: Int): IEmotionSort? {
        for (sort in getAllEmotionSort()) {
            if (sort.getFirstPagePosition() <= position && sort.getFirstPagePosition() + sort.getCount() > position) {
                return sort
            }
        }
        return null
    }
}
