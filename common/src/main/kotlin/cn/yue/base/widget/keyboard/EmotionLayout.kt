package cn.yue.base.widget.keyboard

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout

import cn.yue.base.common.R
import cn.yue.base.utils.debug.LogUtils
import cn.yue.base.utils.device.KeyboardUtils
import cn.yue.base.widget.keyboard.mode.EmotionUtils
import cn.yue.base.widget.keyboard.mode.IEmotion

/**
 * Description :
 * Created by yue on 2018/11/14
 */
class EmotionLayout(context: Context, attrs: AttributeSet?)
    : LinearLayout(context, attrs), IKeyboard {

    private val pageView: EmotionPageView

    private var visible: Boolean = false

    private var keyboardHeight: Int = 0

    override fun getKeyboardHeight(): Int {
        return keyboardHeight
    }

    fun setKeyboardHeight(height: Int) {
        keyboardHeight = height
    }

    init {
        EmotionUtils.initAllEmotion()
        inflate(context, R.layout.layout_emotion_sort, this)
        pageView = findViewById(R.id.emotionPageLayout)
        val indicatorView = findViewById<EmotionIndicatorView>(R.id.indicatorView)
        if (!EmotionUtils.getAllEmotionSort().isEmpty()) {
            indicatorView.playTo(0, EmotionUtils.getAllEmotionSort()[0].getCount())
        }
        val bottomSortLayout = findViewById<EmotionBottomSortLayout>(R.id.bottomLayout)
        bottomSortLayout.setEmotionSortList(EmotionUtils.getAllEmotionSort())
        bottomSortLayout.setOnClickEmotionSortListener {
            sort ->
            pageView.setCurrentItem(sort.getFirstPagePosition())
            indicatorView.playTo(0, sort.getCount())
        }
        pageView.setData(EmotionUtils.getAllEmotionPage())
        pageView.setOnPageChangeListener{ position, lastPosition ->
            val currentSort = EmotionUtils.getEmotionSortByPosition(position)
            val lastSort = EmotionUtils.getEmotionSortByPosition(lastPosition)
            if (currentSort == null || lastSort == null) {
                return@setOnPageChangeListener
            }
            LogUtils.d("position:" + position + ",last:" + lastPosition + "; sort id" + currentSort.getSortIndex() + ",last" + lastSort.getSortIndex())
            if (currentSort.getSortIndex() == lastSort.getSortIndex()) {
                indicatorView.playBy(lastPosition - currentSort.getFirstPagePosition(),
                        position - currentSort.getFirstPagePosition(), currentSort.getCount())
            } else {
                if (position - lastPosition > 0) {
                    indicatorView.playTo(0, currentSort.getCount())
                } else {
                    indicatorView.playTo(currentSort.getCount() - 1, currentSort.getCount())
                }
                bottomSortLayout.smoothScrollToPosition(currentSort.getSortIndex())
            }
        }

    }

    fun setOnEmotionClickListener(onEmotionClickListener: ((itemData: IEmotion) -> Unit)?) {
        pageView.setOnEmotionClickListener(onEmotionClickListener)
    }

    fun toggleEmotionShow(editText: EditText) {
        visible = !visible
        if (visible) {
            KeyboardUtils.hideSoftInput(context as Activity)
        } else {
            KeyboardUtils.showSoftInput(editText)
        }
        changeSize(keyboardHeight)
    }

    override fun onKeyboardOpen() {
        visibility = View.VISIBLE
        visible = false
        changeSize(keyboardHeight)
    }

    override fun onKeyboardClose() {
        if (!visible) {
            changeSize(0)
        }
    }

    private fun changeSize(height: Int) {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        setLayoutParams(layoutParams)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EmotionUtils.clearAllEmotion()
    }


}
