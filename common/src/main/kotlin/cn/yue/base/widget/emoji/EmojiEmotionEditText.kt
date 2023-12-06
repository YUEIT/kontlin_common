package cn.yue.base.widget.emoji

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText
import cn.yue.base.R


class EmojiEmotionEditText : AppCompatEditText {
    private var mEmojiEmotionSize: Int = 0
    private var mEmojiEmotionTextSize: Int = 0
    private var mUseSystemDefault = false

    constructor(context: Context) : super(context) {
        mEmojiEmotionSize = textSize.toInt()
        mEmojiEmotionTextSize = textSize.toInt()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Emojicon)
        mEmojiEmotionSize = a.getDimension(R.styleable.Emojicon_emojiconSize, textSize).toInt()
        mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false)
        a.recycle()
        mEmojiEmotionTextSize = textSize.toInt()
    }

    fun addEmojiEmotion(emoji: EmojiEmotion) {
        if (selectionStart == selectionEnd) {
            text?.insert(selectionStart, EmojiEmotion.newString(emoji.codePoint))
        }
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        updateText()
    }

    /**
     * Set the size of emojicon in pixels.
     */
    fun setEmojiEmotionSize(pixels: Int) {
        mEmojiEmotionSize = pixels
        updateText()
    }

    private fun updateText() {
        text?.let {
            EmojiEmotionHandler.ensure(context, it, mEmojiEmotionSize, mEmojiEmotionTextSize)
//            EmojiEmotionHandler.addEmojis(context, it, mEmojiEmotionSize, mEmojiEmotionTextSize, mUseSystemDefault)
        }
    }

    /**
     * Set whether to use system default emojicon
     */
    fun setUseSystemDefault(useSystemDefault: Boolean) {
        mUseSystemDefault = useSystemDefault
    }

    private var onBackKeyClickListener: (()->Unit)? = null

    fun setOnBackKeyClickListener(i: (()->Unit)) {
        onBackKeyClickListener = i
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        onBackKeyClickListener?.invoke()
        return super.dispatchKeyEventPreIme(event)
    }
}
