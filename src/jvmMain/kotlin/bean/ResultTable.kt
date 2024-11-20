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
}

fun getCurrentResultTable(): ResultTable {
    val facesContext = FacesContext.getCurrentInstance()
    return facesContext.application.evaluateExpressionGet(facesContext, "#{resultTable}", ResultTable::class.java)
}

