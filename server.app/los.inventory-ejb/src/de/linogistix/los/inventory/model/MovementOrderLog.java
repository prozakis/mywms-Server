/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.inventory.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

@Entity
@Table(name = "movement_order")
@Inheritance(strategy = InheritanceType.JOINED)
public class MovementOrderLog extends BasicEntity {
	private static final long serialVersionUID = 1L;

	private String organization = "";
	private String formation = "";
	private String militaryUnit = "";
	private long sequenceNumber = 0;
	private Date currDate = null;
	private String plateNo = "";
	private String vehicleType = "";
	private Date movementDate = null;
	private String judgmentNo = "";
	private String movementPurpose = "";
	private String movementRoute = "";
	private String movementLoad = "";
	private String driverName = "";
	private String passenger1Name = "";
	private String passenger2Name = "";
	private String passenger3Name = "";
	private String passenger4Name = "";

	
	
	
	@Column(nullable = false)
	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	 @Column(nullable = false)
	public String getFormation() {
		return this.formation;
	}

	public void setFormation(String formation) {
		this.formation = formation;
	}

	@Column(nullable = false)
	public String getMilitaryUnit() {
		return this.militaryUnit;
	}

	public void setMilitaryUnit(String militaryUnit) {
		this.militaryUnit = militaryUnit;
	}

	@Column(nullable = false, unique = true)
	public Long getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	@Column(nullable = false)
	public Date getCurrDate() {
		return this.currDate;
	}

	public void setCurrDate(Date currDate) {
		this.currDate = currDate;
	}
	
	@Column(nullable = false)
	public String getPlateNo() {
		return this.plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getVehicleType() {
		return this.vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	
	@Column(nullable = false)
	public String getJudgmentNo() {
		return this.judgmentNo;
	}

	public void setJudgmentNo(String judgmentNo) {
		this.judgmentNo = judgmentNo;
	}
	
	@Column(nullable = false)
	public Date getMovementDate() {
		return this.movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	@Column(nullable = false)
	public String getMovementPurpose() {
		return this.movementPurpose;
	}

	public void setMovementPurpose(String movementPurpose) {
		this.movementPurpose = movementPurpose;
	}
	
	@Column(nullable = false)
	public String getMovementRoute() {
		return this.movementRoute;
	}

	public void setMovementRoute(String movementRoute) {
		this.movementRoute = movementRoute;
	}
	
	public String getMovementLoad() {
		return this.movementLoad;
	}

	public void setMovementLoad(String movementLoad) {
		this.movementLoad = movementLoad;
	}
	
	@Column(nullable = false)
	public String getDriverName() {
		return this.driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public String getPassenger1Name() {
		return this.passenger1Name;
	}

	public void setPassenger1Name(String passenger1Name) {
		this.passenger1Name = passenger1Name;
	}
	
	public String getPassenger2Name() {
		return this.passenger2Name;
	}

	public void setPassenger2Name(String passenger2Name) {
		this.passenger2Name = passenger2Name;
	}
	
	public String getPassenger3Name() {
		return this.passenger3Name;
	}

	public void setPassenger3Name(String passenger3Name) {
		this.passenger3Name = passenger3Name;
	}
	
	public String getPassenger4Name() {
		return this.passenger4Name;
	}

	public void setPassenger4Name(String passenger4Name) {
		this.passenger4Name = passenger4Name;
	}
}
