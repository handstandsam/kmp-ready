package kmp4free.samples

import kotlin.reflect.KClass
object JvmMain2 {
    fun hello(): String {
        val kClass: KClass<JvmMain2> = JvmMain2::class
        return kClass.simpleName!!
    }
}