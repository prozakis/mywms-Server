/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemUnit;
import org.mywms.model.StockUnit;
import org.mywms.model.Zone;

/**
 * @see org.mywms.service.ItemDataService
 * @author Olaf Krause
 * @version $Revision: 564 $ provided by $Author: mjordan $
 */
@Stateless
public class ItemDataServiceBean
    extends BasicServiceBean<ItemData>
    implements ItemDataService
{
	private static final Logger log  = Logger.getLogger(ItemDataServiceBean.class);
    
	@EJB
	ItemUnitService itemUnitService;
	
	/**
     * @see org.mywms.service.ItemDataService#getListByZone(Zone)
     */
    @SuppressWarnings("unchecked")
    public List<ItemData> getListByZone(Zone zone) {
        Query query =
            manager.createQuery("SELECT idata FROM "
                + ItemData.class.getSimpleName()
                + " idata "
                + "WHERE idata.zone=:zone "
                + "ORDER BY idata.created DESC, idata.id DESC");

        query.setParameter("zone", zone);

        return (List<ItemData>) query.getResultList();
    }

    /**
     * @see org.mywms.service.ItemDataService#create(Client, String)
     */
    public ItemData create(Client client, String number)
        throws UniqueConstraintViolatedException
    {
    	
        if (client == null || number == null) {
            throw new NullPointerException("createItemData: parameter == null");
        }
        client = manager.merge(client);

        ItemData itemData = new ItemData();
        itemData.setClient(client);
        itemData.setNumber(number);
        
        ItemUnit u = itemUnitService.getDefault();
		itemData.setHandlingUnit(u);
        
        manager.persist(itemData);

        log.info("CREATED ItemData: " + itemData.toDescriptiveString());
        
        try {
            manager.flush();
        }
        catch (PersistenceException pe) {
            throw new UniqueConstraintViolatedException(
                ServiceExceptionKey.ITEMDATA_NUMBER_NOT_UNIQUE);
        }

        return itemData;
    }

    /**
     * @see org.mywms.service.ItemDataService#getListByNameFragment(Client,
     *      String)
     */
    @SuppressWarnings("unchecked")
    public List<ItemData> getListByNameFragment(Client client, String fragment)
    {
        String lowerFrag = fragment.toLowerCase();

        StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT it FROM " + ItemData.class.getSimpleName() + " it ")
            .append("WHERE LOCATE(:frag, LOWER(it.name))>0 ");

        if (!client.isSystemClient()) {
            qstr.append("AND it.client = :client ");
        }

        qstr.append("ORDER BY it.name ASC");

        Query query = manager.createQuery(qstr.toString());
        query.setParameter("frag", lowerFrag);

        if (!client.isSystemClient()) {
            query.setParameter("client", client);
        }
        return (List<ItemData>) query.getResultList();
    }

    /**
     * @see org.mywms.service.ItemDataService#getByItemNumber(Client,
     *      java.lang.String)
     */
    public ItemData getByItemNumber(Client client, String itemNumber){
    	
        Query query =
            manager.createQuery("SELECT id FROM "
                + ItemData.class.getSimpleName()
                + " id "
                + "WHERE id.number=:itemNumber "
                + "AND id.client=:cl");

        query.setParameter("itemNumber", itemNumber);
        query.setParameter("cl", client);

        try {
            ItemData id = (ItemData) query.getSingleResult();
            return id;
        }
	        catch (NoResultException ex) {
	            return null;
	        }
    }

    /**
     * @see org.mywms.service.ItemDataService#getListSafetyStockUnderflow(org.mywms.model.Client)
     */
    @SuppressWarnings("unchecked")
    public List<ItemData> getListSafetyStockUnderflow(Client client) {
        Query query1 =
            manager.createQuery("SELECT id FROM "
                + ItemData.class.getSimpleName()
                + " id "
                + "WHERE id.id IN ( "
                + "SELECT id.id FROM "
                + ItemData.class.getSimpleName()
                + " id, "
                + StockUnit.class.getSimpleName()
                + " su "
                + "WHERE su.itemData=id "
                + "  AND id.client=:client "
                + "GROUP BY id.id, id.safetyStock "
                + "HAVING sum(su.amount-su.reservedAmount)<id.safetyStock "
                + ")");
        query1.setParameter("client", client);
        List<ItemData> result1 = (List<ItemData>) query1.getResultList();

        Query query2 =
            manager.createQuery("SELECT id FROM "
                + ItemData.class.getSimpleName()
                + " id "
                + "WHERE id NOT IN (SELECT DISTINCT su.itemData FROM "
                + StockUnit.class.getSimpleName()
                + " su ) "
                + "AND id.safetyStock>0 "
                + "AND id.client=:client ");
        query2.setParameter("client", client);
        List<ItemData> result2 = (List<ItemData>) query2.getResultList();

        ArrayList result = new ArrayList();
        for (ItemData itemData: result1) {
            result.add(itemData);
        }

        for (ItemData itemData: result2) {
            result.add(itemData);
        }

        return result;
    }
}
