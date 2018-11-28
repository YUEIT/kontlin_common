package cn.yue.base.kotlin.test

import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.components.BasePullPageFragment
import com.alibaba.android.arouter.facade.annotation.Route
import io.reactivex.Single

/**
 * Description :
 * Created by yue on 2018/11/23
 */

@Route(path = "/app/onePage")
class OnePageFragment : BasePullPageFragment<ArticleListVO, ArticleVO>() {

    override fun getItemLayoutId(): Int {
        return R.layout.item_article_list
    }

    override fun bindItemData(holder: CommonViewHolder<ArticleVO>, position: Int, s: ArticleVO) {

    }

    override fun getRequestSingle(pageNo: Int, nt: String): Single<ArticleListVO>? {
        return ArticleRetrofit.purchaseSnsService.getArticleList("d50148f28900cdaedf9c7cd40e330210", 1, pageNo, 20)
    }
}