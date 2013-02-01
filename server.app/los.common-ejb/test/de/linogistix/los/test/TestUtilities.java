/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mywms.ejb.BeanLocator;

/**
 *
 * @author trautm
 */
public class TestUtilities {
        private static final Logger logger = Logger.getLogger(TestUtilities.class);    

public static BeanLocator beanLocator;
    static{
        try{
         	
            File file = new File(".");
            logger.info("search path for properties files: " + file.getCanonicalFile());
            
            String pathToConfigDir = "../../config/server.app";
            
            InputStream is;
            String res = pathToConfigDir+"/jndi.properties";
            logger.info("+++ read from " + res);
            is = new FileInputStream(res);
            Properties jndi = new Properties();
            jndi.load(is);

            res = pathToConfigDir+"/appserver.properties";
            logger.info("+++ read from " + res);
            is = new FileInputStream(res);
            Properties apps = new Properties();
            apps.load(is);
            beanLocator = new BeanLocator("admin", "admin", jndi, apps);
        } catch(Exception ex){
            logger.error(ex,ex);
            beanLocator = null;
        }
    }
}
