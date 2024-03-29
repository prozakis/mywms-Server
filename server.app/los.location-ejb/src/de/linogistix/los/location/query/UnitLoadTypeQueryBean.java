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


import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.model.UnitLoadType;

import de.linogistix.los.location.service.QueryUnitLoadTypeService;
import de.linogistix.los.query.BusinessObjectQueryBean;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class UnitLoadTypeQueryBean extends BusinessObjectQueryBean<UnitLoadType> implements UnitLoadTypeQueryRemote{

	@EJB
	QueryUnitLoadTypeService ulTypeService;
	
    @Override
    public String getUniqueNameProp() {
        return "name";
    }

	public UnitLoadType getDefaultUnitLoadType() {
		return ulTypeService.getDefaultUnitLoadType();
	}

	public UnitLoadType getPickLocationUnitLoadType() {
		return ulTypeService.getPickLocationUnitLoadType();
	}
	
}
