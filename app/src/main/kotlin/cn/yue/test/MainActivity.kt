package cn.yue.test

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.photo.data.MediaData
import cn.yue.base.router.FRouter
import cn.yue.base.widget.recyclerview.CommonAdapter
import cn.yue.base.widget.recyclerview.CommonViewHolder
import com.alibaba.android.arouter.facade.annotation.Route


/**
 * Description :
 * Created by yue on 2019/6/19
 */
@Route(path = "/app/main")
class MainActivity : BaseFragmentActivity() {

    override fun getContentViewLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()
        getTopBar().setContentVisibility(View.GONE)
        val rv = findViewById<RecyclerView>(R.id.rv)
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
        list.add(ItemAction("test camera") {
            FRouter.instance.build("/app/testCamera").navigation(this)
        })
        list.add(ItemAction("Hint") {
            FRouter.instance.build("/app/testHint").navigation(this)
        })
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
//            FRouter.instance.build("/common/selectPhoto")
//                .withBoolean("isPreview", true)
//                .navigation(this, 1)
            FRouter.instance.build("/app/testPhoto")
                .navigation(this)
        })
        list.add(ItemAction("View Photo") {
            FRouter.instance.build("/common/viewPhoto")
                    .withStringArrayList("urls", arrayListOf("http://daidaigoucn.oss-cn-shanghai.aliyuncs.com/static/images/shop/sd1.png"))
                    .navigation(this)
        })
        list.add(ItemAction("Download") {
            FRouter.instance.build("/app/testDownload").navigation(this)
        })
        list.add(ItemAction("widget") {
            FRouter.instance.build("/app/testWidget").navigation(this)
        })
        list.add(ItemAction("web") {
            FRouter.instance.build("/app/testWeb").navigation(this)
        })
        list.add(ItemAction("nested") {
            FRouter.instance.build("/app/testNested").navigation(this)
        })
        return list
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val photos = data?.getParcelableArrayListExtra<MediaData>("medias")
            FRouter.instance.build("/app/testVideo")
                    .withParcelable("uri", photos?.get(0)?.uri)
                    .navigation(this)
        }
    }

    class ItemAction (
            var name: String,
            var block: () -> Unit
    )
}

