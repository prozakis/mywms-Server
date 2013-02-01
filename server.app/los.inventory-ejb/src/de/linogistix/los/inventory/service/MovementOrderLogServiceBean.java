package de.linogistix.los.inventory.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.service.BasicServiceBean;

import de.linogistix.los.inventory.model.MovementOrderLog;


public class MovementOrderLogServiceBean extends BasicServiceBean<MovementOrderLog>
		implements MovementOrderLogService {
	private static final Logger log = Logger
			.getLogger(MovementOrderLogServiceBean.class.getName());

	@Override
	public MovementOrderLog createNewMovementOrder(String organization,
			String formation, String militaryUnit, String plateNo,
			String vehicleType, String movementDateStr, String judgmentNo,
			String movementPurpose, String movementRoute, String movementLoad,
			String driverName, String passenger1Name, String passenger2Name,
			String passenger3Name, String passenger4Name) {
		
		MovementOrderLog myNewMovementOrder;
		
		
		if (organization == null || formation == null || militaryUnit == null
				|| plateNo == null || movementDateStr == null
				|| judgmentNo == null || movementRoute == null
				|| movementPurpose == null || driverName == null) {
			throw new NullPointerException(
					"Creating new movement order failed due to missing parameter(s)");
		} else {
			// Convert movementDate string to a Date Object
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
			Date movementDate = null;
			try {
				movementDate = dateFormat.parse(movementDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// Get current date to String and Date object
			dateFormat = new SimpleDateFormat("dd/mm/yyyy");
			Calendar cal = Calendar.getInstance();
			Date currDate = cal.getTime();
			String currDateStr = dateFormat.format(currDate);

			myNewMovementOrder = new MovementOrderLog();
			myNewMovementOrder.setCurrDate(currDate);
			myNewMovementOrder.setOrganization(organization);
			myNewMovementOrder.setFormation(formation);
			myNewMovementOrder.setMilitaryUnit(militaryUnit);
			myNewMovementOrder.setPlateNo(plateNo);
			if (vehicleType != null)
				myNewMovementOrder.setVehicleType(vehicleType);
			myNewMovementOrder.setMovementDate(movementDate);
			myNewMovementOrder.setJudgmentNo(judgmentNo);
			myNewMovementOrder.setMovementPurpose(movementPurpose);
			myNewMovementOrder.setMovementRoute(movementRoute);
			if (movementLoad != null)
				myNewMovementOrder.setMovementLoad(movementLoad);
			myNewMovementOrder.setDriverName(driverName);
			myNewMovementOrder.setDriverName(driverName);
			myNewMovementOrder.setDriverName(driverName);
			myNewMovementOrder.setDriverName(driverName);
			myNewMovementOrder.setDriverName(driverName);

			
		}
		
			manager.persist(myNewMovementOrder);
			manager.flush();
			return myNewMovementOrder;
	}

	@Override
	public boolean deleteMovementOrder(long sequenceNumber) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean editMovementOrder(long sequenceNumber, String[] fields,
			String[] values) {
		// TODO Auto-generated method stub
		return false;
	}

}
