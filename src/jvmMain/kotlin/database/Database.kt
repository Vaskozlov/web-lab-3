package org.vaskozlov.lab3.database

import org.vaskozlov.lab3.bean.CheckData
import java.sql.ResultSet

class Database(private val driver: DatabaseDriver) : AutoCloseable {
    companion object {
        private fun constructCheckData(resultSet: ResultSet) =
            CheckData(
                resultSet.getLong("ID"),
                resultSet.getString("HTTP_SESSION_ID"),
                resultSet.getDouble("X"),
                resultSet.getDouble("Y"),
                resultSet.getDouble("R"),
                resultSet.getBoolean("IS_IN_AREA"),
                resultSet.getLong("EXECUTION_TIME_NS")
            )
    }
    
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
    
    private val loadCheckDataQuery = "SELECT * FROM CHECKS;"
    
    private val clearTableQuery = "TRUNCATE TABLE CHECKS;"
    
    fun saveCheckResult(checkData: CheckData) {
        val statement = driver.prepareStatement(
            savePointCheckDataQuery, listOf(
                checkData.httpSessionId,
                checkData.x,
                checkData.y,
                checkData.r,
                checkData.inArea,
                checkData.executionTimeNs
            )
        )
        
        driver.runInTransaction{
            checkData.id = driver.executeQuery(statement).first().getLong("ID")
        }
    }
    
    fun loadAllCheckResults() =
        driver.runInTransaction {
            it.executeQuery(loadCheckDataQuery)
        }
            ?.map(::constructCheckData)
            ?.toMutableList() ?: mutableListOf()
    
    fun clearTable() = driver.executeUpdate(clearTableQuery)
    
    override fun close() = driver.close()
}