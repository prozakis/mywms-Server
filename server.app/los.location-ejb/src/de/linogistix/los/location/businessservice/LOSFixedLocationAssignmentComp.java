/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.ItemData;
import org.mywms.model.UnitLoadType;

import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;

@Local
public interface LOSFixedLocationAssignmentComp {

	public void createFixedLocationAssignment(LOSStorageLocation sl, ItemData item)
					throws LOSLocationException;
	
	public boolean hasFixedLocationAssignment(ItemData item);
	
	public List<LOSStorageLocation> getAssignedLocationsForStorage(ItemData item, UnitLoadType ult) throws LOSLocationException;

	public LOSFixedLocationAssignment getAssignment(LOSStorageLocation sl);

	public boolean isFixedLocation(LOSStorageLocation transfer);
    
    
}
