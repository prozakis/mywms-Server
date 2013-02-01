package de.linogistix.los.inventory.query;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Remote
public interface LOSFuelOrderLogQueryRemote extends BusinessObjectQueryRemote<LOSFuelOrderLog>{ 
	public List<LOSFuelOrderLog> queryByDate(Date createDateFrom,Date createDateTo);
	public List<LOSFuelOrderLog> queryByDateAndLoc(Date createDateFrom,Date createDateTo, LOSStorageLocation storageLocation);
}
