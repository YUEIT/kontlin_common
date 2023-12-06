package cn.yue.base.widget.keyboard

import android.content.Context
import android.widget.ImageView
import cn.yue.base.R
import cn.yue.base.image.ImageLoader.Companion.loadImage
import cn.yue.base.widget.keyboard.mode.IEmotion
import cn.yue.base.widget.recyclerview.CommonAdapter
import cn.yue.base.widget.recyclerview.CommonViewHolder

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
            emotionItemIV?.setImageResource(itemData.getImageResId())
        } else {
            emotionItemIV?.loadImage(itemData.getImageUrl())
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
