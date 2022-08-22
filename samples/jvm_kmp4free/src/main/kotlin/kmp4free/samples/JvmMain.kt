package kmp4free.samples

import kotlin.reflect.KClass
object JvmMain {
    fun hello(): String {
        val kClass: KClass<JvmMain> = JvmMain::class
        return kClass.simpleName!!
    }
}