package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.service.BasicServiceBean;

import java.math.BigDecimal;
import org.mywms.service.BasicService;
import org.mywms.model.VehicleData;
import org.mywms.model.ItemData;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.model.OrderReceiptPosition;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
@Stateless
public class LOSFuelOrderLogServiceBean extends BasicServiceBean<LOSFuelOrderLog> implements LOSFuelOrderLogService{

	Logger log = Logger.getLogger(LOSFuelOrderLogServiceBean.class);

	public LOSFuelOrderLog create(VehicleData vehicle, LOSStorageLocation storLoc, int stationPump, LOSOrderReceipients receipient, 
				OrderReceiptPosition rcptPos, String orderType, BigDecimal tankRemaining){

		//log.error("MELISA fuel log "+ orderType);
		LOSFuelOrderLog fuelLog = new LOSFuelOrderLog(); 

		if(vehicle != null){
			fuelLog.setVehicleID(vehicle.getId());
			fuelLog.setVehicleLabelID(vehicle.getLabelId());
			fuelLog.setVehiclePlateNumber(vehicle.getPlateNumber());
		}
		if(storLoc != null){
			fuelLog.setStorLocID(storLoc.getId());
			fuelLog.setStorLocName(storLoc.getName());
		}
		fuelLog.setStationPump(stationPump);
		if(receipient != null){
			fuelLog.setReceipientID(receipient.getId());
			String receipientName = receipient.getRankAbbr() + " " + receipient.getFirstName() + " " + receipient.getLastName(); 
			fuelLog.setReceipientName(receipientName);
			fuelLog.setReceipientTokenID(receipient.getTokenId());
			fuelLog.setReceipientIDCard(receipient.getIdentityCard());
		}
		fuelLog.setOrderType(orderType);
		fuelLog.setTankRemaining(tankRemaining);
		if(rcptPos != null){
			fuelLog.setRcptPosID(rcptPos.getId());
			fuelLog.setRcptArticleDescr(rcptPos.getArticleDescr());
			fuelLog.setRcptArticleRef(rcptPos.getArticleRef());
			fuelLog.setRcptPosQuantity(rcptPos.getAmount());
		}
		
		manager.persist(fuelLog);
		manager.flush();
		return fuelLog;
	}
	
	public LOSFuelOrderLog create(LOSStorageLocation storLoc, LOSOrderReceipients receipient, 
			ItemData it, BigDecimal amount, String orderType, BigDecimal tankRemaining){

			//log.error("MELISA fuel log "+ orderType);
			LOSFuelOrderLog fuelLog = new LOSFuelOrderLog(); 
						
			if(storLoc != null){
				fuelLog.setStorLocID(storLoc.getId());
				fuelLog.setStorLocName(storLoc.getName());
			}

			if(receipient != null){
				fuelLog.setReceipientID(receipient.getId());
				String receipientName = receipient.getRankAbbr() + " " + receipient.getFirstName() + " " + receipient.getLastName(); 
				fuelLog.setReceipientName(receipientName);
				fuelLog.setReceipientTokenID(receipient.getTokenId());
				fuelLog.setReceipientIDCard(receipient.getIdentityCard());
			}
			fuelLog.setOrderType(orderType);
			fuelLog.setTankRemaining(tankRemaining);
			if(it != null){				
				fuelLog.setRcptArticleDescr(it.getDescription());
				fuelLog.setRcptArticleRef(it.getNumber());
				fuelLog.setRcptPosQuantity(amount);
			}
			
			manager.persist(fuelLog);
			manager.flush();
			return fuelLog;
	}
	
	
}
