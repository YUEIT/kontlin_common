package cn.yue.test

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.activity.launch
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
//        XposedNative.auth()
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
    
    private val launcher = registerResultLauncher {
    
    }

    private fun initItem(): MutableList<ItemAction> {
        val list = ArrayList<ItemAction>()
        list.add("Test Camera" to {
            FRouter.instance.build("/app/testCamera").navigation(this)
        })
        list.add("Hint" to {
            launcher.launch("/app/testHint")
        })
        list.add("Pull" to {
            FRouter.instance.build("/app/testPull").withString("test", "hehe").navigation(this)
        })
        list.add("Page" to {
            FRouter.instance.build("/app/testPage").navigation(this)
        })
        list.add("Pull ViewModel" to {
            FRouter.instance.build("/app/testPullVM").navigation(this)
        })
        list.add("Page ViewModel" to {
            FRouter.instance.build("/app/testPageVM").navigation(this)
        })
        list.add("Select Photo" to {
            FRouter.instance.build("/app/testPhoto")
                .navigation(this)
        })
        list.add("View Photo" to {
            FRouter.instance.build("/common/viewPhoto")
                    .withStringArrayList("urls", arrayListOf("http://daidaigoucn.oss-cn-shanghai.aliyuncs.com/static/images/shop/sd1.png"))
                    .navigation(this)
        })
        list.add("Download" to {
            FRouter.instance.build("/app/testDownload").navigation(this)
        })
        list.add("widget" to {
            FRouter.instance.build("/app/testWidget").navigation(this)
        })
        list.add("web" to {
            FRouter.instance.build("/app/testWeb").navigation(this)
        })
        list.add("nested" to {
            FRouter.instance.build("/app/testNested").navigation(this)
        })
        list.add("tab layout" to {
            FRouter.instance.build("/app/testTab").navigation(this)
        })
        return list
    }

    class ItemAction (val name: String, val block: () -> Unit)
    
    private infix fun String.to(block: () -> Unit): ItemAction = ItemAction(this, block)
}

