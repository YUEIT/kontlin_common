package cn.yue.base.kotlin.test

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 介绍: module 中所有PHP接口
 * 作者: zhanghui
 * 邮箱: zhangh@imcoming.cn
 * 时间: 2018/7/20 下午2:47
 */
interface SnsService {
    //生意经或者品牌故事列表
    @GET("/post/publish/getPostInfo")
    fun getArticleList(@Query("token") token: String,
                       @Query("postType") postType: Int,
                       @Query("pageNo") pageNo: Int,
                       @Query("pageSize") pageSize: Int): Single<ArticleListVO>

}
