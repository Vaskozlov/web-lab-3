package org.vaskozlov.lab3.database

import org.vaskozlov.lab3.bean.CheckData

class Database(private val driver: DatabaseDriver) {
    private val savePointCheckDataQuery = """
        INSERT INTO CHECKS
        (HTTP_SESSION_ID,
         X,
         Y,
         R,
         IS_IN_AREA,
         EXECUTION_TIME_NS)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING ID;
    """
    
    private val loadCheckDataQuery = "SELECT * FROM CHECKS;";
    
    fun saveCheckResult(checkData: CheckData) {
        checkData.id = driver.executeQuery(
            savePointCheckDataQuery, listOf(
                checkData.httpSessionId,
                checkData.x,
                checkData.y,
                checkData.r,
                checkData.inArea,
                checkData.executionTimeNs
            )
        ).first().getLong("ID")
    }
    
    fun loadAllCheckResults() = driver.executeQuery(
        loadCheckDataQuery,
        emptyList()
    )
    
    fun clearTable() {
        driver.executeUpdate("TRUNCATE TABLE CHECKS;", emptyList())
    }
}