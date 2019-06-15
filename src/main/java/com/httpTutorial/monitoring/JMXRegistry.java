package com.httpTutorial.monitoring;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class JMXRegistry {
    private MBeanServer mbs = null;

    public JMXRegistry(){
        this.init();
    }

    public void init(){
        this.mbs = ManagementFactory.getPlatformMBeanServer();
    }

    public void rebinding(String name, Object mbean){
        ObjectName mbeanName = null;
        String compositeName = null;

        try{
            compositeName = name + ":type=" + mbean.getClass().getName();
            mbeanName = new ObjectName(compositeName);
        } catch (MalformedObjectNameException e){
            throw new IllegalArgumentException("The name " + compositeName + "is invalid");
        }
        try{
            if(this.mbs.isRegistered(mbeanName)){
                this.mbs.unregisterMBean(mbeanName);
            }
            this.mbs.registerMBean(mbean,mbeanName);
        } catch(InstanceAlreadyExistsException e){
            throw new IllegalStateException("mbean of " + mbean.getClass().getName()+ " already exists with name" + compositeName + e);
        } catch(InstanceNotFoundException ex ){
            throw new RuntimeException("mBean of "+ mbean.getClass().getName() + " can't be found with reason" + ex);
        } catch(MBeanRegistrationException ex){
            throw new RuntimeException("mBean of " + mbean.getClass().getName() + " can't be unregistered! with reason: " + ex );
        } catch(NotCompliantMBeanException e){
            throw new IllegalStateException("mbean of " + mbean.getClass().getName()+ " is not compliant with JMX convention with reason: "+ e);
        }
    }

}
