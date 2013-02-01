/*
 * StorageLocationQueryRemote.java
 *
 * Created on 14. September 2006, 06:59
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.query;


import de.linogistix.los.inventory.model.LOSInventory;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import javax.ejb.Remote;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface LOSInventoryQueryRemote extends BusinessObjectQueryRemote<LOSInventory>{ 
  
}
