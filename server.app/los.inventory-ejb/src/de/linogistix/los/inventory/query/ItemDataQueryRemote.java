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

import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;

import javax.ejb.Remote;

import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Remote
public interface ItemDataQueryRemote extends BusinessObjectQueryRemote<ItemData>{ 
  
    public LOSResultList<BODTO<ItemData>> autoCompletionClientAndLot(String exp, 
	BODTO<Client> client, 
	BODTO<Lot> lot, 
	QueryDetail detail);

}
