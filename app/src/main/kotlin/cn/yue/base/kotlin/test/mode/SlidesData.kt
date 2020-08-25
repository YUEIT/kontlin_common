package cn.yue.base.kotlin.test.mode

/**
 * Description :
 * Created by yue on 2020/8/24
 */
data class SlidesData(
    val slideshow: Slideshow
)

data class Slideshow(
    val author: String,
    val date: String,
    val slides: List<Slide>,
    val title: String
)

data class Slide(
    val items: List<String>,
    val title: String,
    val type: String
)