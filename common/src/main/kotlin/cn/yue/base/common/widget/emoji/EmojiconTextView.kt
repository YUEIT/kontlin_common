package cn.yue.base.common.widget.emoji

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import cn.yue.base.common.R

open class EmojiconTextView : AppCompatTextView {
    private var mEmojiconSize: Int = 0
    private var mEmojiconTextSize: Int = 0
    private var mTextStart = 0
    private var mTextLength = -1
    private var mUseSystemDefault = false
    private var isCopy: Boolean = false

    private var myClipboard: ClipboardManager? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mEmojiconTextSize = textSize.toInt()
        if (attrs == null) {
            mEmojiconSize = textSize.toInt()
        } else {
            val a = context.obtainStyledAttributes(attrs, R.styleable.Emojicon)
            mEmojiconSize = a.getDimension(R.styleable.Emojicon_emojiconSize, textSize).toInt()
            mTextStart = a.getInteger(R.styleable.Emojicon_emojiconTextStart, 0)
            mTextLength = a.getInteger(R.styleable.Emojicon_emojiconTextLength, -1)
            mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false)
            isCopy = a.getBoolean(R.styleable.Emojicon_emojiconCopy, false)
            a.recycle()
        }
        text = text

        if (isCopy) {
            setOnLongClickListener {
                if (myClipboard == null) {
                    myClipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                }
                AlertDialog.Builder(context)
                        .setItems(arrayOf("复制", "取消")) { dialog, which ->
                            when (which) {
                                0 -> {
                                    val myClip = ClipData.newPlainText("text", text)
//                                    myClipboard!!.primaryClip = myClip
                                }
                                1 -> {
                                }
                            }
                            dialog.dismiss()
                        }.create().show()

                true
            }
        }
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        var text = text
        if (!TextUtils.isEmpty(text)) {
            val builder = SpannableStringBuilder(text)
            EmojiconHandler.addEmojis(context, builder, mEmojiconSize, mEmojiconTextSize, mTextStart, mTextLength, mUseSystemDefault)
            text = builder
        }
        super.setText(text, type)
    }

    /**
     * Set the size of emojicon in pixels.
     */
    fun setEmojiconSize(pixels: Int) {
        mEmojiconSize = pixels
        super.setText(text)
    }

    /**
     * Set whether to use system default emojicon
     */
    fun setUseSystemDefault(useSystemDefault: Boolean) {
        mUseSystemDefault = useSystemDefault
    }
}