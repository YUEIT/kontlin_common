package cn.yue.base.widget.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.CallSuper
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatEditText
import cn.yue.base.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class FormattedEditText : AppCompatEditText {
    private var mTouchSlop = 0
    private var mHolders: Array<Placeholder?>? = null

    @Mode
    private var mode = MODE_NONE
    private var placeholder = 0.toChar()
    private var emptyPlaceholder = 0.toChar()
    private var mark = 0.toChar()
    private var mPlaceholders: String? = null
    private var hintText: String? = null
    private var formatStyle: String? = null
    private var isShowHintWhileEmpty = false
    private var hintColor = Color.TRANSPARENT
    private var mLastIndex = 0
    private var mIsFormatted = false
    private var mWatchers: MutableList<TextWatcher>? = null
    private var mClearDrawable: Drawable? = null
    private var mGravity = GRAVITY_CENTER
    private var mRealPaddingRight = 0
    private var mDrawablePadding = 0
    private val mDownPoint = FloatArray(2)
    private var mClearClickListener: OnClearClickListener? = null
    private var mTextWatcher: FormattedTextWatcher? = null
    private var mLengthFilterDelegate: LengthFilterDelegate? = null
    private var mRestoring = false
    private var mFilterRestoreTextChangeEvent = false
    private val mComparator = PlaceholderComparator()
    private var mEscapeIndexes: IntArray? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mTextWatcher = FormattedTextWatcher()
        super.addTextChangedListener(mTextWatcher)
        val viewConfiguration = ViewConfiguration.get(getContext())
        mTouchSlop = viewConfiguration.scaledTouchSlop
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(
                attrs, R.styleable.FormattedEditText, defStyleAttr, 0
            )
            try {
                @Mode val mode = ta.getInt(R.styleable.FormattedEditText_fet_mode, MODE_NONE)
                val mark = ta.getString(R.styleable.FormattedEditText_fet_mark)
                val hintColor = ta.getColor(R.styleable.FormattedEditText_fet_hintTextColor, 0)
                val placeHolder = ta.getString(R.styleable.FormattedEditText_fet_placeholder)
                val emptyPlaceHolder =
                    ta.getString(R.styleable.FormattedEditText_fet_emptyPlaceholder)
                val formatStyle = ta.getString(R.styleable.FormattedEditText_fet_formatStyle)
                val hintText = ta.getString(R.styleable.FormattedEditText_fet_hintText)
                val showHintWhileEmpty =
                    ta.getBoolean(R.styleable.FormattedEditText_fet_showHintWhileEmpty, false)
                mClearDrawable = ta.getDrawable(R.styleable.FormattedEditText_fet_clearDrawable)
                mGravity = ta.getInt(
                    R.styleable.FormattedEditText_fet_drawableGravity, GRAVITY_CENTER
                )
                mDrawablePadding = ta.getDimensionPixelSize(
                    R.styleable.FormattedEditText_fet_drawablePadding, 0
                )
                setConfig(
                    Config.create()
                        .mode(mode)
                        .placeholder(
                            if (placeHolder == null || placeHolder.isEmpty()) DEFAULT_PLACE_HOLDER else placeHolder[0]
                        )
                        .hintColor(hintColor)
                        .hintText(hintText)
                        .mark(
                            if (mark == null || mark.isEmpty()) DEFAULT_MARK else mark[0]
                        )
                        .emptyPlaceholder(
                            if (emptyPlaceHolder == null || emptyPlaceHolder.isEmpty()) 0.toChar() else emptyPlaceHolder[0]
                        )
                        .formatStyle(formatStyle)
                        .showHintWhileEmpty(showHintWhileEmpty),
                    true
                )
            } finally {
                ta.recycle()
            }
        }
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                throw UnsupportedOperationException(
                    "We can not support this feature when the layout is right-to-left"
                )
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetClearDrawableBound()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mClearDrawable != null) {
            val width = mClearDrawable!!.intrinsicWidth + mDrawablePadding * 2
            val height = mClearDrawable!!.intrinsicHeight + mDrawablePadding * 2
            val measuredWidth = measuredWidth
            val measuredHeight = measuredHeight
            var remeasuredWidth = measuredWidth
            var remeasuredHeight = measuredHeight
            if (measuredWidth < width) {
                val specMode = MeasureSpec.getMode(widthMeasureSpec)
                val specSize = MeasureSpec.getSize(widthMeasureSpec)
                if (specMode != MeasureSpec.EXACTLY) {
                    remeasuredWidth = Math.max(width, measuredWidth)
                    if (specMode == MeasureSpec.AT_MOST) {
                        remeasuredWidth = Math.min(remeasuredWidth, specSize)
                    }
                }
            }
            if (measuredHeight < height) {
                val specMode = MeasureSpec.getMode(heightMeasureSpec)
                val specSize = MeasureSpec.getSize(heightMeasureSpec)
                if (specMode != MeasureSpec.EXACTLY) {
                    remeasuredHeight = Math.max(height, measuredHeight)
                    if (specMode == MeasureSpec.AT_MOST) {
                        remeasuredHeight = Math.min(remeasuredHeight, specSize)
                    }
                }
            }
            if (remeasuredWidth != measuredWidth || remeasuredHeight != measuredHeight) {
                setMeasuredDimension(remeasuredWidth, remeasuredHeight)
            }
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher) {
        if (mWatchers == null) {
            mWatchers = ArrayList()
        }
        mWatchers!!.add(watcher)
    }

    override fun removeTextChangedListener(watcher: TextWatcher) {
        if (mWatchers != null) {
            mWatchers!!.remove(watcher)
        }
    }

    override fun setPadding(left: Int, top: Int, mRight: Int, bottom: Int) {
        var right = mRight
        mRealPaddingRight = right
        if (mClearDrawable != null) {
            right += mClearDrawable!!.intrinsicWidth + mDrawablePadding * 2
        }
        super.setPadding(left, top, right, bottom)
        resetClearDrawableBound()
    }

    override fun setPaddingRelative(start: Int, top: Int, mEnd: Int, bottom: Int) {
        var end = mEnd
        mRealPaddingRight = end
        if (mClearDrawable != null) {
            end += mClearDrawable!!.intrinsicWidth + mDrawablePadding * 2
        }
        super.setPaddingRelative(start, top, end, bottom)
        resetClearDrawableBound()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mClearDrawable != null && isFocused && length() > 0) {
            canvas.save()
            canvas.translate(scrollX.toFloat(), scrollY.toFloat())
            mClearDrawable!!.draw(canvas)
            canvas.restore()
        }
    }

    override fun drawableStateChanged() {
        if (mClearDrawable != null) {
            val state = drawableState
            if (mClearDrawable!!.isStateful && mClearDrawable!!.setState(state)) {
                val dirty = mClearDrawable!!.bounds
                val scrollX = scrollX
                val scrollY = scrollY
                invalidate(
                    dirty.left + scrollX,
                    dirty.top + scrollY,
                    dirty.right + scrollX,
                    dirty.bottom + scrollY
                )
            }
        }
        super.drawableStateChanged()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mClearDrawable != null) {
            val x = event.x
            val y = event.y
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    mDownPoint[0] = x
                    mDownPoint[1] = y
                }
                MotionEvent.ACTION_UP -> {
                    val rect = mClearDrawable!!.bounds
                    if (rect.top - mDrawablePadding <= y
                        && rect.bottom + mDrawablePadding >= y
                        && rect.left - mDrawablePadding <= x
                        && rect.right + mDrawablePadding >= x) {
                        if (Math.abs(mDownPoint[0] - x) <= mTouchSlop
                            && Math.abs(mDownPoint[1] - y) <= mTouchSlop
                        ) {
                            if (mClearClickListener != null) {
                                if (!mClearClickListener!!.onClearClick(this, mClearDrawable)) {
                                    clearTextInTouch(event)
                                }
                            } else {
                                clearTextInTouch(event)
                            }
                            super.onTouchEvent(event)
                            return true
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun setClearDrawable(drawable: Drawable) {
        if (mClearDrawable !== drawable) {
            mClearDrawable = drawable
            requestLayout()
        }
    }

    fun setClearDrawablePadding(pad: Int) {
        if (mDrawablePadding != pad) {
            mDrawablePadding = pad
            if (mClearDrawable != null) {
                requestLayout()
            }
        }
    }

    fun setOnClearClickListener(clickListener: OnClearClickListener?) {
        mClearClickListener = clickListener
    }

    @CallSuper
    override fun setFilters(filters: Array<InputFilter>) {
        requireNotNull(filters) { "filters can not be null" }
        var havingFilter = false
        for (i in filters.indices) {
            if (filters[i] is LengthFilter) {
                mLengthFilterDelegate = LengthFilterDelegate(filters[i])
                filters[i] = mLengthFilterDelegate!!
            } else if (filters[i] is PlaceholderFilter) {
                havingFilter = true
            }
        }
        if (!havingFilter) {
            val replaceFilters = arrayOfNulls<InputFilter>(filters.size + 1)
            replaceFilters[0] = PlaceholderFilter()
            System.arraycopy(filters, 0, replaceFilters, 1, filters.size)
            super.setFilters(replaceFilters)
            return
        }
        super.setFilters(filters)
    }

    private fun clearTextInTouch(event: MotionEvent) {
        event.action = MotionEvent.ACTION_CANCEL
        val editable = text
        editable?.clear()
    }

    private fun setConfig(config: Config, create: Boolean) {
        if (config.mMode != null) {
            mode = config.mMode!!
        }
        if (mode == MODE_NONE) {
            clearArray(mHolders)
            mHolders = null
            return
        }
        if (config.mFormatStyle != null) {
            formatStyle = config.mFormatStyle
            if (mode == MODE_SIMPLE) {
                if (config.mPlaceholder != null) {
                    placeholder = config.mPlaceholder!!
                }
                parseSimplePlaceholders()
            } else if (mode == MODE_COMPLEX) {
                if (config.mMark != null) {
                    mark = config.mMark!!
                }
                parseComplexPlaceholders()
            } else {
                val length = formatStyle!!.length
                val temp = IntArray(length)
                var index = 0
                for (i in formatStyle!!.indices) {
                    val c = formatStyle!![i]
                    if (c == ESCAPE_CHAR) {
                        temp[index] = i
                        index++
                    }
                }
                val indexes = IntArray(index)
                System.arraycopy(temp, 0, indexes, 0, index)
                mEscapeIndexes = indexes
                if (mode == MODE_HINT) {
                    checkHintStyleIsRight(config.mHintText)
                }
            }
        } else if (formatStyle != null) {
            if (mode == MODE_SIMPLE) {
                if (config.mPlaceholder != null && placeholder != config.mPlaceholder) {
                    placeholder = config.mPlaceholder!!
                    if (mHolders != null) {
                        val placeholders: Array<Placeholder?> = mHolders!!
                        for (holder in placeholders) {
                            holder?.holder = config.mPlaceholder!!
                        }
                        mPlaceholders = placeholder.toString()
                    } else {
                        parseSimplePlaceholders()
                    }
                }
            } else if (mode == MODE_COMPLEX) {
                if (config.mMark != null && mark != config.mMark) {
                    mark = config.mMark!!
                    parseComplexPlaceholders()
                }
            } else if (mode == MODE_HINT) {
                checkHintStyleIsRight(config.mHintText)
            }
        } else {
            throw IllegalArgumentException("Format style can not be empty")
        }
        if (config.mShowHintWhileEmpty != null) {
            isShowHintWhileEmpty = config.mShowHintWhileEmpty!!
        }
        if (config.mEmptyPlaceholder != null) {
            emptyPlaceholder = config.mEmptyPlaceholder!!
        }
        if (config.mHintColor != null) {
            hintColor = config.mHintColor!!
        }
        var text = text
        if (text == null || text.isEmpty()) {
            return
        }
        if (!create) {
            setText(realText)
        } else {
            setText(text)
        }
        text = getText()
        Selection.setSelection(text, text!!.length)
    }

    private fun parseSimplePlaceholders() {
        if (TextUtils.isDigitsOnly(formatStyle)) {
            mHolders = arrayOfNulls(formatStyle!!.length)
            var holder = Placeholder()
            var index = Character.getNumericValue(formatStyle!![0])
            holder.index = index
            holder.holder = placeholder
            mHolders!![0] = holder
            for (i in 1 until formatStyle!!.length) {
                val number = Character.getNumericValue(formatStyle!![i])
                holder = Placeholder()
                index = mHolders!![i - 1]!!.index + 1 + number
                holder.index = index
                holder.holder = placeholder
                mHolders!![i] = holder
            }
            mPlaceholders = placeholder.toString()
        } else {
            throw IllegalArgumentException("Format style must be numeric")
        }
    }

    private fun parseComplexPlaceholders() {
        require(formatStyle!!.indexOf(mark) != -1) { "Format style must be have Mark strings" }
        val length = formatStyle!!.length
        val temp = arrayOfNulls<Placeholder>(length)
        var realCount = 0
        var holder: Placeholder
        val builder = StringBuilder()
        for (i in 0 until length) {
            val sub = formatStyle!![i]
            if (mark != sub) {
                if (!Character.isDigit(sub)) {
                    builder.append(sub)
                }
                holder = Placeholder()
                holder.index = i
                holder.holder = sub
                temp[realCount] = holder
                realCount += 1
            }
        }
        if (length > 0) {
            holder = Placeholder()
            holder.index = length
            holder.holder = 0.toChar()
            temp[realCount] = holder
            realCount += 1
        }
        mPlaceholders = builder.toString()
        mHolders = arrayOfNulls(realCount)
        System.arraycopy(temp, 0, mHolders, 0, realCount)
        clearArray(temp)
    }

    val realText: String
        get() = getRealText(false)

    private fun getRealText(saved: Boolean): String {
        if (saved && mode == MODE_NONE) {
            return ""
        }
        val editable = text
        if (editable == null || editable.isEmpty()) {
            return ""
        }
        val value = SpannableStringBuilder(editable)
        val spans: Array<IPlaceholderSpan?>
        if (mode == MODE_NONE) {
            spans = EMPTY_SPANS
        } else if (mode < MODE_MASK) {
            spans = value.getSpans(
                0,
                Math.min(value.length, mHolders!![mHolders!!.size - 1]!!.index),
                IPlaceholderSpan::class.java
            )
        } else {
            spans = value.getSpans(
                0,
                Math.min(value.length, formatStyle!!.length),
                IPlaceholderSpan::class.java
            )
            if (spans.size == formatStyle!!.length) {
                return ""
            }
        }
        if (spans.isEmpty()) {
            if (saved) {
                value.clear()
                return ""
            }
        } else {
            clearNonEmptySpans(value, spans, false)
        }
        val realText = value.toString()
        value.clear()
        return realText
    }

    private fun checkHintStyleIsRight(hintText: String?) {
        if (hintText != null) {
            this.hintText = hintText
            var indexInStyle = 0
            var indexInText = 0
            var nextCharIsText = false
            while (indexInStyle < formatStyle!!.length) {
                require(indexInText < hintText.length) { "Hint text style must be conform to formatting style" }
                val charInStyle = formatStyle!![indexInStyle]
                if (!nextCharIsText && isMaskChar(charInStyle)) {
                    require(
                        !isMismatchMask(
                            charInStyle,
                            hintText[indexInText]
                        )
                    ) { "Hint text style must be conform to formatting style" }
                    indexInText += 1
                    indexInStyle += 1
                } else if (!nextCharIsText && charInStyle == ESCAPE_CHAR) {
                    nextCharIsText = true
                    indexInStyle += 1
                } else {
                    val charInText = hintText[indexInText]
                    require(charInStyle == charInText) { "Hint text style must be conform to formatting style" }
                    nextCharIsText = false
                    indexInText += 1
                    indexInStyle += 1
                }
            }
            require(hintText.length == indexInText) { "Hint text style must be conform to formatting style" }
        }
    }

    private fun resetClearDrawableBound() {
        if (mClearDrawable != null) {
            val top = paddingTop + mDrawablePadding
            val bottom = paddingBottom + mDrawablePadding
            val width = mClearDrawable!!.intrinsicWidth
            val height = mClearDrawable!!.intrinsicHeight
            val newRight = getWidth() - mRealPaddingRight - mDrawablePadding
            val h = getHeight()
            when (mGravity) {
                GRAVITY_TOP -> mClearDrawable!!.setBounds(
                    newRight - width,
                    top,
                    newRight,
                    top + height
                )
                GRAVITY_CENTER -> {
                    val newTop = top + (h - top - bottom - height) / 2
                    mClearDrawable!!.setBounds(newRight - width, newTop, newRight, newTop + height)
                }
                GRAVITY_BOTTOM -> {
                    val newBottom = h - bottom
                    mClearDrawable!!.setBounds(
                        newRight - width, newBottom - height, newRight, newBottom
                    )
                }
                else -> {
                    val newBottom = h - bottom
                    mClearDrawable!!.setBounds(
                        newRight - width, newBottom - height, newRight, newBottom
                    )
                }
            }
        }
    }

    private fun sendBeforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        val list: List<TextWatcher>? = mWatchers
        if (list != null) {
            val size = list.size
            for (i in 0 until size) {
                list[i].beforeTextChanged(s, start, count, after)
            }
        }
    }

    private fun sendOnTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val list: List<TextWatcher>? = mWatchers
        if (list != null) {
            val size = list.size
            for (i in 0 until size) {
                list[i].onTextChanged(s, start, before, count)
            }
        }
    }

    private fun sendAfterTextChanged(s: Editable) {
        val list: List<TextWatcher>? = mWatchers
        if (list != null) {
            val size = list.size
            for (i in 0 until size) {
                list[i].afterTextChanged(s)
            }
        }
    }

    private fun <T> clearArray(array: Array<T>?) {
        if (array != null) {
            Arrays.fill(array, null)
        }
    }

    private fun formatTextWhenDelete(editable: Editable, start: Int, before: Int) {
        mIsFormatted = true
        val filter = mFilterRestoreTextChangeEvent
        super.removeTextChangedListener(mTextWatcher)
        val filters = editable.filters
        editable.filters = EMPTY_FILTERS
        var selectionStart: Int
        var selectionEnd: Int
        if (!filter) {
            selectionStart = Selection.getSelectionStart(editable)
            selectionEnd = Selection.getSelectionEnd(editable)
            editable.setSpan(SELECTION_SPAN, selectionStart, selectionEnd, Spanned.SPAN_MARK_MARK)
        }
        if (mode < MODE_MASK) {
            val deletedLast = start >= editable.length
            if (!deletedLast) {
                formatDefined(editable, start, true)
            } else {
                for (i in start downTo 1) {
                    val sub = editable[i - 1]
                    val place = findPlaceholder(i - 1)
                    if (sub == place) {
                        editable.delete(i - 1, i)
                    } else {
                        break
                    }
                }
            }
        } else {
            formatMask(editable, start, true)
        }
        if (!filter) {
            selectionStart = editable.getSpanStart(SELECTION_SPAN)
            selectionEnd = editable.getSpanEnd(SELECTION_SPAN)
            editable.removeSpan(SELECTION_SPAN)
            editable.filters = filters
            val text = text
            Selection.setSelection(text, selectionStart, selectionEnd)
        } else {
            setFilters(filters)
        }
        mIsFormatted = false
        super.addTextChangedListener(mTextWatcher)
    }

    private fun formatTextWhenAppend(editable: Editable, start: Int, before: Int, count: Int) {
        mIsFormatted = true
        val filter = mFilterRestoreTextChangeEvent
        super.removeTextChangedListener(mTextWatcher)
        val filters = editable.filters
        editable.filters = EMPTY_FILTERS
        var selectionStart: Int
        var selectionEnd: Int
        if (!filter) {
            selectionStart = Selection.getSelectionStart(editable)
            selectionEnd = Selection.getSelectionEnd(editable)
            editable.setSpan(SELECTION_SPAN, selectionStart, selectionEnd, Spanned.SPAN_MARK_MARK)
        }
        if (mode < MODE_MASK) {
            val appendedLast = start > mHolders!![mHolders!!.size - 1]!!.index
            if (!appendedLast) {
                formatDefined(editable, start, false)
            }
        } else {
            formatMask(editable, start, false)
        }
        if (!filter) {
            selectionStart = editable.getSpanStart(SELECTION_SPAN)
            selectionEnd = editable.getSpanEnd(SELECTION_SPAN)
            editable.removeSpan(SELECTION_SPAN)
            editable.filters = filters
            if (mLengthFilterDelegate != null) {
                val out = mLengthFilterDelegate!!.mFilter.filter(
                    editable, 0, editable.length, EMPTY_SPANNED, 0, 0
                )
                if (out != null) {
                    editable.delete(out.length, editable.length)
                }
            }
            Selection.setSelection(
                editable,
                Math.min(selectionStart, editable.length),
                Math.min(selectionEnd, editable.length)
            )
        } else {
            editable.filters = filters
        }
        mIsFormatted = false
        super.addTextChangedListener(mTextWatcher)
    }

    private fun findPlaceholder(index: Int): Char {
        mHolders?.apply {
            val len = this.size
            val last = mLastIndex
            val centerIndex = this[last]!!.index
            when {
                centerIndex == index -> {
                    return this[last]!!.holder
                }
                centerIndex < index -> {
                    for (i in last until len) {
                        mLastIndex = i
                        if (this[i]!!.index == index) {
                            return this[i]!!.holder
                        } else if (this[i]!!.index > index) {
                            return 0.toChar()
                        }
                    }
                }
                else -> {
                    for (i in last downTo 0) {
                        mLastIndex = i
                        if (this[i]!!.index == index) {
                            return this[i]!!.holder
                        } else if (this[i]!!.index < index) {
                            return 0.toChar()
                        }
                    }
                }
            }
        }

        return 0.toChar()
    }

    private fun formatDefined(editable: Editable, mStart: Int, deletion: Boolean) {
        var start = mStart
        start = clearPlaceholders(editable, start)
        var selectionIndex = -1
        var indexInText = start
        val maxPos = mHolders!![mHolders!!.size - 1]!!.index
        while (indexInText < maxPos) {
            if (indexInText >= editable.length) {
                break
            }
            val placeholder = findPlaceholder(indexInText)
            if (placeholder.toInt() != 0) {
                editable.insert(indexInText, placeholder.toString())
                editable.setSpan(
                    PlaceholderSpan(),
                    indexInText,
                    indexInText + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                indexInText += 1
                if (selectionIndex == -1) {
                    if (indexInText == start + 1) {
                        selectionIndex = indexInText
                    }
                } else if (indexInText == selectionIndex + 1) {
                    selectionIndex = indexInText
                }
            } else {
                indexInText += 1
            }
        }
        if (deletion && start == 0 && selectionIndex != -1) {
            editable.setSpan(
                SELECTION_SPAN, selectionIndex, selectionIndex, Spanned.SPAN_MARK_MARK
            )
        }
    }

    private fun formatMask(editable: Editable, mStart: Int, deletion: Boolean) {
        var start = mStart
        start = clearPlaceholders(editable, start)
        if (start == -1) {
            return
        }
        if (deletion
            && start == editable.length && (mode == MODE_MASK && emptyPlaceholder.toInt() == 0
                    || mode == MODE_HINT && hintText == null)
        ) {
            return
        }
        var indexInStyle = start + rangeCountEscapeChar(start)
        var indexInText = start
        var selectionIndex = -1
        var nextCharIsText = false
        val styleLength = formatStyle!!.length
        while (indexInStyle < styleLength) {
            val charInStyle = formatStyle!![indexInStyle]
            if (!nextCharIsText && isMaskChar(charInStyle)) {
                if (indexInText >= editable.length) {
                    if (mode == MODE_MASK) {
                        if (emptyPlaceholder.toInt() != 0) {
                            editable.insert(indexInText, emptyPlaceholder.toString())
                            editable.setSpan(
                                EmptyPlaceholderSpan(),
                                indexInText,
                                indexInText + 1,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            indexInText += 1
                            indexInStyle += 1
                        } else {
                            break
                        }
                    } else {
                        if (hintText == null) {
                            break
                        }
                        editable.insert(
                            indexInText, hintText!!.subSequence(indexInText, indexInText + 1)
                        )
                        editable.setSpan(
                            HintPlaceholderSpan(
                                if (hintColor == Color.TRANSPARENT) currentHintTextColor else hintColor
                            ),
                            indexInText,
                            indexInText + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        indexInText += 1
                        indexInStyle += 1
                    }
                } else if (isMismatchMask(charInStyle, editable[indexInText])) {
                    editable.delete(indexInText, indexInText + 1)
                } else {
                    indexInText += 1
                    indexInStyle += 1
                }
            } else if (!nextCharIsText && charInStyle == ESCAPE_CHAR) {
                nextCharIsText = true
                indexInStyle += 1
            } else {
                editable.insert(indexInText, charInStyle.toString())
                editable.setSpan(
                    PlaceholderSpan(),
                    indexInText,
                    indexInText + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                nextCharIsText = false
                indexInText += 1
                indexInStyle += 1
                if (selectionIndex == -1 || indexInText == selectionIndex + 1) {
                    selectionIndex = indexInText
                }
            }
        }
        if (deletion && start == 0 && selectionIndex != -1) {
            editable.setSpan(
                SELECTION_SPAN, selectionIndex, selectionIndex, Spanned.SPAN_MARK_MARK
            )
        }
    }

    private fun clearPlaceholders(editable: Editable, start: Int): Int {
        var start = start
        val spans: Array<IPlaceholderSpan?>
        val sorted: Boolean
        if (start > 0) {
            sorted = true
            mComparator.mEditable = editable
            val left: Array<IPlaceholderSpan?>
            if (mode < MODE_MASK) {
                var i: Int = start
                while (i > 0) {
                    val holder = findPlaceholder(i)
                    if (holder.toInt() == 0) {
                        break
                    }
                    i--
                }
                start = i
                left = EMPTY_SPANS
            } else {
                left = editable.getSpans(0, start, IPlaceholderSpan::class.java)
                Arrays.sort(left, mComparator)
            }
            val right: Array<IPlaceholderSpan?>
            if (start >= editable.length) {
                right = EMPTY_SPANS
            } else {
                right = editable.getSpans(start, editable.length, IPlaceholderSpan::class.java)
                Arrays.sort(right, mComparator)
            }
            mComparator.mEditable = null
            if (left.isEmpty()) {
                spans = right
            } else if (left.size == start) {
                start = 0
                spans = arrayOfNulls(left.size + right.size)
                System.arraycopy(left, 0, spans, 0, left.size)
                System.arraycopy(right, 0, spans, left.size, right.size)
            } else {
                var last = start - 1
                var index: Int = left.size - 1
                while (index >= 0) {
                    val spanStart = editable.getSpanStart(left[index])
                    if (last != spanStart) {
                        last += 1
                        index += 1
                        break
                    }
                    last = spanStart - 1
                    index--
                }
                start = last
                val leftLength = left.size - index
                if (leftLength == 0) {
                    spans = right
                } else {
                    spans = arrayOfNulls(leftLength + right.size)
                    System.arraycopy(left, index, spans, 0, leftLength)
                    System.arraycopy(right, 0, spans, leftLength, right.size)
                }
            }
        } else {
            sorted = false
            spans = editable.getSpans(0, editable.length, IPlaceholderSpan::class.java)
        }
        if (spans.size == editable.length - start) {
            editable.delete(start, editable.length)
            if (start == 0 && isNeedClearText) {
                return -1
            }
        } else if (spans.isNotEmpty()) {
            clearNonEmptySpans(editable, spans, sorted)
        }
        return start
    }

    private val isNeedClearText: Boolean
        private get() = (mode == MODE_MASK && (isShowHintWhileEmpty || emptyPlaceholder.toInt() == 0)
                || mode == MODE_HINT && (isShowHintWhileEmpty || hintText == null))

    private fun rangeCountEscapeChar(end: Int): Int {
        if (mEscapeIndexes == null) {
            return 0
        }
        var count = 0
        for (escapeIndex in mEscapeIndexes!!) {
            count += if (escapeIndex < end) {
                1
            } else {
                break
            }
        }
        return count
    }

    private fun clearNonEmptySpans(
	    editable: Editable,
	    spans: Array<IPlaceholderSpan?>,
	    sorted: Boolean
    ) {
        if (!sorted) {
            mComparator.mEditable = editable
            Arrays.sort(spans, mComparator)
            mComparator.mEditable = null
        }
        var last = spans[0]
        var current = spans[0]
        var lastStart = editable.getSpanStart(last)
        for (i in 1 until spans.size) {
            var spanStart = editable.getSpanStart(spans[i])
            if (lastStart + 1 == spanStart) {
                current = spans[i]
            } else {
                editable.delete(editable.getSpanStart(last), editable.getSpanEnd(current))
                current = spans[i]
                last = current
                spanStart = editable.getSpanStart(last)
            }
            lastStart = spanStart
        }
        editable.delete(editable.getSpanStart(last), editable.getSpanEnd(current))
    }

    private fun isMismatchMask(mask: Char, value: Char): Boolean {
        return (mask != CHARACTER_MASK && (mask != LETTER_MASK || !Character.isLetter(value))
                && (mask != DIGIT_MASK || !Character.isDigit(value))
                && (mask != DIGIT_OR_LETTER_MASK
                || !Character.isDigit(value) && !Character.isLetter(value)))
    }

    private fun isMaskChar(mask: Char): Boolean {
        return mask == DIGIT_MASK || mask == LETTER_MASK || mask == DIGIT_OR_LETTER_MASK || mask == CHARACTER_MASK
    }

    override fun onSaveInstanceState(): Parcelable? {
        val start = selectionStart
        val end = selectionEnd
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.mMode = mode
        savedState.mPlaceholder = placeholder
        savedState.mEmptyPlaceholder = emptyPlaceholder
        savedState.mMark = mark
        savedState.mPlaceholders = mPlaceholders
        savedState.mHintText = hintText
        savedState.mFormatStyle = formatStyle
        savedState.mShowHintWhileEmpty = isShowHintWhileEmpty
        savedState.mHintColor = hintColor
        savedState.mSelectionStart = start
        savedState.mSelectionEnd = end
        savedState.mRealText = getRealText(true)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        mode = state.mMode
        placeholder = state.mPlaceholder
        emptyPlaceholder = state.mEmptyPlaceholder
        mark = state.mMark
        mPlaceholders = state.mPlaceholders
        hintText = state.mHintText
        formatStyle = state.mFormatStyle
        isShowHintWhileEmpty = state.mShowHintWhileEmpty
        hintColor = state.mHintColor
        if (mode == MODE_SIMPLE) {
            parseSimplePlaceholders()
        } else if (mode == MODE_COMPLEX) {
            parseComplexPlaceholders()
        }
        if (state.mRealText != null) {
            mRestoring = true
            super.onRestoreInstanceState(state.superState)
            mRestoring = false
            mFilterRestoreTextChangeEvent = true
            setText(state.mRealText)
            mFilterRestoreTextChangeEvent = false
            val text = text
            Selection.setSelection(
                text,
                Math.min(state.mSelectionStart, text!!.length),
                Math.min(state.mSelectionEnd, text.length)
            )
        } else {
            super.onRestoreInstanceState(state.superState)
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(MODE_NONE, MODE_SIMPLE, MODE_COMPLEX, MODE_MASK, MODE_HINT)
    internal annotation class Mode
    interface OnClearClickListener {
        fun onClearClick(editText: FormattedEditText?, drawable: Drawable?): Boolean
    }

    private interface IPlaceholderSpan
    private interface IEmptyPlaceholderSpan : IPlaceholderSpan
    private class Placeholder {
        var index = 0
        var holder = 0.toChar()
    }

    private class PlaceholderSpan : IPlaceholderSpan
    private class EmptyPlaceholderSpan : IEmptyPlaceholderSpan
    private class HintPlaceholderSpan internal constructor(color: Int) : ForegroundColorSpan(color),
	    IEmptyPlaceholderSpan

    class Config private constructor() {
        var mMode: Int? = null
        var mHintColor: Int? = null
        var mMark: Char? = null
        var mPlaceholder: Char? = null
        var mEmptyPlaceholder: Char? = null
        var mShowHintWhileEmpty: Boolean? = null
        var mHintText: String? = null
        var mFormatStyle: String? = null
        fun mode(mode: Int): Config {
            mMode = mode
            return this
        }

        fun hintText(hintText: String?): Config {
            mHintText = hintText
            return this
        }

        fun mark(mark: Char?): Config {
            mMark = mark
            return this
        }

        fun placeholder(placeholder: Char?): Config {
            mPlaceholder = placeholder
            return this
        }

        fun showHintWhileEmpty(showHintWhileEmpty: Boolean): Config {
            mShowHintWhileEmpty = showHintWhileEmpty
            return this
        }

        fun formatStyle(formatStyle: String?): Config {
            mFormatStyle = formatStyle
            return this
        }

        fun hintColor(hintColor: Int): Config {
            mHintColor = hintColor
            return this
        }

        fun emptyPlaceholder(emptyPlaceholder: Char?): Config {
            mEmptyPlaceholder = emptyPlaceholder
            return this
        }

        fun config(editText: FormattedEditText) {
            editText.setConfig(this, false)
        }

        companion object {
            fun create(): Config {
                return Config()
            }
        }
    }

    private class SavedState : BaseSavedState {
        var mMode = MODE_NONE
        var mPlaceholder = 0.toChar()
        var mEmptyPlaceholder = 0.toChar()
        var mMark = 0.toChar()
        var mPlaceholders: String? = null
        var mHintText: String? = null
        var mFormatStyle: String? = null
        var mRealText: String? = null
        var mShowHintWhileEmpty = false
        var mHintColor = Color.TRANSPARENT
        var mSelectionStart = 0
        var mSelectionEnd = 0

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            mMode = `in`.readInt()
            mPlaceholder = `in`.readInt().toChar()
            mEmptyPlaceholder = `in`.readInt().toChar()
            mMark = `in`.readInt().toChar()
            mRealText = `in`.readString()
            mPlaceholders = `in`.readString()
            mHintText = `in`.readString()
            mFormatStyle = `in`.readString()
            mShowHintWhileEmpty = `in`.readInt() != 0
            mHintColor = `in`.readInt()
            mSelectionStart = `in`.readInt()
            mSelectionEnd = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mMode)
            out.writeInt(mPlaceholder.toInt())
            out.writeInt(mEmptyPlaceholder.toInt())
            out.writeInt(mMark.toInt())
            out.writeString(mRealText)
            out.writeString(mPlaceholders)
            out.writeString(mHintText)
            out.writeString(mFormatStyle)
            out.writeInt(if (mShowHintWhileEmpty) 1 else 0)
            out.writeInt(mHintColor)
            out.writeInt(mSelectionStart)
            out.writeInt(mSelectionEnd)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> =
                object : Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState? {
                        return SavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }

        override fun describeContents(): Int {
            return 0
        }

        object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    private class PlaceholderComparator : Comparator<IPlaceholderSpan?> {
        var mEditable: Editable? = null
        override fun compare(o1: IPlaceholderSpan?, o2: IPlaceholderSpan?): Int {
            val x = mEditable?.getSpanStart(o1) ?: 0
            val y = mEditable?.getSpanStart(o2) ?: 0
            return if (x < y) -1 else if (x == y) 0 else 1
        }
    }

    private inner class PlaceholderFilter : InputFilter {
        private val mFilterBuilder = StringBuilder()
        override fun filter(
            source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int
        ): CharSequence? {
            if (mRestoring) {
                return null
            }
            if (mode == MODE_SIMPLE || mode == MODE_COMPLEX) {
                if (mPlaceholders == null || mIsFormatted || source.isEmpty()) {
                    return null
                }
                mFilterBuilder.setLength(0)
                val len = source.length
                for (i in 0 until len) {
                    val sub = source[i]
                    if (mPlaceholders!!.indexOf(sub) == -1) {
                        mFilterBuilder.append(sub)
                    }
                }
                return mFilterBuilder
            }
            return null
        }
    }

    private inner class LengthFilterDelegate(val mFilter: InputFilter) : InputFilter {
        override fun filter(
            source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int
        ): CharSequence? {
            if (mRestoring) {
                return null
            }
            if (!mIsFormatted && mode >= MODE_MASK) {
                val spans = dest.getSpans(0, dest.length, IEmptyPlaceholderSpan::class.java)
                return if (spans.isEmpty()) {
                    mFilter.filter(source, start, end, dest, dstart, dend)
                } else null
            }
            return mFilter.filter(source, start, end, dest, dstart, dend)
        }

    }

    private inner class FormattedTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (mRestoring) {
                return
            }
            sendBeforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (mRestoring) {
                return
            }
            sendOnTextChanged(s, start, before, count)
            if (mode == MODE_NONE || mode < MODE_MASK && mHolders == null) {
                return
            }
            if (!mIsFormatted && s is Editable) {
                if (count == 0) {
                    formatTextWhenDelete(s, start, before)
                } else {
                    formatTextWhenAppend(s, start, before, count)
                }
            }
        }

        override fun afterTextChanged(s: Editable) {
            if (mRestoring) {
                return
            }
            sendAfterTextChanged(s)
        }
    }

    companion object {
        const val MODE_NONE = -1
        const val MODE_SIMPLE = 0
        const val MODE_COMPLEX = 1
        const val MODE_MASK = 2
        const val MODE_HINT = 3
        const val GRAVITY_TOP = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_BOTTOM = 2
        private val SELECTION_SPAN = Any()
        private val EMPTY_FILTERS = arrayOfNulls<InputFilter>(0)
        private val EMPTY_SPANS = arrayOfNulls<IPlaceholderSpan>(0)
        private val EMPTY_SPANNED: Spanned = SpannedString("")
        private const val DEFAULT_PLACE_HOLDER = ' '
        private const val DEFAULT_MARK = '*'
        private const val DIGIT_MASK = '0'
        private const val LETTER_MASK = 'A'
        private const val DIGIT_OR_LETTER_MASK = '*'
        private const val CHARACTER_MASK = '?'
        private const val ESCAPE_CHAR = '\\'
    }
}