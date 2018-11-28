package cn.yue.base.common.utils.constant

import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_DATE
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_EMAIL
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_ID_CARD15
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_ID_CARD18
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_IP
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_MOBILE_EXACT
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_MOBILE_SIMPLE
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_TEL
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_URL
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_USERNAME
import cn.yue.base.common.utils.constant.ConstantUtils.Companion.REGEX_ZH
import java.util.*
import java.util.regex.Pattern


/**
 * 介绍：正则相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object RegexUtils {

    /**
     * If u want more please visit http://toutiao.com/i6231678548520731137/
     */

    /**
     * 验证手机号（简单）
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isMobileSimple(input: CharSequence): Boolean {
        return isMatch(REGEX_MOBILE_SIMPLE, input)
    }

    /**
     * 验证手机号（精确）
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isMobileExact(input: CharSequence): Boolean {
        return isMatch(REGEX_MOBILE_EXACT, input)
    }

    /**
     * 验证电话号码
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isTel(input: CharSequence): Boolean {
        return isMatch(REGEX_TEL, input)
    }

    /**
     * 验证身份证号码15位
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isIDCard15(input: CharSequence): Boolean {
        return isMatch(REGEX_ID_CARD15, input)
    }

    /**
     * 验证身份证号码18位
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isIDCard18(input: CharSequence): Boolean {
        return isMatch(REGEX_ID_CARD18, input)
    }

    /**
     * 验证邮箱
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isEmail(input: CharSequence): Boolean {
        return isMatch(REGEX_EMAIL, input)
    }

    /**
     * 验证URL
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isURL(input: CharSequence): Boolean {
        return isMatch(REGEX_URL, input)
    }

    /**
     * 验证汉字
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isZh(input: CharSequence): Boolean {
        return isMatch(REGEX_ZH, input)
    }

    /**
     * 验证用户名
     *
     * 取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是6-20位
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isUsername(input: CharSequence): Boolean {
        return isMatch(REGEX_USERNAME, input)
    }

    /**
     * 验证yyyy-MM-dd格式的日期校验，已考虑平闰年
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isDate(input: CharSequence): Boolean {
        return isMatch(REGEX_DATE, input)
    }

    /**
     * 验证IP地址
     *
     * @param input 待验证文本
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isIP(input: CharSequence): Boolean {
        return isMatch(REGEX_IP, input)
    }

    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return `true`: 匹配<br></br>`false`: 不匹配
     */
    @JvmStatic
    fun isMatch(regex: String, input: CharSequence?): Boolean {
        return input != null && input.length > 0 && Pattern.matches(regex, input)
    }

    /**
     * 获取正则匹配的部分
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return 正则匹配的部分
     */
    @JvmStatic
    fun getMatches(regex: String, input: CharSequence?): List<String>? {
        if (input == null) return null
        val matches = ArrayList<String>()
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(input)
        while (matcher.find()) {
            matches.add(matcher.group())
        }
        return matches
    }

    /**
     * 获取正则匹配分组
     *
     * @param input 要分组的字符串
     * @param regex 正则表达式
     * @return 正则匹配分组
     */
    @JvmStatic
    fun getSplits(input: String?, regex: String): Array<String>? {
        return input?.split(regex.toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
    }

    /**
     * 替换正则匹配的第一部分
     *
     * @param input       要替换的字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替换正则匹配的第一部分
     */
    @JvmStatic
    fun getReplaceFirst(input: String?, regex: String, replacement: String): String? {
        return if (input == null) null else Pattern.compile(regex).matcher(input).replaceFirst(replacement)
    }

    /**
     * 替换所有正则匹配的部分
     *
     * @param input       要替换的字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替换所有正则匹配的部分
     */
    @JvmStatic
    fun getReplaceAll(input: String?, regex: String, replacement: String): String? {
        return if (input == null) null else Pattern.compile(regex).matcher(input).replaceAll(replacement)
    }

}