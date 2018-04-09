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
//        val test = "google".takeUnless { it.length < 10 }
//        System.out.print(test)
        val  test :String ?= null
        System.out.print(test?.removePrefix("【")?.removeSuffix("】"))
    }


    fun test1(){
        assertEquals(4, 2 + 2)
        val v :String ?= null
        System.out.print(v?.length)
        val a :ArrayList<String> = ArrayList()
        val b :ArrayList<String> = ArrayList()

        val c = (a == b)
        print(c)
        val d = A()
        val e = A()
        val f = (d == e )
        print(f)
    }
    class A{

    }
}
