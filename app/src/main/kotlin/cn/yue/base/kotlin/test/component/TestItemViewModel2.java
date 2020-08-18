//package cn.yue.base.kotlin.test.component;
//
//import androidx.databinding.ObservableField;
//
//import cn.yue.base.common.binding.action.BindingAction;
//import cn.yue.base.common.binding.action.BindingCommand;
//import cn.yue.base.middle.mvvm.BaseViewModel;
//import cn.yue.base.middle.mvvm.ItemViewModel;
//import cn.yue.base.test.R;
//
//public class TestItemViewModel2 extends ItemViewModel {
//
//    public TestItemBean itemBean;
//    public TestItemViewModel2(TestItemBean itemBean, BaseViewModel parentViewModel) {
//        super(parentViewModel);
//        this.itemBean = itemBean;
//        nameField.set(itemBean.getName());
//    }
//
//    @Override
//    protected int getItemType() {
//        return 2;
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.item_test_other2;
//    }
//
//    public ObservableField<String> nameField = new ObservableField<>();
//
//    public BindingCommand<Void> onclick = new BindingCommand<Void>(new BindingAction() {
//        @Override
//        public void call() {
//            itemBean.setName("change");
//            nameField.set(itemBean.getName());
//        }
//    });
//}
