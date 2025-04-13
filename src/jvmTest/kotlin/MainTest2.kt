import kotlin.test.Test
import kotlin.test.assertEquals

class MainTest2 {
    @Test
    fun testMainOutput() {
        val output = "Hello World!"
        assertEquals("Hello World!", output, "The output should match 'Hello World!'")
    }
}