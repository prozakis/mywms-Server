/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

@Entity
@Table(name = "los_stocktakingrecord")
public class LOSStocktakingRecord extends BasicEntity{

	private static final long serialVersionUID = 1L;

	private LOSStocktakingOrder stocktakingOrder;
	
	private String clientNo;
	
	private String locationName;
	
	private String unitLoadLabel;
	
	private String itemNo;
	
	private String lotNo;
	
	private BigDecimal plannedQuantity;
	
	private BigDecimal countedQuantity;
	
	private String serialNo;
	
	private Long countedStockId;
	
	private LOSStocktakingState state;

	private String ulTypeNo = null;
	
	@ManyToOne(optional = true)
	public LOSStocktakingOrder getStocktakingOrder() {
		return stocktakingOrder;
	}

	public void setStocktakingOrder(LOSStocktakingOrder stocktakingOrder) {
		this.stocktakingOrder = stocktakingOrder;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public String getLotNo() {
		return lotNo;
	}

	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}

	@Column(precision=10, scale=10)
	public BigDecimal getPlannedQuantity() {
		return plannedQuantity;
	}

	public void setPlannedQuantity(BigDecimal plannedQuantity) {
		this.plannedQuantity = plannedQuantity;
	}

	@Column(precision=10, scale=10)
	public BigDecimal getCountedQuantity() {
		return countedQuantity;
	}

	public void setCountedQuantity(BigDecimal countedQuantity) {
		this.countedQuantity = countedQuantity;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	
	public Long getCountedStockId() {
		return countedStockId;
	}

	public void setCountedStockId(Long countedStockId) {
		this.countedStockId = countedStockId;
	}

	public LOSStocktakingState getState() {
		return state;
	}

	public void setState(LOSStocktakingState state) {
		this.state = state;
	}

	public String getUlTypeNo() {
		return ulTypeNo;
	}

	public void setUlTypeNo(String ulTypeNo) {
		this.ulTypeNo = ulTypeNo;
	}
	
}
