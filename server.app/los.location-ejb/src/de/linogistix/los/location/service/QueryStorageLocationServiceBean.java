/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.Client;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.location.model.LOSArea;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class QueryStorageLocationServiceBean 
	implements	QueryStorageLocationService, QueryStorageLocationServiceRemote 
{

	@EJB
	private QueryClientService queryClientService;
	
	@EJB
	ContextService ctxService;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryStorageLocationService#getListByAreaType(de.linogistix.los.location.model.LOSAreaType)
	 */
	@SuppressWarnings("unchecked")
	public List<LOSStorageLocation> getListByAreaType(LOSAreaType areaType) {
		
		Client callersClient = ctxService.getCallersClient();
		
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT sl FROM "
                	+ LOSStorageLocation.class.getSimpleName()+ " sl "
                    + ", " + LOSArea.class.getSimpleName() + " a "
                	+ "WHERE a.areaType = :areaType AND sl.area.id = a.id ");
		
        if (!callersClient.isSystemClient()) {
            qstr.append("AND sl.client = :cl ");
        }
        
        qstr.append("ORDER BY sl.name");
        
		Query query = manager.createQuery(qstr.toString());

        query.setParameter("areaType", areaType);
        
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }

        return (List<LOSStorageLocation>) query.getResultList();
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryStorageLocationService#getByName(java.lang.String)
	 */
	public LOSStorageLocation getByName(String name) throws UnAuthorizedException {
		
		if(ctxService.getCallersUser() == null){
    		throw new UnAuthorizedException();
    	}
		
		Query query = manager.createQuery("SELECT o FROM "
				+ LOSStorageLocation.class.getSimpleName() + " o "
				+ "WHERE o.name=:na");

		query.setParameter("na", name);

		try {
			LOSStorageLocation sl = (LOSStorageLocation) query.getSingleResult();
			
			if(!ctxService.getCallersClient().equals(queryClientService.getSystemClient())
	        	&& !ctxService.getCallersClient().equals(sl.getClient()))
        	{
        		throw new UnAuthorizedException();
        	}
			
			return sl;
			
		} catch (NoResultException ex) {
			return null;
		}
	}

}
