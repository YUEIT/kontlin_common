package cn.yue.base.common.widget.keyboard

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager

import cn.yue.base.common.R
import cn.yue.base.common.widget.keyboard.mode.IEmotion
import cn.yue.base.common.widget.keyboard.mode.IEmotionPage

/**
 * Description :
 * Created by yue on 2018/11/14
 */
class EmotionPageView(context: Context, attrs: AttributeSet?)
    : LinearLayout(context, attrs) {

    private var emotionPageAdapter: EmotionPageAdapter<IEmotionPage>
    private var viewPager: ViewPager
    private var lastPosition: Int = 0

    init {
        inflate(context, R.layout.layout_emotion_page, this)
        viewPager = findViewById(R.id.emotionVP)
        emotionPageAdapter = EmotionPageAdapter(null)
        viewPager.adapter = emotionPageAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                onPageChangeListener?.invoke(position, lastPosition)
                lastPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    fun setData(pageList: List<IEmotionPage>) {
        emotionPageAdapter.setPageList(pageList)
    }

    fun setCurrentItem(position: Int) {
        lastPosition = position
        viewPager.currentItem = position
    }

    private var onPageChangeListener: ((position: Int, lastPosition: Int) -> Unit)? = null

    fun setOnPageChangeListener(onPageChangeListener: ((position: Int, lastPosition: Int) -> Unit)) {
        this.onPageChangeListener = onPageChangeListener
    }

    fun setOnEmotionClickListener(onEmotionClickListener: ((itemData: IEmotion) -> Unit)?) {
        emotionPageAdapter.setOnEmotionClickListener(onEmotionClickListener)
    }
}
