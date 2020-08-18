//package cn.yue.base.kotlin.test.component;
//
//import android.os.Bundle;
//import android.view.View;
//
//import com.alibaba.android.arouter.facade.annotation.Route;
//
//import cn.yue.base.middle.mvvm.components.binding.BasePullVMBindFragment;
//import cn.yue.base.middle.router.RouterCard;
//import cn.yue.base.test.R;
//import cn.yue.base.test.databinding.FragmentTestPullVmBinding;
//
//@Route(path = "/app/testPullVM")
//public class TestPullVMFragment extends BasePullVMBindFragment<TestPullViewModel ,FragmentTestPullVmBinding> {
//
//    @Override
//    protected int getContentLayoutId() {
//        return R.layout.fragment_test_pull_vm;
//    }
//
//    @Override
//    protected void initView(Bundle savedInstanceState) {
//        super.initView(savedInstanceState);
//        findViewById(R.id.jump1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewModel.navigation(new RouterCard().setPath("/app/testPullPageVM"));
//            }
//        });
//        findViewById(R.id.jump2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                viewModel.finish();
//            }
//        });
//    }
//}
