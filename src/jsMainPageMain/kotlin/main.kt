import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.get

fun adjustTableRows() {
    val table = document.getElementById("resultTable") as HTMLTableElement
    val rowHeight = table.rows[0]?.clientHeight!!
    val maxTableHeight = window.innerHeight * 0.8
    val maxRows = (maxTableHeight / rowHeight).toInt()
    
    table.setAttribute("rows", maxRows.toString())
}

fun main() {
    window.addEventListener("load", { adjustTableRows() })
    window.addEventListener("resize", { adjustTableRows() })
}
