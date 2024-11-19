package org.vaskozlov.lab3.bean

import jakarta.enterprise.context.ApplicationScoped
import jakarta.faces.context.FacesContext
import jakarta.inject.Named
import org.vaskozlov.lab3.database
import java.io.Serializable

@Named
@ApplicationScoped
class ResultTable : Serializable {
    var results: MutableList<CheckData> = database.loadAllCheckResults()
        .map {
            CheckData(
                it.getLong("ID"),
                it.getString("HTTP_SESSION_ID"),
                it.getDouble("X"),
                it.getDouble("Y"),
                it.getDouble("R"),
                it.getBoolean("IS_IN_AREA"),
                it.getLong("EXECUTION_TIME_NS")
            )
        }
        .toMutableList()
}

fun getCurrentResultTable(): ResultTable {
    val facesContext = FacesContext.getCurrentInstance()
    return facesContext.application.evaluateExpressionGet(facesContext, "#{resultTable}", ResultTable::class.java)
}

