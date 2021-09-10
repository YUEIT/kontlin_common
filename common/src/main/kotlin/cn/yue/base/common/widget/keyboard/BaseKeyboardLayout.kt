package cn.yue.base.common.widget.keyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import cn.yue.base.common.utils.device.KeyboardUtils


/**
 * Description :
 * Created by yue on 2018/11/14
 */
abstract class BaseKeyboardLayout(context: Context, attrs: AttributeSet?)
    : RelativeLayout(context, attrs), IKeyboard {

    private var mEmotionLayout: EmotionLayout
    private val keyboardHelp: KeyboardHelp = KeyboardHelp()

    abstract fun getLayoutId(): Int

    override fun getKeyboardHeight(): Int {
        return keyboardHelp.keyboardHeight
    }

    var mMaxParentHeight: Int = 0

    init {
        keyboardHelp.addOnSoftKeyBoardVisibleListener(context, this)
        View.inflate(context, getLayoutId(), this)
        mEmotionLayout = getEmotionLayout()
        initView(context)
    }

    protected open fun initView(context: Context) {}

    protected abstract fun getEmotionLayout(): EmotionLayout

    override fun onKeyboardOpen() {
            mEmotionLayout.setKeyboardHeight(keyboardHelp.keyboardHeight)
            mEmotionLayout.onKeyboardOpen()
    }

    override fun onKeyboardClose() {
            mEmotionLayout.setKeyboardHeight(keyboardHelp.keyboardHeight)
            mEmotionLayout.onKeyboardClose()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mMaxParentHeight != 0) {
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val expandSpec = MeasureSpec.makeMeasureSpec(mMaxParentHeight, heightMode)
            super.onMeasure(widthMeasureSpec, expandSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    @SuppressLint("ResourceType")
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        val childSum = childCount
        if (childSum > 1) {
            throw IllegalStateException("can host only one direct child")
        }
        super.addView(child, index, params)
        if (childSum == 0) {
            if (child.id < 0) {
                child.id = 101
            }
            val paramsChild = child.layoutParams as LayoutParams
            paramsChild.addRule(ALIGN_PARENT_BOTTOM)
            child.layoutParams = paramsChild
        } else if (childSum == 1) {
            val paramsChild = child.layoutParams as LayoutParams
            paramsChild.addRule(ABOVE, 101)
            child.layoutParams = paramsChild
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (mMaxParentHeight == 0) {
            mMaxParentHeight = h
        }
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
        return if (KeyboardUtils.isFullScreen(context as Activity)) {
            false
        } else {
            super.requestFocus(direction, previouslyFocusedRect)
        }
    }

    override fun requestChildFocus(child: View, focused: View) {
        if (!KeyboardUtils.isFullScreen(context as Activity)) {
            super.requestChildFocus(child, focused)
        }
    }

}
