package org.vaskozlov.lab3.bean

import java.io.Serializable

interface PointsMXBean : Serializable {
    fun printHello()
    
    fun increaseCount()
    
    fun getCount(): Long
}