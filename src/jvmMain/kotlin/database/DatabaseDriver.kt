package org.vaskozlov.lab3.database

import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

class DatabaseDriver(
    private val url: String,
    private val login: String?,
    private val password: String?
) : AutoCloseable {
    private var threadLocalConnection = ThreadLocal<Connection>()
    
    init {
        DriverManager.registerDriver(org.postgresql.Driver())
    }
    
    private fun getConnection(): Connection {
        var connection = threadLocalConnection.get();
        
        if (connection == null) {
            connection = DriverManager.getConnection(url, login, password)
            threadLocalConnection.set(connection)
        }
        
        return connection
    }
    
    private fun beginTransaction() {
        getConnection().autoCommit = false
    }
    
    private fun commitTransaction() {
        getConnection().commit()
        getConnection().autoCommit = true
    }
    
    private fun rollbackTransaction() {
        getConnection().rollback()
        getConnection().autoCommit = true
    }
    
    fun prepareStatement(
        @Language("SQL") query: String,
        arguments: List<Any?> = emptyList()
    ): PreparedStatement {
        val statement = getConnection().prepareStatement(query)
        
        for (i in arguments.indices) {
            statement.setObject(i + 1, arguments[i])
        }
        
        return statement
    }
    
    fun executeQuery(
        @Language("SQL") query: String,
        arguments: List<Any?> = emptyList()
    ) = executeQuery(prepareStatement(query, arguments))
    
    fun executeQuery(statement: PreparedStatement) = sequence {
        statement.use {
            val result = it.executeQuery()
            
            while (result.next()) {
                yield(result)
            }
        }
    }
    
    fun executeUpdate(
        @Language("SQL") query: String,
        arguments: List<Any?> = emptyList<Any>()
    ): Int = executeUpdate(prepareStatement(query, arguments))
    
    fun executeUpdate(statement: PreparedStatement): Int = statement.executeUpdate()
    
    fun <T> runInTransaction(block: (DatabaseDriver) -> T): T {
        beginTransaction()
        
        try {
            val result = block(this)
            commitTransaction()
            return result
        } catch (e: Exception) {
            rollbackTransaction()
            throw e
        }
    }
    
    override fun close() {
        getConnection().close()
        threadLocalConnection.remove()
    }
}