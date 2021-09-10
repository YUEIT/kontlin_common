package cn.yue.base.common.widget.keyboard

import android.content.Context
import android.widget.ImageView

import cn.yue.base.common.R
import cn.yue.base.common.image.ImageLoader
import cn.yue.base.common.widget.keyboard.mode.EmotionUtils
import cn.yue.base.common.widget.keyboard.mode.IEmotion
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder

/**
 * Description :
 * Created by yue on 2018/11/15
 */
class EmotionAdapter<T : IEmotion>(context: Context, list: MutableList<T>) : CommonAdapter<T>(context, list) {

    override fun getLayoutIdByType(viewType: Int): Int {
        return R.layout.item_emotion
    }

    override fun bindData(holder: CommonViewHolder, position: Int, itemData: T) {
        val emotionItemIV = holder.getView<ImageView>(R.id.emotionItemIV)
        if (itemData.getImageResId() > 0) {
            emotionItemIV!!.setImageResource(itemData.getImageResId())
        } else {
            ImageLoader.getLoader().loadImage(emotionItemIV, itemData.getImageUrl())
        }
        holder.setOnItemClickListener {
            onEmotionClickListener?.invoke(itemData)
        }
    }

    private var onEmotionClickListener: ((itemData: IEmotion) -> Unit)? = null

    fun setOnEmotionClickListener(onEmotionClickListener: ((itemData: IEmotion) -> Unit)?) {
        this.onEmotionClickListener = onEmotionClickListener
    }
}
