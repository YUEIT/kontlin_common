package cn.yue.base.widget.keyboard.mode

/**
 * Description :
 * Created by yue on 2018/11/15
 */
interface IEmotionSort {

    fun getFirstPagePosition(): Int

    fun getSortIndex(): Int

    fun getSortName(): String

    fun getIconUrl(): String

    fun getCount(): Int

    fun getEmotionPage(): MutableList<out IEmotionPage>
}
