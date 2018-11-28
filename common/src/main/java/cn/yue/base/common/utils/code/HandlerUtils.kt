package cn.yue.base.common.utils.code

import android.os.Handler
import android.os.Message

import java.lang.ref.WeakReference

/**
 * 介绍：Handler相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class HandlerUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 使用必读：推荐在Activity或者Activity内部持有类中实现该接口，不要使用匿名类，可能会被GC
     *
     * @param listener 收到消息回调接口
     */
    class HandlerHolder(listener: OnReceiveMessageListener) : Handler() {
        private var mListenerWeakReference: WeakReference<OnReceiveMessageListener>? = null

        init {
            mListenerWeakReference = WeakReference(listener)
        }

        override fun handleMessage(msg: Message) {
            if (mListenerWeakReference != null && mListenerWeakReference!!.get() != null) {
                mListenerWeakReference!!.get()!!.handlerMessage(msg)
            }
        }
    }

    /**
     * 收到消息回调接口
     */
    interface OnReceiveMessageListener {
        fun handlerMessage(msg: Message)
    }
}
