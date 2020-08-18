//package cn.yue.base.kotlin.test.component;
//
//import com.alibaba.android.arouter.facade.annotation.Route;
//
//import cn.yue.base.common.widget.recyclerview.CommonAdapter;
//import cn.yue.base.middle.mvvm.CommonVMAdapter;
//import cn.yue.base.middle.mvvm.ItemViewModel;
//import cn.yue.base.middle.mvvm.components.BasePageVMFragment;
//
//@Route(path = "/app/testPageVM")
//public class TestPageVMFragment extends BasePageVMFragment<TestPageViewModel, TestItemBean> {
//
//    @Override
//    public CommonAdapter<TestItemBean> getAdapter() {
////        return new CommonAdapter<TestItemBean>(mActivity) {
////            @Override
////            public int getLayoutIdByType(int viewType) {
////                return R.layout.item_test;
////            }
////
////            @Override
////            public void bindData(CommonViewHolder<TestItemBean> holder, int position, TestItemBean testItemBean) {
////                holder.setText(R.id.testTV, testItemBean.getName());
////            }
////        };
//        return new CommonVMAdapter<TestItemBean>(mActivity) {
//            @Override
//            public ItemViewModel initItemViewModel(TestItemBean itemBean) {
//                if (itemBean.getIndex() % 2 == 0) {
//                    return new TestItemViewModel(itemBean, viewModel);
//                } else {
//                    return new TestItemViewModel2(itemBean, viewModel);
//                }
//            }
//        };
//    }
//}
