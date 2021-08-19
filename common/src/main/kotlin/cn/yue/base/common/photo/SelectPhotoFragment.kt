package cn.yue.base.common.photo

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.image.ImageLoader.Companion.getLoader
import cn.yue.base.common.photo.PhotoUtils.getPhotosByFolder
import cn.yue.base.common.photo.PhotoUtils.getTheLastPhotos
import cn.yue.base.common.photo.data.MediaVO
import cn.yue.base.common.photo.data.MediaVO.CREATOR.equals
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.debug.LogUtils
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import java.util.*
import java.util.concurrent.Executors

/**
 * Description :
 * Created by yue on 2019/3/11
 */
class SelectPhotoFragment : BaseFragment() {
    private val pageCount = 50
    private var adapter: CommonAdapter<MediaVO>? = null
    private val photoList: MutableList<MediaVO> = ArrayList()
    private var page = 0
    private var isCanLoadMore = true
    override fun initTopBar(topBar: TopBar) {}

    override fun getLayoutId(): Int {
        return R.layout.fragment_select_photo
    }

    override fun initView(savedInstanceState: Bundle?) {
        val photoRV = findViewById<RecyclerView>(R.id.photoRV)
        photoRV.layoutManager = GridLayoutManager(mActivity, 4)
        photoRV.adapter = object : CommonAdapter<MediaVO>(mActivity, photoList) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_select_photo
            }

            override fun bindData(holder: CommonViewHolder, position: Int, mediaVO: MediaVO) {
                val photoIV = holder.getView<ImageView>(R.id.photoIV)
                val checkIV = holder.getView<CheckBox>(R.id.checkIV)
                photoIV!!.setBackgroundColor(Color.parseColor("#ffffff"))
                getLoader().loadImage(photoIV, mediaVO.uri)
                photoIV.setOnClickListener(View.OnClickListener {
                    if (getSelectList().size >= getMaxNum() && !checkIV!!.isChecked) {
                        return@OnClickListener
                    }
                    checkIV!!.isChecked = !checkIV.isChecked
                    addSelectList(mediaVO, checkIV.isChecked)
                    topBar.setRightTextStr(if (getSelectList().isEmpty()) "取消" else "确定（" + getSelectList().size + "/" + getMaxNum() + "）")
                })
                checkIV!!.isChecked = contains(mediaVO)
            }
        }.also { adapter = it }
        photoRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                LogUtils.e("" + layoutManager!!.findLastVisibleItemPosition())
                if (photoList.size - 5 <= layoutManager.findLastVisibleItemPosition() && isCanLoadMore) {
                    isCanLoadMore = false
                    getPhotoList()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        getPhotoList()
    }

    fun refresh(folderId: String?) {
        this.folderId = folderId
        allMedia = null
        adapter!!.clear()
        page = 0
        photoList.clear()
        getPhotoList()
    }

    private var folderId: String? = null
    private fun getPhotoList() {
        RunTimePermissionUtil.requestPermissions(mActivity, {
                val threadPoolUtils = Executors.newSingleThreadExecutor()
                threadPoolUtils.execute(Runnable {
                    if (allMedia == null) {
                        allMedia = when {
                            TextUtils.isEmpty(folderId) -> {
                                getTheLastPhotos(mActivity, 100)
                            }
                            folderId!!.toInt() == -1 -> {
                                getPhotosByFolder(mActivity, true, folderId)
                            }
                            else -> {
                                getPhotosByFolder(mActivity, false, folderId)
                            }
                        }
                    }
                    if (allMedia == null) {
                        allMedia = ArrayList()
                    }
                    val fromIndex = page * pageCount
                    if (fromIndex >= allMedia!!.size) {
                        handler.sendMessage(Message.obtain(handler, 101, null))
                        return@Runnable
                    }
                    var toIndex = (page + 1) * pageCount
                    if (toIndex > allMedia!!.size) {
                        toIndex = allMedia!!.size
                    }
                    val list = allMedia!!.subList(fromIndex, toIndex)
                    handler.sendMessage(Message.obtain(handler, 101, list))
                    page++
            })
        }, {}, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private var allMedia: List<MediaVO>? = null
    private var handler = Handler(Handler.Callback { msg ->
        if (msg.what == 101) {
            val addList = msg.obj
            if (addList is List<*>?) {
                if (addList == null || addList.isEmpty()) {
                    isCanLoadMore = false
                } else {
                    isCanLoadMore = true
                    photoList.addAll(addList as List<MediaVO>)
                    adapter!!.setList(photoList)
                }
            }
        }
        false
    })

    private fun getSelectList(): MutableList<MediaVO> {
        return (mActivity as SelectPhotoActivity).getPhotoList()
    }

    private fun getMaxNum(): Int {
        val num = (mActivity as SelectPhotoActivity).maxNum
        return if ( num <= 0) 1 else num
    }

    private fun addSelectList(mediaVO: MediaVO, checked: Boolean) {
        if (checked) {
            for (mediaVO1 in getSelectList()) {
                if (equals(mediaVO, mediaVO1)) {
                    return
                }
            }
            getSelectList().add(mediaVO)
        } else {
            val iterator: MutableIterator<*> = getSelectList().iterator()
            while (iterator.hasNext()) {
                val mediaVO1 = iterator.next() as MediaVO
                if (equals(mediaVO, mediaVO1)) {
                    iterator.remove()
                    return
                }
            }
        }
    }

    private operator fun contains(mediaVO: MediaVO): Boolean {
        for (mediaVO1 in getSelectList()) {
            if (equals(mediaVO, mediaVO1)) {
                return true
            }
        }
        return false
    }
}