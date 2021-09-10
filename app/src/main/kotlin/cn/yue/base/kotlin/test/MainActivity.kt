package cn.yue.base.kotlin.test

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import cn.yue.base.common.activity.BaseActivity
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.router.FRouter
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Description :
 * Created by yue on 2019/6/19
 */
@Route(path = "/app/main")
class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = object : CommonAdapter<ItemAction>(this, initItem()) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_activity_main
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: ItemAction) {
                holder.viewToAction<TextView>(R.id.itemTV) {
                    it.text = itemData.name
                    it.setOnClickListener {
                        itemData.block.invoke()
                    }
                }
            }
        }
    }

    private fun initItem(): MutableList<ItemAction> {
        val list = ArrayList<ItemAction>()
        list.add(ItemAction("Pull") {
            FRouter.instance.build("/app/testPull").withString("test", "hehe").navigation(this)
        })
        list.add(ItemAction("Page") {
            FRouter.instance.build("/app/testPage").navigation(this)
        })
        list.add(ItemAction("Pull ViewModel") {
            FRouter.instance.build("/app/testPullVM").navigation(this)
        })
        list.add(ItemAction("Page ViewModel") {
            FRouter.instance.build("/app/testPageVM").navigation(this)
        })
        list.add(ItemAction("Select Photo") {
            FRouter.instance.build("/common/selectPhoto").navigation(this, 1)
        })
        list.add(ItemAction("View Photo") {
            FRouter.instance.build("/common/viewPhoto")
                    .withStringArrayList("list", arrayListOf("http://daidaigoucn.oss-cn-shanghai.aliyuncs.com/static/images/shop/sd1.png"))
                    .navigation(this)
        })
        list.add(ItemAction("Download") {
            FRouter.instance.build("/app/testDownload").navigation(this)
        })
        list.add(ItemAction("login") {
            FRouter.instance.build("/app/login").navigation(this)
        })
        list.add(ItemAction("widget") {
            FRouter.instance.build("/app/testWidget").navigation(this)
        })
        return list
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val photos = data?.getParcelableArrayListExtra<Uri>("photos")
            FRouter.instance.build("/common/viewPhoto")
                    .withParcelableArrayList("uris", photos)
                    .navigation(this)
        }
    }

    class ItemAction (
            var name: String,
            var block: () -> Unit
    )
}

