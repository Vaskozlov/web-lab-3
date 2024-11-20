package org.vaskozlov.lab3.database

import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

class DatabaseDriver(url: String, login: String?, password: String?) : AutoCloseable {
    private var connection: Connection
    
    init {
        DriverManager.registerDriver(org.postgresql.Driver())
        connection = DriverManager.getConnection(url, login, password)
    }
    
    private fun prepareStatement(
        @Language("SQL") query: String,
        arguments: List<Any?> = emptyList()
    ): PreparedStatement {
        val statement = connection.prepareStatement(query)
        
        for (i in arguments.indices) {
            statement.setObject(i + 1, arguments[i])
        }
        
        return statement
    }
    
    fun executeQuery(@Language("SQL") query: String, arguments: List<Any?> = emptyList()) = sequence {
        prepareStatement(query, arguments).use {
            val result = it.executeQuery()
            
            while (result.next()) {
                yield(result)
            }
        }
    }
    
    fun executeUpdate(@Language("SQL") query: String, arguments: List<Any?> = emptyList<Any>()): Int =
        prepareStatement(query, arguments).executeUpdate()
    
    override fun close() = connection.close()
}