/*
 * UserCRUDRemote.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.crud;

import javax.ejb.Remote;

import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.model.LOSReplenishRequest;



/**
 * CRUD operations for User entities
 * @see  BusinessObjectCRUDRemote
 * 
 * @author trautm
 *
 */
@Remote
public interface ReplenishRequestCRUDRemote extends BusinessObjectCRUDRemote<LOSReplenishRequest>{

}
