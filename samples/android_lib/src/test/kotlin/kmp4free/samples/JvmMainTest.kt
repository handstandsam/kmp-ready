package kmp4free.samples

import com.google.common.truth.Truth.assertThat
import kmp4free.samples.android.lib.AndroidLib
import org.junit.Test

class JvmMainTest {

    @Test
    fun hello() {
        assertThat(AndroidLib.list.size).isEqualTo(2)
    }
}