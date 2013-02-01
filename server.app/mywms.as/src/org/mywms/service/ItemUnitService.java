/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import javax.ejb.Local;

import org.mywms.model.ItemUnit;

/**
 * This interface declares the service for the entity ItemData. For this
 * service it is save to call the <code>get(String name)</code>
 * method.
 * 
 * @see org.mywms.service.BasicService#get(String)
 * @author Olaf Krause
 * @version $Revision: 564 $ provided by $Author: mjordan $
 */
@Local
public interface ItemUnitService
    extends BasicService<ItemUnit>
{

	ItemUnit getDefault() ;
   
	ItemUnit getByName(String name);
	
}
