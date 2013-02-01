/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.model.ItemData;
import org.mywms.service.BasicService;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;

@Local
public interface LOSFixedLocationAssignmentService 
					extends BasicService<LOSFixedLocationAssignment>
{	
	public LOSFixedLocationAssignment getByLocation(LOSStorageLocation sl);
	
	public boolean existsFixedLocationAssignment(ItemData item);
	
	public List<LOSFixedLocationAssignment> getByItemData(ItemData item);
}
