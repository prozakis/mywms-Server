package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.ItemData;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;

@Remote
public interface QueryFixedAssignmentServiceRemote {
	
	public LOSFixedLocationAssignment getByLocation(LOSStorageLocation sl);
	
	public boolean existsFixedLocationAssignment(ItemData item);
	
	public List<LOSFixedLocationAssignment> getByItemData(ItemData item);
}
