//package cn.yue.base.kotlin.test.component;
//
//import android.app.Application;
//
//import androidx.annotation.NonNull;
//
//import java.util.concurrent.TimeUnit;
//
//import cn.yue.base.common.utils.debug.ToastUtils;
//import cn.yue.base.middle.mvvm.PullViewModel;
//import cn.yue.base.middle.net.observer.BasePullSingleObserver;
//import io.reactivex.Single;
//
//public class TestPullViewModel extends PullViewModel {
//
//    public TestPullViewModel(@NonNull Application application) {
//        super(application);
//    }
//
//    @Override
//    protected void loadData() {
//        Single.just("ssss")
//                .delay(1000, TimeUnit.MILLISECONDS)
//                .compose(this.toBindLifecycle())
//                .subscribe(new BasePullSingleObserver<String>(this) {
//                    @Override
//                    public void onNext(String s) {
//                        ToastUtils.showShortToast(s);
//                    }
//                });
//    }
//}
