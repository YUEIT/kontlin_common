package cn.yue.base.activity.rx


import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.exceptions.Exceptions
import io.reactivex.rxjava3.functions.Predicate
import io.reactivex.rxjava3.functions.Function
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
