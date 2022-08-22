package kmp4free.samples

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JvmMainTest {
    @Test
    fun hello() {
        assertThat(JvmMain.hello()).isEqualTo(JvmMain::class.java.simpleName)
    }
}