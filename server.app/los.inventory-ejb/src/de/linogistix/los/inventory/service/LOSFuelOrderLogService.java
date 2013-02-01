package de.linogistix.los.inventory.service;

import javax.ejb.Remote;

import java.math.BigDecimal;
import org.mywms.model.ItemData;
import org.mywms.service.BasicService;
import org.mywms.model.VehicleData;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.model.OrderReceiptPosition;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;

@Remote
public interface LOSFuelOrderLogService extends BasicService<LOSFuelOrderLog>{

	public LOSFuelOrderLog create(VehicleData vehicle, LOSStorageLocation storLoc, int stationPump, LOSOrderReceipients receipient, 
				OrderReceiptPosition rcptPos, String orderType, BigDecimal tankRemaining);
	
	public LOSFuelOrderLog create(LOSStorageLocation storLoc, LOSOrderReceipients receipient, 
			ItemData it, BigDecimal amount, String orderType, BigDecimal tankRemaining);
    
}
