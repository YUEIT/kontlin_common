package cn.yue.base.widget.excel

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cn.yue.base.R
import cn.yue.base.utils.app.DisplayUtils
import cn.yue.base.widget.recyclerview.CommonAdapter
import cn.yue.base.widget.recyclerview.CommonViewHolder

/**
 * Description :
 * Created by yue on 2020/11/21
 */
class ExcelPanel(context: Context, attributeSet: AttributeSet): FrameLayout(context, attributeSet) {

    private val leftWidth: Int
    private val topHeight: Int
    private val leftRV: InnerRecyclerView
    private val topHSV: InnerHorizontalScrollView
    private val topRV: InnerRecyclerView
    private val contentRV: InnerRecyclerView
    private val contentHSV: InnerHorizontalScrollView

    init {
        val a = getContext().theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.ExcelPanel,
                0, 0)
        try {
            leftWidth = a.getDimension(R.styleable.ExcelPanel_left_width, DisplayUtils.dip2px(70f).toFloat()).toInt()
            topHeight = a.getDimension(R.styleable.ExcelPanel_top_height, DisplayUtils.dip2px(70f).toFloat()).toInt()
        } finally {
            a.recycle()
        }
        View.inflate(context, R.layout.layout_excel_panel, this)
        leftRV = findViewById(R.id.leftRV)
        topHSV = findViewById(R.id.topHSV)
        topHSV.scrollBarStyle
        topRV = findViewById(R.id.topRV)
        contentHSV = findViewById(R.id.contentHSV)
        contentRV = findViewById(R.id.contentRV)
        (leftRV.layoutParams as LayoutParams).topMargin = topHeight
        (topHSV.layoutParams as LayoutParams).marginStart = leftWidth
        (contentHSV.layoutParams as LayoutParams).topMargin = topHeight
        (contentHSV.layoutParams as LayoutParams).marginStart = leftWidth
        contentRV.setOnScrollListener { _, dx, dy ->
            leftRV.fastScrollBy(0, dy)
        }
        contentHSV.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            topHSV.fastScrollTo(scrollX, 0)
        }
        leftRV.setOnScrollListener { _, dx, dy ->
            contentRV.fastScrollBy(0, dy)
        }
        topHSV.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            contentHSV.fastScrollTo(scrollX, 0)
        }
    }

    fun <L, T, C>setAdapter(adapter: Adapter<L, T, C>) {
        leftRV.layoutManager = LinearLayoutManager(context)
        leftRV.adapter = adapter.leftAdapter
        topRV.layoutManager = GridLayoutManager(context, adapter.getTopCount())
        topRV.adapter = adapter.topAdapter
        contentRV.layoutManager = GridLayoutManager(context, adapter.getTopCount())
        contentRV.adapter = adapter.contentAdapter
    }

    companion object {
        const val CONTENT = 0
        const val TOP = 1
        const val LEFT = 2
    }

    abstract class Adapter<L, T, C>(context: Context) {

        val leftAdapter: CommonAdapter<L>
        val topAdapter: CommonAdapter<T>
        val contentAdapter: CommonAdapter<C>

        init {
            leftAdapter = object : CommonAdapter<L>(context) {
                override fun getLayoutIdByType(viewType: Int): Int {
                    return getLayoutIdByExcel(LEFT)
                }

                override fun bindData(holder: CommonViewHolder, position: Int, itemData: L) {
                    bindLeftData(holder, position, itemData)
                }
            }
            topAdapter = object : CommonAdapter<T>(context) {
                override fun getLayoutIdByType(viewType: Int): Int {
                    return getLayoutIdByExcel(TOP)
                }

                override fun bindData(holder: CommonViewHolder, position: Int, itemData: T) {
                    bindTopData(holder, position, itemData)
                }
            }
            contentAdapter = object : CommonAdapter<C>(context) {
                override fun getLayoutIdByType(viewType: Int): Int {
                    return getLayoutIdByExcel(CONTENT)
                }

                override fun bindData(holder: CommonViewHolder, position: Int, itemData: C) {
                    val horizontalPosition = position % getTopCount()
                    val verticalPosition = position / getTopCount()
                    bindContentData(holder, horizontalPosition, verticalPosition, itemData)
                }
            }
        }

        abstract fun getTopCount(): Int

        abstract fun getLayoutIdByExcel(viewType: Int): Int

        abstract fun bindLeftData(holder: CommonViewHolder, position: Int, itemData: L)

        abstract fun bindTopData(holder: CommonViewHolder, position: Int, itemData: T)

        abstract fun bindContentData(holder: CommonViewHolder, horizontalPosition: Int, verticalPosition: Int, itemData: C)

        fun setAllData(leftList: List<L>, topList: List<T>, contentList: List<C>) {
            leftAdapter.setList(leftList)
            topAdapter.setList(topList)
            contentAdapter.setList(contentList)
        }

        fun getLeftData(): List<L> {
            return leftAdapter.getData()
        }

        fun getTopData(): List<T> {
            return topAdapter.getData()
        }

        fun notifyDataSetChanged() {
            leftAdapter.notifyDataSetChanged()
            topAdapter.notifyDataSetChanged()
            contentAdapter.notifyDataSetChanged()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionIndex >= 1) return true
        return super.dispatchTouchEvent(ev)
    }

}