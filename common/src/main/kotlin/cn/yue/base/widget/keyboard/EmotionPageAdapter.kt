package cn.yue.base.widget.keyboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import cn.yue.base.common.R
import cn.yue.base.utils.code.realSize
import cn.yue.base.widget.keyboard.mode.IEmotion
import cn.yue.base.widget.keyboard.mode.IEmotionPage

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class EmotionPageAdapter<in T : IEmotionPage>(private var pageList: List<T>?) : PagerAdapter() {

    fun setPageList(pageList: List<T>?) {
        this.pageList = pageList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return pageList.realSize()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val contentView = LayoutInflater.from(context).inflate(R.layout.item_emotion_page, null)
        val emotionRV = contentView.findViewById<RecyclerView>(R.id.emotionRV)
        if (pageList.realSize() > position) {
            emotionRV.layoutManager = GridLayoutManager(context, pageList!![position].getRowNum())
            val adapter = EmotionAdapter(context, pageList!![position].getEmotionList())
            adapter.setOnEmotionClickListener {
                onEmotionClickListener?.invoke(it)
            }
            emotionRV.adapter = adapter
        }
        container.addView(contentView)
        return contentView
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return (view === any as View)
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {

    }

    private var onEmotionClickListener: ((itemData: IEmotion) -> Unit)? = null

    fun setOnEmotionClickListener(onEmotionClickListener: ((itemData: IEmotion) -> Unit)?) {
        this.onEmotionClickListener = onEmotionClickListener
    }
}
