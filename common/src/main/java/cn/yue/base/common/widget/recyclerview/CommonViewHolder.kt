package cn.yue.base.common.widget.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2016/12/2.
 */

class CommonViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun <V : View> getView(id: Int): V? {

        if (itemView != null) {
            val view = itemView.findViewById<View>(id)
            return view as V
        } else {
            return null
        }
    }

    fun setText(id: Int, s: String?) {
        var s = s
        val t = getView<View>(id) as TextView?
        if (null != t) {
            if (null == s) {
                s = ""
            }
            t.text = s.trim { it <= ' ' }
        }

    }

    fun setImageResource(id: Int, resId: Int) {
        if (getView<View>(id) != null) {
            (getView<View>(id) as ImageView).setImageResource(resId)
        }
    }

    fun setBackgroundDrawable(id: Int, resId: Int) {
        if (getView<View>(id) != null) {
            getView<View>(id)!!.setBackgroundResource(resId)
        }
    }

    fun setBackgroundColor(id: Int, color: Int) {
        if (getView<View>(id) != null) {
            getView<View>(id)!!.setBackgroundColor(color)
        }
    }

    fun setOnClickListener(id: Int, l: View.OnClickListener) {
        if (getView<View>(id) != null) {
            getView<View>(id)!!.setOnClickListener(l)
        }
    }

    fun setOnLongClickListener(id: Int, l: View.OnLongClickListener) {
        if (getView<View>(id) != null) {
            getView<View>(id)!!.setOnLongClickListener(l)
        }
    }

    fun setOnTouchListener(id: Int, l: View.OnTouchListener) {
        if (getView<View>(id) != null) {
            getView<View>(id)!!.setOnTouchListener(l)
        }
    }


    fun setOnItemClickListener(position: Int, t: T, listener: OnItemClickListener<T>?) {
        if (itemView != null && listener != null) {
            itemView.setOnClickListener { listener.onItemClick(position, t) }
        }
    }

    fun setOnItemLongClickListener(position: Int, t: T, listener: OnItemLongClickListener<T>?) {
        if (itemView != null && listener != null) {
            itemView.setOnLongClickListener {
                listener.onItemLongClick(position, t)
                true
            }
        }
    }


    interface OnItemClickListener<T> {
        fun onItemClick(position: Int, t: T)
    }

    interface OnItemLongClickListener<T> {
        fun onItemLongClick(position: Int, t: T)
    }

    companion object {

        fun <T> getHolder(context: Context, id: Int, root: ViewGroup): CommonViewHolder<T> {
            val itemView = LayoutInflater.from(context).inflate(id, root, false)
            return CommonViewHolder<T>(itemView)
        }
    }

}
