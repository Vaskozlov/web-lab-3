package org.vaskozlov.lab3.bean

import jakarta.enterprise.context.SessionScoped
import jakarta.faces.context.FacesContext
import jakarta.inject.Named
import org.primefaces.event.SlideEndEvent
import org.vaskozlov.lab3.core.IsInAreaService
import org.vaskozlov.lab3.database
import java.io.Serializable
import kotlin.system.measureNanoTime

@Named
@SessionScoped
class CheckData : Serializable {
    var id: Long = 0
    var httpSessionId: String = ""
    var x: Double = 0.0
    var y: Double = 0.0
    var r: Double = 1.0
    var inArea: Boolean = false
    var executionTimeNs: Long = 0
    
    constructor()
    
    constructor(
        id: Long,
        httpSessionId: String,
        x: Double,
        y: Double,
        r: Double,
        inArea: Boolean,
        executionTimeNs: Long
    ) {
        this.id = id
        this.httpSessionId = httpSessionId
        this.x = x
        this.y = y
        this.r = r
        this.inArea = inArea
        this.executionTimeNs = executionTimeNs
    }
    
    fun copy() = CheckData(id, httpSessionId, x, y, r, inArea, executionTimeNs)
    
    fun submit() {
        executionTimeNs = measureNanoTime {
            httpSessionId = FacesContext.getCurrentInstance().externalContext.getSessionId(true)
            inArea = IsInAreaService(x, y, r).isInArea()
        }
        
        database.saveCheckResult(this)
        getCurrentResultTable().results.add(this.copy())
    }
    
    fun clearTable() {
        database.clearTable()
        getCurrentResultTable().results.clear()
    }
    
    fun onXSliderChange(event: SlideEndEvent?) {
        x = event!!.value
    }
    
    fun onRSliderChange(event: SlideEndEvent?) {
        r = event!!.value
    }
}