/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UnitLoadService;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.inventory.model.LOSStorageRequestState;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * @see org.mywms.service.AreaService
 *  
 * @author Taieb El Fakiri
 * @version $Revision: 276 $ provided by $Author: trautmann $
 */
@Stateless
public class LOSStorageRequestServiceBean
        extends BasicServiceBean<LOSStorageRequest>
        implements LOSStorageRequestService {

    private static final Logger log = Logger.getLogger(LOSStorageRequestServiceBean.class);
    @EJB
    UnitLoadService ulService;
    @EJB
    ClientService clService;
    
    @SuppressWarnings("unchecked")
	public LOSStorageRequest getRawOrCreateByLabel(Client c, String label) throws InventoryException, EntityNotFoundException {
        LOSStorageRequest ret;
        LOSUnitLoad ul;
                
        ul = (LOSUnitLoad)ulService.getByLabelId(clService.getSystemClient(), label);
        
        Query query =
                manager.createQuery("SELECT req FROM " 
                + LOSStorageRequest.class.getSimpleName() 
                + " req " 
                + " WHERE req.unitLoad.labelId=:label "
                +" AND req.requestState = :rstate "
                );

        query.setParameter("label", label);
        query.setParameter("rstate", LOSStorageRequestState.RAW);
        
        List<LOSStorageRequest> list = query.getResultList();
        if (list == null || list.size() < 1){   
            ret = new LOSStorageRequest();
            ret.setUnitLoad(ul);
            ret.setNumber("");
            ret.setClient(ul.getClient());
            manager.persist(ret);
            log.info("CREATED LOSStorageRequest for " + label);
            return ret;
        } else{
            log.warn("FOUND existing LOSStorageRequest for " + label);
            return list.get(0);
        }
    }

	@SuppressWarnings("unchecked")
	public List<LOSStorageRequest> getListByLabelId(String label) {
		
		Query query = manager.createQuery("SELECT req FROM "
				+ LOSStorageRequest.class.getSimpleName() + " req "
				+ " WHERE req.unitLoad.labelId=:label ");

		query.setParameter("label", label);
		
		return query.getResultList();
	}
}
