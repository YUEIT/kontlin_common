package cn.yue.test.mvp

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.activity.BaseFragment
import cn.yue.base.widget.recyclerview.CommonAdapter
import cn.yue.base.widget.recyclerview.CommonViewHolder
import cn.yue.test.R

/**
 * Description :
 * Created by yue on 2023/7/27
 */
class TestTabChildFragment : BaseFragment() {
	
	override fun getLayoutId(): Int {
		return R.layout.fragment_test_tab_child
	}
	
	override fun initView(savedInstanceState: Bundle?) {
		findViewById<RecyclerView>(R.id.recyclerview).apply {
			layoutManager = LinearLayoutManager(mActivity)
			adapter = object : CommonAdapter<String>(mActivity, initList()) {
				override fun getLayoutIdByType(viewType: Int): Int {
					return R.layout.item_test
				}
				
				override fun bindData(holder: CommonViewHolder, position: Int, itemData: String) {
					holder.setText(R.id.testTV, "${position}")
				}
			}
		}
	}
	
	fun initList(): ArrayList<String> {
		val list = arrayListOf<String>()
		for (i in 0..50) {
			list.add("${i}")
		}
		return list
	}
	
	
	
	
}