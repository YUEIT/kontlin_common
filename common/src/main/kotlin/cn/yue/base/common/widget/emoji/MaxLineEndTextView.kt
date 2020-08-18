package cn.yue.base.common.widget.emoji

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

/**
 * 介绍：部分手机end的时候。。。后面还有半个字
 * 作者：qianjujun
 * 邮箱：qianjujun@imcoming.com
 * 时间： 2016/11/23
 */
class MaxLineEndTextView : EmojiconTextView {
    private var textChange: Boolean = false

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}


    override fun setText(text: CharSequence, type: TextView.BufferType) {
        textChange = true
        super.setText(text, type)

    }

    override fun onDraw(canvas: Canvas) {
        if (TextUtils.isEmpty(text) && !textChange) {
            super.onDraw(canvas)
            return
        }
        val maxLines = maxLines
        if (maxLines == 0) {
            super.onDraw(canvas)
            return
        }
        val paint = paint
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight

        val bufferWidth = paint.textSize.toInt() * 1//缓冲区长度，空出三个字符的长度来给最后的省略号和全文

        // 计算出最大行文字所能显示的长度
        val availableTextWidth = (width - paddingLeft - paddingRight) * maxLines - bufferWidth


        // 根据长度截取出剪裁后的文字
        val ellipsizeStr = TextUtils.ellipsize(text, paint, availableTextWidth.toFloat(), TextUtils.TruncateAt.END)


        if (TextUtils.equals(ellipsizeStr, text)) {
            super.onDraw(canvas)
            textChange = false
            return
        }
        text = ellipsizeStr
        textChange = false
        super.onDraw(canvas)
    }
}
