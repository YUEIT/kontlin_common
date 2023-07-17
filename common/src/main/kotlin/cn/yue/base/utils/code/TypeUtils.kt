package cn.yue.base.utils.code

import com.google.gson.internal.`$Gson$Types`
import java.lang.reflect.Type
import java.util.*

/**
 * Description :
 * Created by yue on 2018/11/20
 */
object TypeUtils {

    fun `$List`(type: Type): Type {
        return `$Gson$Types`.newParameterizedTypeWithOwner(null, List::class.java, type)
    }

    fun `$Set`(type: Type): Type {
        return `$Gson$Types`.newParameterizedTypeWithOwner(null, Set::class.java, type)
    }

    fun `$HashMap`(type: Type, type2: Type): Type {
        return `$Gson$Types`.newParameterizedTypeWithOwner(null, HashMap::class.java, type, type2)
    }

    fun `$Map`(type: Type, type2: Type): Type {
        return `$Gson$Types`.newParameterizedTypeWithOwner(null, Map::class.java, type, type2)
    }

    fun `$Parameterized`(ownerType: Type, rawType: Type, vararg typeArguments: Type): Type {
        return `$Gson$Types`.newParameterizedTypeWithOwner(ownerType, rawType, *typeArguments)
    }

    fun `$Array`(type: Type): Type {
        return `$Gson$Types`.arrayOf(type)
    }

    fun `$SubtypeOf`(type: Type): Type {
        return `$Gson$Types`.subtypeOf(type)
    }

    fun `$SupertypeOf`(type: Type): Type {
        return `$Gson$Types`.supertypeOf(type)
    }

}

