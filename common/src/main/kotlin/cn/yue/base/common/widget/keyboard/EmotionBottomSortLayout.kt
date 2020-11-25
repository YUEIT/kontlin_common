package cn.yue.base.common.widget.keyboard

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.R
import cn.yue.base.common.widget.keyboard.mode.IEmotionSort
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import java.util.*

/**
 * Description :
 * Created by yue on 2018/11/16
 */
abstract class EmotionBottomSortLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var sortRV: RecyclerView? = null
    private var commonAdapter: CommonAdapter<IEmotionSort>
    private var list: MutableList<IEmotionSort> = ArrayList()
    private var currentIndex = 0

    init {
        inflate(context, R.layout.layout_emotion_bottom_sort, this)
        sortRV = findViewById(R.id.emotionSortRV)
        sortRV!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        commonAdapter = object: CommonAdapter<IEmotionSort>(context, list) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_emotion_bottom_sort
            }

            override fun bindData(holder: CommonViewHolder, position: Int, iEmotionSort: IEmotionSort) {
                if (position == currentIndex) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#efefef"))
                } else {
                    holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))
                }
                //                ImageLoader.getLoader().loadImage((ImageView) holder.getView(R.id.emotionImageIV), iEmotionSort.getIconUrl());
                val imageView = holder.getView<ImageView>(R.id.emotionImageIV)
                holder.setOnItemClickListener {
                    if (mListener != null) {
                        mListener!!(iEmotionSort)
                    }
                    currentIndex = position
                    notifyDataSetChanged()
                }
            }
        }
        sortRV!!.setAdapter(commonAdapter)
    }

    fun setEmotionSortList(list: MutableList<IEmotionSort>) {
        this.list = list
        commonAdapter.setList(list)
    }

    fun smoothScrollToPosition(position: Int) {
        currentIndex = position
        sortRV!!.smoothScrollToPosition(position)
        commonAdapter.notifyDataSetChanged()
    }

//    interface OnClickEmotionSortListener {
//        fun onClick(sort: IEmotionSort)
//    }

    var mListener: ((sort : IEmotionSort) -> Unit)? = null

    fun setOnClickEmotionSortListener(mListener: (sort : IEmotionSort) -> Unit) {
        this.mListener = mListener
    }
}
