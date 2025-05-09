package org.vaskozlov.lab3

import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener
import jakarta.servlet.annotation.WebListener
import org.vaskozlov.lab3.bean.Points
import java.lang.management.ManagementFactory
import javax.management.ObjectName

@WebListener
class AppStartupListener : ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent?) {
        super.contextInitialized(sce)
        
        try {
            val mbs = ManagementFactory.getPlatformMBeanServer()
            val objectName = ObjectName("org.vaskozlov.lab3:type=PointsMXBean")
            val bean = Points()
            mbs.registerMBean(bean, objectName)
            
            println("INITIALIZED PointsMBean")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}