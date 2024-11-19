import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import org.w3c.dom.HTMLButtonElement

val pageScope = CoroutineScope(Dispatchers.Default)

val clock = document.getElementById("clock")!!

suspend fun startClock() {
    while (true) {
        clock.innerHTML = "Текущие время ${js("new Date().toLocaleTimeString()") as String}"
        delay(1000)
    }
}

fun main() {
    pageScope.async {
        startClock()
    }
    console.log("TEST")
}