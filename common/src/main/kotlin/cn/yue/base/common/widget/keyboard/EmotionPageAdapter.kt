package cn.yue.base.common.widget.keyboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import cn.yue.base.common.R
import cn.yue.base.common.widget.keyboard.mode.IEmotionPage

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class EmotionPageAdapter<in T : IEmotionPage>(private var pageList: List<T>?) : PagerAdapter() {

    fun setPageList(pageList: List<T>) {
        this.pageList = pageList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (pageList != null && pageList!!.isNotEmpty()) { pageList!!.size } else 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val contentView = LayoutInflater.from(context).inflate(R.layout.item_emotion_page, null)
        val emotionRV = contentView.findViewById<RecyclerView>(R.id.emotionRV)
        if (pageList != null && pageList!!.size > position) {
            emotionRV.layoutManager = GridLayoutManager(context, pageList!![position].getRowNum())
            emotionRV.adapter = EmotionAdapter(context, pageList!![position].getEmotionList())
        }
        container.addView(contentView)
        return contentView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view === `object` as View)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }
}
