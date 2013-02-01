/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;

/**
 * Facade for managing {@link ItemData}, {@link LOSAdvice} and {@link StockUnit} in the warehouse.
 * 
 * @author trautm
 *
 */
@Remote
public interface ManageInventoryFacade {

    /**
     * Creates a new ItemData/aarticle in the wms.
     * 
     * @param clientRef a reference to the client
     * @param articleRef a reference to the article
     * @return true if the ItemData could have been created successfully
     */
    @WebMethod
    boolean createItemData(
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "articleRef") String articleRef) throws InventoryException;

    /**
     * Deletes an existing ItemData/article in the wms.
     * 
     * @param clientRef a reference to the client
     * @param articleRef a reference to the article
     * @return true if the ItemData could have been deleted successfully
     */
    @WebMethod
    boolean deleteItemData(
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "articleRef") String articleRef);

    /**
     * Updates/changes the reference
     * 
     * @param clientRef a reference to the client
     * @param existingRef existing reference to the article
     * @param existingRef new reference for this article
     * @return true if the reference could have been updated successfully
     */
    @WebMethod
    boolean updateItemReference(
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "existingRef") String existingRef,
            
            @WebParam(name = "newRef") String newRef);

    /**
     * Creates an avis for the given article. 
     * 
     * All existing instances of this article belonging to another/old batch might be send to
     * the extinguishing process depending on the expireBatch flag.
     * 
     * @param clientRef a reference to the client
     * @param articleRef a reference to the article
     * @param expectedDelivery date when the delivery is expected
     * @param bestBeforeEnd best before end date
     * @param useNotBefore do not use this article before this date
     * @param expireBatch if true, existing batches of this article must be extinguished after arrival of this article
     * @return true if the avis is created successfully
     */
    @WebMethod
    boolean createAvis(
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "articleRef") String articleRef,
            
            @WebParam(name = "batchRef") String batchRef,
            
            @WebParam(name = "amount") BigDecimal amount,
            
            @WebParam(name = "expectedDelivery") Date expectedDelivery,
            
            @WebParam(name = "bestBeforeEnd") Date bestBeforeEnd,
            
            @WebParam(name = "useNotBefore") Date useNotBefore,
            
            @WebParam(name = "expireBatch") boolean expireBatch);

    /**
     * Creates an avis for the given article. 
     * 
     * In contrast to {@link #createAvis(String, String, String, BigDecimal, Date, Date, Date, boolean)} 
     * a reference to an external ID can be provided with parameter <code>parentRequest</code> 
     * 
     * @param clientRef
     * @param articleRef
     * @param batchRef
     * @param amount
     * @param expectedDelivery
     * @param bestBeforeEnd
     * @param useNotBefore
     * @param expireBatch
     * @param requestId
     * @return true if creation was successful
     */
    public boolean createAvis(String clientRef, String articleRef,
			String batchRef, BigDecimal amount, Date expectedDelivery,
			Date bestBeforeEnd, Date useNotBefore, boolean expireBatch, String requestId);

    /**
     * Creates an avis for the given article. 
     * 
     * In contrast to {@link #createAvis(String, String, String, BigDecimal, Date, Date, Date, boolean)} 
     * a reference to an external ID can be provided with parameter <code>parentRequest</code> 
     * 
     * @param clientRef
     * @param articleRef
     * @param batchRef
     * @param amount
     * @param expectedDelivery
     * @param bestBeforeEnd
     * @param useNotBefore
     * @param expireBatch
     * @param requestId
     * @param comment
     * @return true if creation was successful
     */
    public boolean createAvis(String clientRef, String articleRef,
			String batchRef, BigDecimal amount, Date expectedDelivery,
			Date bestBeforeEnd, Date useNotBefore, boolean expireBatch, String requestId, String comment);
    
    /**
     * Creates a {@link StockUnit} on given {@link LOSStorageLocation}
     * 
     * @param clientRef the client the stock unit belongs to
     * @param slName the name of the storage location
     * @param articleRef the item data number
     * @param lotRef the lot name
     * @param amount the amount to be created
     * @param unitLoadRef the labelID of unit load
     * @throws InventoryException 
     * @throws FacadeException
     * @throws EntityNotFoundException
     */
    @WebMethod
    void createStockUnitOnStorageLocation(
            
            @WebParam(name = "clientRef") String clientRef,
            
            @WebParam(name = "slName") String slName,
            
            @WebParam(name = "articleRef") String articleRef,
            
            @WebParam(name = "lotRef") String lotRef,
            
            @WebParam(name = "amount") BigDecimal amount,
            
            @WebParam(name = "unitLoadRef") String unitLoadRef) throws InventoryException, FacadeException, EntityNotFoundException ;

    
    //-------------
    
    /**
     * Deletes {@link StockUnit} from {@link LOSStorageLocation}. Does not necessarily delete the {@link StockUnit} itself!
     */
    void deleteStockUnitsFromStorageLocations(List<BODTO<LOSStorageLocation>> locations) throws FacadeException;
    
    /**
     * Sends all {@link StockUnit} on given {@link LOSStorageLocation}s to nirwana.
     * 
     * @param locations
     * @throws FacadeException
     */
    void sendStockUnitsToNirwanaFromSl(List<BODTO<LOSStorageLocation>> locations) throws FacadeException;
    
    /**
     * Send given List of {@link StockUnit}s to nirwana
     * 
     * @param sus
     * @throws FacadeException
     */
    void sendStockUnitsToNirwana(List<BODTO<StockUnit>> sus) throws FacadeException;
    
    /**
     * Sends all {@link StockUnit}s on given {@link LOSUnitLoad} to nirwana.
     * @param uls
     * @throws FacadeException
     */
    void sendStockUnitsToNirwanaFromUl(List<BODTO<LOSUnitLoad>> uls) throws FacadeException;
    
    /**
     * Sends {@link StockUnit}s on given {@link LOSStorageLocation} to nirwana.
     * 
     * @param location the name of the storage location
     * @throws FacadeException
     */
    void sendStockUnitToNirwana(String location) throws FacadeException;
    
    //-------------
    
    /**
     * Transferres {@link StockUnit} to given {@link LOSUnitLoad}
     */
    void transferStockUnit(BODTO<StockUnit> su, BODTO<LOSUnitLoad> ul) throws FacadeException;
    
    /**
     * 
     * @param suTO transfer this StockUnit
     * @param ulTO to this UnitLoad
     * @param comment
     * @throws FacadeException
     */
    public void transferStockUnit(BODTO<StockUnit> suTO, BODTO<LOSUnitLoad> ulTO, String comment) throws FacadeException;
     
    /**
     * 
     * @param suTO transfer this StockUnit
     * @param ulTO to this UnitLoad
     * @param removeSuReservation remove any reservation on the stock unit
     * @param removeSuLock remove any lock on the stock unit	
     * @throws FacadeException
     */public void transferStockUnit(BODTO<StockUnit> suTO,
			BODTO<LOSUnitLoad> ulTO, 
			boolean removeSuReservation,
			boolean removeSuLock, String comment) throws FacadeException;
    

     /**
      * Tests whether Stockunit can be transferred to Unitload.
      * @param su
      * @param ul
      * @return
      * @throws FacadeException
      */
     public boolean testSuitable(BODTO<StockUnit> su, BODTO<LOSUnitLoad> ul) throws FacadeException;

     
     /**
      * Change the amount of the unitload
      * @param su
      * @param amount amount to be transferred
      * @param reserved amount to be reserved
	  * @param comment
      * @throws FacadeException
      */
     public void changeAmount(BODTO<StockUnit> su, BigDecimal amount, BigDecimal reserved, String comment) throws FacadeException;

     /**
      * Transfers all stock units from one unit load to the other.
      * 
      * @param from
      * @param to
      * @param relesereservation releases reservations on stock unit being transferred
      * @throws FacadeException
      */
     public void transferStock(BODTO<LOSUnitLoad> from, BODTO<LOSUnitLoad> to
 			, boolean relesereservation) throws FacadeException ;
     
     /**
      * Create a new {@link Lot}.
      * 
      * @param client the client the lot belongs to
      * @param item the item data of the lot
      * @param lotnumber the lot name
      * @param useNotBefore lot should not be used before this date date
      * @param bestBeforeEnd lot must not be used after that date
      * @param expireBatch if true, older lots are extinguished on arrvial of new lot
      * @return the created Lot
      * @throws InventoryException
      */
     public BODTO<Lot> createLot(BODTO<Client> client, 
    		 					 BODTO<ItemData> item, 
    		 					 String lotnumber, 
    		 					 Date useNotBefore,
    		 					 Date bestBeforeEnd, 
    		 					 boolean expireBatch) throws InventoryException;
     
     /** 
      * Reserves amount on {@link StockUnit}, e.g. for being picked later on.
      
      * @param stock the stock unit for which to reserve items 
      * @param amountToReserve the amount to reserve
      * @throws InventoryException
      */
     public void reserveStock(BODTO<StockUnit> stock, BigDecimal amountToReserve) throws InventoryException;
     
     /**
      * Releases (previous) reservations on {@link StockUnit} 
      * 
      * @param stockTO the stock unit for which reservations on items should be released
      * @param amountToRelease amount to release
      * @throws InventoryException
      */
     public void releaseReservation(BODTO<StockUnit> stockTO, BigDecimal amountToRelease) throws InventoryException;

     
     /**
      * Remove {@link StockUnit} from System
      * 
      * @param stockUnit
      * @param activityCode
      */
     public void removeStockUnit(BODTO<StockUnit> stockUnit, String activityCode) throws FacadeException;
     
     
 	 /**
 	  * Replacement for ManageStorage.transferUnitLoad.
 	  * It considers fixed location assignments
 	  *  
 	 * @param target
 	 * @param ul
 	 * @param index
 	 * @param ignoreSlLock
 	 * @param info
 	 * @throws FacadeException
 	 */
 	public void transferUnitLoad(BODTO<LOSStorageLocation> target,
			BODTO<LOSUnitLoad> ul, int index, boolean ignoreSlLock, String info) throws FacadeException;
}
