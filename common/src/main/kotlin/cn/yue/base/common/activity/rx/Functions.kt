package cn.yue.base.common.activity.rx

import io.reactivex.Completable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import java.util.concurrent.CancellationException

object Functions {
    val RESUME_FUNCTION: Function<Throwable, Boolean> = object : Function<Throwable, Boolean> {
        @Throws(Exception::class)
        override fun apply(throwable: Throwable): Boolean {
            if (throwable is OutsideLifecycleException) {
                return true
            }
            Exceptions.propagate(throwable!!)
            return false
        }
    }
    val SHOULD_COMPLETE = Predicate<Boolean> { shouldComplete -> shouldComplete }
    val CANCEL_COMPLETABLE: Function<Any, Completable> = Function<Any, Completable> {
        Completable.error(CancellationException())
    }
}
