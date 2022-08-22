import kmp4free.samples.MultiplatformCommonMain

import kotlin.test.Test
import kotlin.test.assertEquals

class MultiplatformCommonMainTest {
    @Test
    fun test() {
        assertEquals(MultiplatformCommonMain().className, MultiplatformCommonMain::class.simpleName)
    }
}