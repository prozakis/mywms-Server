/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.query;

import de.linogistix.los.inventory.model.LOSInventory;
import de.linogistix.los.query.BusinessObjectQueryBean;

import javax.ejb.Stateless;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSInventoryQueryBean extends BusinessObjectQueryBean<LOSInventory> implements LOSInventoryQueryRemote{
  
  public String getUniqueNameProp() {
    return "id";
  }

}
