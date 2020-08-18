package cn.yue.base.common.utils.constant


import cn.yue.base.common.utils.constant.ConvertUtils.Companion.bytes2HexString
import cn.yue.base.common.utils.constant.ConvertUtils.Companion.hexString2Bytes
import cn.yue.base.common.utils.debug.CloseUtils
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * 介绍：加密解密相关的工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object EncryptUtils {

    /*********************** 哈希加密相关  */
    /**
     * MD2加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */

    @JvmStatic
    fun encryptMD2ToString(data: String): String? {
        return encryptMD2ToString(data.toByteArray())
    }

    /**
     * MD2加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */

    @JvmStatic
    fun encryptMD2ToString(data: ByteArray): String? {
        return bytes2HexString(encryptMD2(data))
    }

    /**
     * MD2加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */

    @JvmStatic
    fun encryptMD2(data: ByteArray): ByteArray? {
        return hashTemplate(data, "MD2")
    }

    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */

    @JvmStatic
    fun encryptMD5ToString(data: String): String? {
        return encryptMD5ToString(data.toByteArray())
    }

    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @param salt 盐
     * @return 16进制加盐密文
     */

    @JvmStatic
    fun encryptMD5ToString(data: String, salt: String): String? {
        return bytes2HexString(encryptMD5((data + salt).toByteArray()))
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */

    @JvmStatic
    fun encryptMD5ToString(data: ByteArray): String? {
        return bytes2HexString(encryptMD5(data))
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @param salt 盐字节数组
     * @return 16进制加盐密文
     */

    @JvmStatic
    fun encryptMD5ToString(data: ByteArray?, salt: ByteArray?): String? {
        if (data == null || salt == null) return null
        val dataSalt = ByteArray(data.size + salt.size)
        System.arraycopy(data, 0, dataSalt, 0, data.size)
        System.arraycopy(salt, 0, dataSalt, data.size, salt.size)
        return bytes2HexString(encryptMD5(dataSalt))
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */

    @JvmStatic
    fun encryptMD5(data: ByteArray): ByteArray? {
        return hashTemplate(data, "MD5")
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的16进制密文
     */

    @JvmStatic
    fun encryptMD5File2String(filePath: String): String? {
        val file = if (StringUtils.isSpace(filePath)) null else File(filePath)
        return encryptMD5File2String(file)
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    @JvmStatic
    fun encryptMD5File(filePath: String): ByteArray? {
        val file = if (StringUtils.isSpace(filePath)) null else File(filePath)
        return encryptMD5File(file)
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的16进制密文
     */
    @JvmStatic
    fun encryptMD5File2String(file: File?): String? {
        return bytes2HexString(encryptMD5File(file))
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    @JvmStatic
    fun encryptMD5File(file: File?): ByteArray? {
        if (file == null) return null
        var fis: FileInputStream? = null
        val digestInputStream: DigestInputStream
        try {
            fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            digestInputStream = DigestInputStream(fis, md)
            val buffer = ByteArray(256 * 1024)
            while (digestInputStream.read(buffer) > 0);
            md = digestInputStream.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(fis as Closeable)
        }
    }

    /**
     * SHA1加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA1ToString(data: String): String? {
        return encryptSHA1ToString(data.toByteArray())
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA1ToString(data: ByteArray): String? {
        return bytes2HexString(encryptSHA1(data))
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptSHA1(data: ByteArray): ByteArray? {
        return hashTemplate(data, "SHA1")
    }

    /**
     * SHA224加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA224ToString(data: String): String? {
        return encryptSHA224ToString(data.toByteArray())
    }

    /**
     * SHA224加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA224ToString(data: ByteArray): String? {
        return bytes2HexString(encryptSHA224(data))
    }

    /**
     * SHA224加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptSHA224(data: ByteArray): ByteArray? {
        return hashTemplate(data, "SHA224")
    }

    /**
     * SHA256加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA256ToString(data: String): String? {
        return encryptSHA256ToString(data.toByteArray())
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA256ToString(data: ByteArray): String? {
        return bytes2HexString(encryptSHA256(data))
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptSHA256(data: ByteArray): ByteArray? {
        return hashTemplate(data, "SHA256")
    }

    /**
     * SHA384加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA384ToString(data: String): String? {
        return encryptSHA384ToString(data.toByteArray())
    }

    /**
     * SHA384加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA384ToString(data: ByteArray): String? {
        return bytes2HexString(encryptSHA384(data))
    }

    /**
     * SHA384加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptSHA384(data: ByteArray): ByteArray? {
        return hashTemplate(data, "SHA384")
    }

    /**
     * SHA512加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA512ToString(data: String): String? {
        return encryptSHA512ToString(data.toByteArray())
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptSHA512ToString(data: ByteArray): String? {
        return bytes2HexString(encryptSHA512(data))
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptSHA512(data: ByteArray): ByteArray? {
        return hashTemplate(data, "SHA512")
    }

    /**
     * hash加密模板
     *
     * @param data      数据
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private @JvmStatic
    fun hashTemplate(data: ByteArray?, algorithm: String): ByteArray? {
        if (data == null || data.size <= 0) return null
        try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(data)
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacMD5ToString(data: String, key: String): String? {
        return encryptHmacMD5ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacMD5ToString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptHmacMD5(data, key))
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptHmacMD5(data: ByteArray, key: ByteArray): ByteArray? {
        return hmacTemplate(data, key, "HmacMD5")
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA1ToString(data: String, key: String): String? {
        return encryptHmacSHA1ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA1ToString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptHmacSHA1(data, key))
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptHmacSHA1(data: ByteArray, key: ByteArray): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA1")
    }

    /**
     * HmacSHA224加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA224ToString(data: String, key: String): String? {
        return encryptHmacSHA224ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * HmacSHA224加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA224ToString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptHmacSHA224(data, key))
    }

    /**
     * HmacSHA224加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptHmacSHA224(data: ByteArray, key: ByteArray): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA224")
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA256ToString(data: String, key: String): String? {
        return encryptHmacSHA256ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA256ToString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptHmacSHA256(data, key))
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptHmacSHA256(data: ByteArray, key: ByteArray): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA256")
    }

    /**
     * HmacSHA384加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA384ToString(data: String, key: String): String? {
        return encryptHmacSHA384ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * HmacSHA384加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA384ToString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptHmacSHA384(data, key))
    }

    /**
     * HmacSHA384加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptHmacSHA384(data: ByteArray, key: ByteArray): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA384")
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA512ToString(data: String, key: String): String? {
        return encryptHmacSHA512ToString(data.toByteArray(), key.toByteArray())
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptHmacSHA512ToString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptHmacSHA512(data, key))
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    @JvmStatic
    fun encryptHmacSHA512(data: ByteArray, key: ByteArray): ByteArray? {
        return hmacTemplate(data, key, "HmacSHA512")
    }

    /**
     * Hmac加密模板
     *
     * @param data      数据
     * @param key       秘钥
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private @JvmStatic
    fun hmacTemplate(data: ByteArray?, key: ByteArray?, algorithm: String): ByteArray? {
        if (data == null || data.size == 0 || key == null || key.size == 0) return null
        try {
            val secretKey = SecretKeySpec(key, algorithm)
            val mac = Mac.getInstance(algorithm)
            mac.init(secretKey)
            return mac.doFinal(data)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
            return null
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }

    }

    /************************ DES加密相关  */
    /**
     * DES转变
     *
     * 法算法名称/加密模式/填充方式
     *
     * 加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB
     *
     * 填充方式有：NoPadding、ZerosPadding、PKCS5Padding
     */
    var DES_Transformation = "DES/ECB/NoPadding"
    private val DES_Algorithm = "DES"

    /**
     * DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return Base64密文
     */
    @JvmStatic
    fun encryptDES2Base64(data: ByteArray, key: ByteArray): ByteArray {
        return EncodeUtils.base64Encode(encryptDES(data, key)!!)
    }

    /**
     * DES加密后转为16进制
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptDES2HexString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptDES(data, key))
    }

    /**
     * DES加密
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 密文
     */
    @JvmStatic
    fun encryptDES(data: ByteArray, key: ByteArray): ByteArray? {
        return desTemplate(data, key, DES_Algorithm, DES_Transformation, true)
    }

    /**
     * DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  8字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptBase64DES(data: ByteArray, key: ByteArray): ByteArray? {
        return decryptDES(EncodeUtils.base64Decode(data), key)
    }

    /**
     * DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  8字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptHexStringDES(data: String, key: ByteArray): ByteArray? {
        return decryptDES(hexString2Bytes(data), key)
    }

    /**
     * DES解密
     *
     * @param data 密文
     * @param key  8字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptDES(data: ByteArray?, key: ByteArray): ByteArray? {
        return desTemplate(data, key, DES_Algorithm, DES_Transformation, false)
    }

    /************************ 3DES加密相关  */
    /**
     * 3DES转变
     *
     * 法算法名称/加密模式/填充方式
     *
     * 加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB
     *
     * 填充方式有：NoPadding、ZerosPadding、PKCS5Padding
     */
    var TripleDES_Transformation = "DESede/ECB/NoPadding"
    private val TripleDES_Algorithm = "DESede"


    /**
     * 3DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return Base64密文
     */
    @JvmStatic
    fun encrypt3DES2Base64(data: ByteArray, key: ByteArray): ByteArray {
        return EncodeUtils.base64Encode(encrypt3DES(data, key)!!)
    }

    /**
     * 3DES加密后转为16进制
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encrypt3DES2HexString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encrypt3DES(data, key))
    }

    /**
     * 3DES加密
     *
     * @param data 明文
     * @param key  24字节密钥
     * @return 密文
     */
    @JvmStatic
    fun encrypt3DES(data: ByteArray, key: ByteArray): ByteArray? {
        return desTemplate(data, key, TripleDES_Algorithm, TripleDES_Transformation, true)
    }

    /**
     * 3DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  24字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptBase64_3DES(data: ByteArray, key: ByteArray): ByteArray? {
        return decrypt3DES(EncodeUtils.base64Decode(data), key)
    }

    /**
     * 3DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  24字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptHexString3DES(data: String, key: ByteArray): ByteArray? {
        return decrypt3DES(hexString2Bytes(data), key)
    }

    /**
     * 3DES解密
     *
     * @param data 密文
     * @param key  24字节密钥
     * @return 明文
     */
    @JvmStatic
    fun decrypt3DES(data: ByteArray?, key: ByteArray): ByteArray? {
        return desTemplate(data, key, TripleDES_Algorithm, TripleDES_Transformation, false)
    }

    /************************ AES加密相关  */
    /**
     * AES转变
     *
     * 法算法名称/加密模式/填充方式
     *
     * 加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB
     *
     * 填充方式有：NoPadding、ZerosPadding、PKCS5Padding
     */
    var AES_Transformation = "AES/ECB/NoPadding"
    private val AES_Algorithm = "AES"


    /**
     * AES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return Base64密文
     */
    @JvmStatic
    fun encryptAES2Base64(data: ByteArray, key: ByteArray): ByteArray {
        return EncodeUtils.base64Encode(encryptAES(data, key)!!)
    }

    /**
     * AES加密后转为16进制
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 16进制密文
     */
    @JvmStatic
    fun encryptAES2HexString(data: ByteArray, key: ByteArray): String? {
        return bytes2HexString(encryptAES(data, key))
    }

    /**
     * AES加密
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 密文
     */
    @JvmStatic
    fun encryptAES(data: ByteArray, key: ByteArray): ByteArray? {
        return desTemplate(data, key, AES_Algorithm, AES_Transformation, true)
    }

    /**
     * AES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptBase64AES(data: ByteArray, key: ByteArray): ByteArray? {
        return decryptAES(EncodeUtils.base64Decode(data), key)
    }

    /**
     * AES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptHexStringAES(data: String, key: ByteArray): ByteArray? {
        return decryptAES(hexString2Bytes(data), key)
    }

    /**
     * AES解密
     *
     * @param data 密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    @JvmStatic
    fun decryptAES(data: ByteArray?, key: ByteArray): ByteArray? {
        return desTemplate(data, key, AES_Algorithm, AES_Transformation, false)
    }

    /**
     * DES加密模板
     *
     * @param data           数据
     * @param key            秘钥
     * @param algorithm      加密算法
     * @param transformation 转变
     * @param isEncrypt      `true`: 加密 `false`: 解密
     * @return 密文或者明文，适用于DES，3DES，AES
     */
    @JvmStatic
    fun desTemplate(data: ByteArray?, key: ByteArray?, algorithm: String, transformation: String, isEncrypt: Boolean): ByteArray? {
        if (data == null || data.size == 0 || key == null || key.size == 0) return null
        try {
            val keySpec = SecretKeySpec(key, algorithm)
            val cipher = Cipher.getInstance(transformation)
            val random = SecureRandom()
            cipher.init(if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE, keySpec, random)
            return cipher.doFinal(data)
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }

    }

}