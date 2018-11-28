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
abstract class BaseKeyboardLayout
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr), IKeyboard {

    private val mEmotionLayout: EmotionLayout
    private val keyboardHelp: KeyboardHelp?

    protected abstract val layoutId: Int

    override fun getKeyboardHeight(): Int{
            keyboardHelp?.keyboardHeight
            return 0
        }

    protected var mMaxParentHeight: Int = 0

    init {
        keyboardHelp = KeyboardHelp()
        keyboardHelp.addOnSoftKeyBoardVisibleListener(context, this)
        View.inflate(context, layoutId, this)
        mEmotionLayout = getEmotionLayout()
        //初始时，还没测绘到尺寸，先隐藏
        mEmotionLayout!!.visibility = View.GONE
        initView(context)
    }

    protected open fun initView(context: Context) {}

    protected abstract fun getEmotionLayout(): EmotionLayout

    override fun onKeyboardOpen() {
            mEmotionLayout.setKeyboardHeight(keyboardHelp!!.keyboardHeight)
            mEmotionLayout.onKeyboardOpen()
    }

    override fun onKeyboardClose() {
            mEmotionLayout.setKeyboardHeight(keyboardHelp!!.keyboardHeight)
            mEmotionLayout.onKeyboardClose()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mMaxParentHeight != 0) {
            val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
            val expandSpec = View.MeasureSpec.makeMeasureSpec(mMaxParentHeight, heightMode)
            super.onMeasure(widthMeasureSpec, expandSpec)
            return
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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
                child.id = ID_CHILD
            }
            val paramsChild = child.layoutParams as RelativeLayout.LayoutParams
            paramsChild.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            child.layoutParams = paramsChild
        } else if (childSum == 1) {
            val paramsChild = child.layoutParams as RelativeLayout.LayoutParams
            paramsChild.addRule(RelativeLayout.ABOVE, ID_CHILD)
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
        } else super.requestFocus(direction, previouslyFocusedRect)
    }

    override fun requestChildFocus(child: View, focused: View) {
        if (KeyboardUtils.isFullScreen(context as Activity)) {
            return
        }
        super.requestChildFocus(child, focused)
    }

    companion object {
        private val ID_CHILD = 101
    }

}
