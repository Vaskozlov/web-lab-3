package org.vaskozlov.lab3

import org.vaskozlov.lab3.database.Database
import org.vaskozlov.lab3.database.DatabaseDriver

val databaseDriver = run {
    val serverURL = System.getenv("SERVER_URL") ?: "jdbc:postgresql://localhost:5432/postgres"
    val serverLogin = System.getenv("SERVER_LOGIN")
    val serverPassword = System.getenv("SERVER_PASSWORD")
    println("url: $serverURL, login: $serverLogin, password: $serverPassword")
    DatabaseDriver(serverURL, serverLogin, serverPassword)
}

val database = Database(databaseDriver)
