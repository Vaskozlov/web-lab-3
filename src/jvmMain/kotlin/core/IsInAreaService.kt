package org.vaskozlov.lab3.core

import kotlin.math.sqrt

class IsInAreaService(private val x: Double, private val y: Double, private val r: Double) {
    private val halfR = r / 2.0
    
    private fun isInFirstQuarter(): Boolean {
        if (x >= 0 && y >= 0) {
            return (x <= r && y <= halfR - x / 2)
        }
        
        return false
    }
    
    private fun isInSecondQuarter(): Boolean {
        return false //NOSONAR
    }
    
    private fun isInThirdQuarter(): Boolean {
        if (x < 0 && y <= 0) {
            return sqrt(x * x + y * y) <= halfR
        }
        
        return false
    }
    
    private fun isInFourthQuarter(): Boolean {
        if (x >= 0 && y < 0) {
            return (x <= r && -y <= halfR)
        }
        
        return false
    }
    
    fun isInArea(): Boolean {
        return isInFirstQuarter()
                || isInSecondQuarter()
                || isInThirdQuarter()
                || isInFourthQuarter()
    }
}