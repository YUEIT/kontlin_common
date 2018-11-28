package cn.yue.base.common.widget.keyboard.mode

/**
 * Description :
 * Created by yue on 2018/11/15
 */
interface IEmotionPage {

    fun getEmotionPageIndex(): Int

    fun getColumnNum(): Int //行数

    fun getRowNum(): Int    //列数

    fun getCount(): Int

    fun getEmotionList(): MutableList<out IEmotion>
}
