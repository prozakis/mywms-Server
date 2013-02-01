package de.linogistix.los.inventory.service;

import java.util.Date;

import javax.ejb.Local;

import org.mywms.model.ItemData;
import de.linogistix.los.inventory.model.MovementOrderLog;
import org.mywms.service.BasicService;

/*
 * Defines interface for MovementOrder
 * 
 */

@Local
public interface MovementOrderLogService extends BasicService<MovementOrderLog> {

	MovementOrderLog createNewMovementOrder(String organization, String formation,
			String militaryUnit, String plateNo, String vehicleType,
			String movementDate, String judgmentNo, String movementPurpose,
			String movementRoute, String movementLoad, String driverName,
			String passenger1Name, String passenger2Name,
			String passenger3Name, String passenger4Name);

	boolean deleteMovementOrder(long sequenceNumber);

	boolean editMovementOrder(long sequenceNumber, String[] fields,
			String[] values);
}
