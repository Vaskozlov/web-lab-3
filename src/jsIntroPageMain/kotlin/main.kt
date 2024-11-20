import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

val clock = document.getElementById("clock")!!
val pageCoroutineScope = CoroutineScope(Dispatchers.Default)

suspend fun startClock() {
    while (true) {
        clock.innerHTML = "Текущие время ${js("new Date().toLocaleTimeString()") as String}"
        delay(1000)
    }
}

fun main() {
    pageCoroutineScope.async {
        startClock()
    }
    
    console.log("Async clock started")
}