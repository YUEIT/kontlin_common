package cn.yue.base.common.widget.keyboard

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager

import cn.yue.base.common.R
import cn.yue.base.common.widget.keyboard.mode.IEmotionPage

/**
 * Description :
 * Created by yue on 2018/11/14
 */
class EmotionPageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private var emotionPageAdapter: EmotionPageAdapter<IEmotionPage>
    private var viewPager: ViewPager? = null
    private var lastPosition: Int = 0

    init {
        inflate(context, R.layout.layout_emotion_page, this)
        viewPager = findViewById(R.id.emotionVP)
        emotionPageAdapter = EmotionPageAdapter(null)
        viewPager!!.setAdapter(emotionPageAdapter)
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (mListener != null) {
                    mListener!!(position, lastPosition)
                }
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
        if (viewPager != null) {
            viewPager!!.currentItem = position
        }
    }

    interface OnPageChangeListener {
        fun onPageSelected(position: Int, lastPosition: Int)
    }

    private var mListener: ((position: Int, lastPosition: Int) -> Unit)? = null

    fun setOnPageChangeListener(mListener: ((position: Int, lastPosition: Int) -> Unit)) {
        this.mListener = mListener
    }
}
