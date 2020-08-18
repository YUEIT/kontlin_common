package cn.yue.base.common.widget.emoji

import android.content.Context
import android.graphics.Canvas
import android.text.SpannableStringBuilder
import android.util.AttributeSet

class EmojiconSigleTextView : EmojiconTextView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onDraw(canvas: Canvas) {
        val charSequence = text
        val lastCharDown = layout.getLineVisibleEnd(0)
        if (charSequence.length > lastCharDown && lastCharDown > 4) {
            val spannableStringBuilder = SpannableStringBuilder()
            if (lastCharDown > 13) {
                spannableStringBuilder.append(charSequence.subSequence(0, lastCharDown - 4)).append("...")
            } else {
                spannableStringBuilder.append(charSequence.subSequence(0, lastCharDown)).append("...")
            }
            text = spannableStringBuilder
        }
        super.onDraw(canvas)
    }
}