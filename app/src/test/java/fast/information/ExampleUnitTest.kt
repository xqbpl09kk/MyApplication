package fast.information

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun additionCorrect() {
        assertEquals(4, 2 + 2)
        val v :String ?= null
        System.out.print(v?.length)
    }
}
