/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.stocktaking.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

@Entity
@Table(name="los_stocktaking")
public class LOSStockTaking extends BasicEntity {

	private static final long serialVersionUID = 1L;
	
	private String stockTakingNumber;

	private LOSStockTakingType stockTakingType;
	
	private Date started;
	
	private Date ended;
	
	private List<LOSStocktakingOrder> orderList;

	@Column(nullable=false, unique=true)
	public String getStockTakingNumber() {
		return stockTakingNumber;
	}

	public void setStockTakingNumber(String stockTakingNumber) {
		this.stockTakingNumber = stockTakingNumber;
	}

	@Enumerated(EnumType.STRING)
	public LOSStockTakingType getStockTakingType() {
		return stockTakingType;
	}

	public void setStockTakingType(LOSStockTakingType stockTakingType) {
		this.stockTakingType = stockTakingType;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getEnded() {
		return ended;
	}

	public void setEnded(Date ended) {
		this.ended = ended;
	}

	@OneToMany(mappedBy="stockTaking")
	public List<LOSStocktakingOrder> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<LOSStocktakingOrder> orderList) {
		this.orderList = orderList;
	}
}
