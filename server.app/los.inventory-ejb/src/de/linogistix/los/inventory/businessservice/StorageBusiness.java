/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.model.LOSStorageRequest;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 * Business Services for the Storage process (move goods/unitloads to the warehouse).
 *  
 * @author trautm
 */
@Local
public interface StorageBusiness {

    /**
     * Gets or Creates a LOSStorageRequest (Movement of a UnitLoad to a StorageLocation).
     * The returned LOSStorageRequest might not contain a destination yet if in state RAW.
     * 
     * @param ul
     *  
     * @return
     */
    LOSStorageRequest getOrCreateStorageRequest(Client c,LOSUnitLoad ul) throws FacadeException, EntityNotFoundException;

    /**
     * Gets an open LOSStorageRequest (Movement of a UnitLoad to a StorageLocation).
     * 
     * @param ul
     *  
     * @return
     */
    LOSStorageRequest getOpenStorageRequest( String unitLoadLabel ) throws FacadeException;

    /**
     * Finish LOSStorageRequest by transferring all StockUnits to another UnitLoad in the warehouse (deutsch: Zuschuetten)
     *
     * @param req
     * @param destination
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws LOSLocationException 
     * @throws FacadeException 
     */
    public void finishStorageRequest(LOSStorageRequest req, LOSUnitLoad destination) throws FacadeException, LOSLocationException, FacadeException;

    /**
     * Finish LOSStorageRequest by transferring the UnitLoad to the given StorageLocation.
     *
     * @param req
     * @param sl
     * @param force
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws de.linogistix.los.location.exception.LOSLocationException
     */
    public void finishStorageRequest(LOSStorageRequest req, LOSStorageLocation sl, boolean force) throws FacadeException, LOSLocationException;
   
    /**
     *  Assigns (reserve,...) a suitable StorageLocation to the given LOSStorageRequest.
     * 
     * @param r
     * @return
     * @throws de.linogistix.los.inventory.exception.InventoryException
     * @throws de.linogistix.los.location.exception.LOSLocationException
     */
    public LOSStorageRequest assignStorageLocation(LOSStorageRequest r) throws FacadeException, LOSLocationException;

    /**
     * Finds a suitable StorageLocation where the UnitLaod can be stored.
     * @param ul
     * @return
     */
    public List<LOSStorageLocation> suitableDestination(LOSUnitLoad ul);

    /**
     * Determines to which client a UnitLoad belongs. If the UnitLoad is assigned to another
     * CLient than SystemClient it is this one. If not, but all StockUnits on the UnitLoad
     * belong to one client it is this client. If not all StockUnits belong to one client and
     * the UnitLoad is not assigned to a client different than SystemClient, it is the SystemClient.
     * 
     * @param ul
     * @return
     */
    public Client determineClient(LOSUnitLoad ul);
    
    
	/**
	 * Cancellation of a storage request.
	 * The reservation of the designated location is released.
	 * 
	 * @param req
	 * @throws FacadeException
	 */
	public void cancelStorageRequest(LOSStorageRequest req) throws FacadeException;
	
	/**
	 * Delete a storage request.
	 * The reservation of the designated location is released.
	 * 
	 * @param req
	 * @throws FacadeException
	 */
	public void removeStorageRequest(LOSStorageRequest req) throws FacadeException;

}

