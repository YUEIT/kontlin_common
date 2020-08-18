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

    override fun bindData(holder: CommonViewHolder<T>, position: Int, t: T) {
        val emotionItemIV = holder.getView<ImageView>(R.id.emotionItemIV)
        if (t.getImageResId() > 0) {
            emotionItemIV!!.setImageResource(t.getImageResId())
        } else {
            ImageLoader.getLoader().loadImage(emotionItemIV, t.getImageUrl())
        }
        emotionItemIV!!.setOnClickListener{}
        setOnItemClickListener {
            if (EmotionUtils.onEmotionClickListener != null) {
                EmotionUtils.onEmotionClickListener!!.onClick(t)
            }
        }
    }
}
