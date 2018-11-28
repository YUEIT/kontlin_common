package cn.yue.base.common.widget.emoji

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import cn.yue.base.common.R


class EmojiconEditText : android.support.v7.widget.AppCompatEditText {
    private var mEmojiconSize: Int = 0
    private var mEmojiconTextSize: Int = 0
    private var mUseSystemDefault = false
    internal var onBackKeyClickListener: OnBackKeyClickListener? = null

    constructor(context: Context) : super(context) {
        mEmojiconSize = textSize.toInt()
        mEmojiconTextSize = textSize.toInt()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Emojicon)
        mEmojiconSize = a.getDimension(R.styleable.Emojicon_emojiconSize, textSize).toInt()
        mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false)
        a.recycle()
        mEmojiconTextSize = textSize.toInt()
        text = text
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        updateText()
    }

    /**
     * Set the size of emojicon in pixels.
     */
    fun setEmojiconSize(pixels: Int) {
        mEmojiconSize = pixels

        updateText()
    }

    private fun updateText() {
        EmojiconHandler.addEmojis(context, text, mEmojiconSize, mEmojiconTextSize, mUseSystemDefault)
    }

    /**
     * Set whether to use system default emojicon
     */
    fun setUseSystemDefault(useSystemDefault: Boolean) {
        mUseSystemDefault = useSystemDefault
    }

    interface OnBackKeyClickListener {
        fun onBackKeyClick()
    }

    fun setOnBackKeyClickListener(i: OnBackKeyClickListener) {
        onBackKeyClickListener = i
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (onBackKeyClickListener != null) {
            onBackKeyClickListener!!.onBackKeyClick()
        }
        return super.dispatchKeyEventPreIme(event)
    }
}
