//package cn.yue.base.kotlin.test.component;
//
//import android.app.Application;
//
//import androidx.annotation.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.yue.base.middle.mvvm.PageViewModel;
//import cn.yue.base.middle.net.wrapper.BaseListBean;
//import io.reactivex.Single;
//
//public class TestPageViewModel extends PageViewModel<TestItemBean> {
//
//    public TestPageViewModel(@NonNull Application application) {
//        super(application);
//    }
//
//    @Override
//    protected Single<BaseListBean<TestItemBean>> getRequestSingle(String nt) {
//        BaseListBean listBean = new BaseListBean();
//        listBean.setPageSize(20);
//        listBean.setTotal(22);
//        List<TestItemBean> list = new ArrayList<>();
//        for (int i=0; i < 20; i++) {
//            TestItemBean testItemBean = new TestItemBean();
//            testItemBean.setIndex(i);
//            testItemBean.setName("this is " + i);
//            list.add(testItemBean);
//        }
//        listBean.setList(list);
//        return Single.just(listBean);
//    }
//}
