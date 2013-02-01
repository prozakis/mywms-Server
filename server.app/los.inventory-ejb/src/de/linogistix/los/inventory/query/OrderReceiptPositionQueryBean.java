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


import javax.ejb.Stateless;

import de.linogistix.los.inventory.model.OrderReceiptPosition;
import de.linogistix.los.query.BusinessObjectQueryBean;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class OrderReceiptPositionQueryBean extends BusinessObjectQueryBean<OrderReceiptPosition> implements OrderReceiptPositionQueryRemote{

  @Override
  public String getUniqueNameProp() {
    return "id";
  }
	
}
