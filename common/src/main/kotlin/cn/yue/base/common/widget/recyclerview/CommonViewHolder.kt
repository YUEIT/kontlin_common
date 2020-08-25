package cn.yue.base.common.widget.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommonViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun <V : View> getView(id: Int): V? {
        return itemView.findViewById<V>(id)
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
        }
    }

    fun setOnItemLongClickListener(onItemLongClickListener: (() -> Unit)?) {
        if (onItemLongClickListener != null) {
            itemView.setOnLongClickListener {
                onItemLongClickListener()
                true
            }
        }
    }

    companion object {

        fun <T> getHolder(context: Context, id: Int, root: ViewGroup): CommonViewHolder<T> {
            val itemView = LayoutInflater.from(context).inflate(id, root, false)
            return CommonViewHolder<T>(itemView)
        }
    }

}
