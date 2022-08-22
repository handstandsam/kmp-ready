package kmp4free.samples

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JvmLibTest {
    @Test
    fun test() {
        assertThat(JvmLib.list.size).isEqualTo(2)
        assertThat(JvmLib.className).isEqualTo(JvmLib::class.java.simpleName)
    }
}