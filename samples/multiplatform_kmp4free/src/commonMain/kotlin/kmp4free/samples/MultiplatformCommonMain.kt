package kmp4free.samples

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MultiplatformCommonMain {
    val scope = CoroutineScope(Dispatchers.Unconfined)

    fun hello(): String {
        return this::class.simpleName!!
    }
}