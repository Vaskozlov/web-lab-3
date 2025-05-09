package org.vaskozlov.lab3.bean

class Points : PointsMXBean {
    private var pointsCount: Long = 0
    
    override fun printHello() {
        println("Hello from MBean4PointsHandler!")
    }
    
    override fun increaseCount() {
        ++pointsCount
    }
    
    override fun getCount(): Long {
        return pointsCount
    }
}