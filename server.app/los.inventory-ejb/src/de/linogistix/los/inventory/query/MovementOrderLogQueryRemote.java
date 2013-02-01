package de.linogistix.los.inventory.query;


import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.model.MovementOrderLog;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

@Remote
public interface MovementOrderLogQueryRemote{ 
	public List<MovementOrderLog> queryByDate(Date createDateFrom,Date createDateTo);
}
