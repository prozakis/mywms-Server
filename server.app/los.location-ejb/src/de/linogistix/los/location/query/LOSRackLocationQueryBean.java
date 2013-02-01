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

package de.linogistix.los.location.query;

import javax.ejb.Stateless;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.location.model.LOSRackLocation;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSRackLocationQueryBean 
        extends BusinessObjectQueryBean<LOSRackLocation> 
        implements LOSRackLocationQueryRemote
{
	
	@Override
	public String getUniqueNameProp() {
		return "name";
	}
	
}