package cn.yue.base.widget.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.image.ImageLoader.Companion.loadImage

class CommonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun <V : View> requireView(id: Int): V {
        return itemView.findViewById(id)
                ?: throw NullPointerException("no found view with $id in $this")
    }

    fun <V : View> getView(id: Int): V? {
        return itemView.findViewById(id)
    }

    fun <V: View> viewToAction(id: Int, action: ((it: V) -> Unit)) {
        val view = getView<V>(id)
        if (view != null) {
            action.invoke(view)
        }
    }

    fun setText(id: Int, s: String?) {
        var str = s
        val t = getView<TextView>(id)
        if (null != t) {
            if (null == str) {
                str = ""
            }
            t.text = str.trim { it <= ' ' }
        }
    }

    fun setVisible(id: Int, visible: Int) {
        val t = getView<View>(id)
        if (null != t) {
            t.visibility = visible
        }
    }

    fun setImageUrl(id: Int, url: String?) {
        getView<ImageView>(id)?.loadImage(url)
    }

    fun setImageResource(id: Int, resId: Int) {
        getView<ImageView>(id)?.setImageResource(resId)
    }

    fun setBackgroundDrawable(id: Int, resId: Int) {
        getView<View>(id)?.setBackgroundResource(resId)
    }

    fun setBackgroundColor(id: Int, color: Int) {
        getView<View>(id)?.setBackgroundColor(color)
    }

    fun setOnClickListener(id: Int, l: View.OnClickListener) {
        getView<View>(id)?.setOnClickListener(l)
    }

    fun setOnLongClickListener(id: Int, l: View.OnLongClickListener) {
        getView<View>(id)?.setOnLongClickListener(l)
    }

    fun setOnTouchListener(id: Int, l: View.OnTouchListener) {
        getView<View>(id)?.setOnTouchListener(l)
    }


    fun setOnItemClickListener(onItemClickListener: (() -> Unit)?) {
        if (onItemClickListener != null) {
            itemView.setOnClickListener { onItemClickListener() }
        } else {
            itemView.setOnClickListener(null)
        }
    }

    fun setOnItemLongClickListener(onItemLongClickListener: (() -> Unit)?) {
        if (onItemLongClickListener != null) {
            itemView.setOnLongClickListener {
                onItemLongClickListener()
                true
            }
        } else {
            itemView.setOnClickListener(null)
        }
    }

    companion object {

        fun getHolder(context: Context, id: Int, root: ViewGroup): CommonViewHolder {
            val itemView = LayoutInflater.from(context).inflate(id, root, false)
            return CommonViewHolder(itemView)
        }
    }

}
