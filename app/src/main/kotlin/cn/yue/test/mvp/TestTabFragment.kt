package cn.yue.test.mvp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.yue.base.mvp.components.BaseHintFragment
import cn.yue.base.widget.viewpager.SampleTabStrip2
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestTabBinding
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * Description :
 * Created by yue on 2023/7/27
 */
@Route(path = "/app/testTab")
class TestTabFragment: BaseHintFragment() {
	
	override fun getContentLayoutId(): Int {
		return R.layout.fragment_test_tab
	}
	
	private lateinit var binding: FragmentTestTabBinding
	
	override fun bindLayout(inflated: View) {
		super.bindLayout(inflated)
		binding = FragmentTestTabBinding.bind(inflated)
	}
	
	override fun initView(savedInstanceState: Bundle?) {
		super.initView(savedInstanceState)
		binding.viewPager.adapter = object : FragmentStateAdapter(this),
			SampleTabStrip2.LayoutTabProvider {
			override fun getItemCount(): Int {
				return 5
			}
			
			override fun createTabView(): View {
				return View.inflate(mActivity, R.layout.item_tab, null)
			}
			
			override fun bindTabView(view: View, position: Int, selectPosition: Int) {
				view.findViewById<TextView>(R.id.testTV).apply {
					text = "$position"
					if (selectPosition == position) {
						setTextColor(Color.RED)
					} else {
						setTextColor(Color.BLACK)
					}
				}
			}
			
			override fun createFragment(position: Int): Fragment {
				return TestTabChildFragment()
			}
		}
		binding.tab.setViewPager(binding.viewPager)
//		TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
//			tab.text = "${(position + 1)}"
//		}.attach()
	}
}