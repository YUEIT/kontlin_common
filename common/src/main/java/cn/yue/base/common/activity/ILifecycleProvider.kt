package cn.yue.base.common.activity

import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.SingleTransformer

/**
 * Description :
 * Created by yue on 2019/6/17
 */
interface ILifecycleProvider<E> : LifecycleProvider<E> {

    fun <T> toBindLifecycle() : SingleTransformer<T, T>

    fun <T> toBindLifecycle(e : E) : SingleTransformer<T, T>
}