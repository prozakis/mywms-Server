/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.exception;

public enum LOSLocationExceptionKey {
	STORAGELOCATION_ALLREADY_FULL,   
	
	STORAGELOCATION_INDEX_FULL,
	
	UNITLOAD_NOT_SUITABLE, 
	
	STORAGELOCATION_ALREADY_RESERVED_FOR_DIFFERENT_TYPE, 
	
	STORAGELOCATION_LOCKED, 
	
	NO_CLIENT_WITH_NAME, WRONG_CLIENT,
	
	NO_ITEM_WITH_NUMBER,
            
    NO_GOODS_IN_LOCATION,
    
    NO_GOODS_OUT_LOCATION,
    
    UNITLOAD_NOT_ON_LOCATION, 
    
    LOCATION_ALLREADY_ASSIGNED_TO_DIFFEREND_ITEM, 
    
    NO_STORE_AREA,NO_PICK_AREA,
    
    NO_SUCH_LOCATION, LOCATION_ISLOCKED,
    
    WRONG_ITEMDATA_FIXASSIGNMENT, 
    
    LOCATION_CONSTRAINT_VIOLATION, 
    
    DEFAULT_UNITLOADTYPE_MISSING, 
    
    WRONG_UNITLOAD, 
    
    NO_SUCH_UNITLOAD, 
    
    
    

}
