/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.Document;

/**
 *
 * @author MELISA
 */
@Entity
@Table(name = "los_FuelLogBook"
//	, uniqueConstraints = {
//	    @UniqueConstraint(columnNames = {
//	            "BookDocumentID"
//	        })}
) 
public class LOSFuelOrderLogDocument extends Document{

	private static final long serialVersionUID = 1L;

	private String bookDocumentID;
    
    private String storLoc;
    
    private Date dateFrom;
    
    private Date dateTo;
    
            
    @Column(nullable=false)
    public String getBookDocumentID() {
        return bookDocumentID;
    }

    public void setBookDocumentID(String bookDocumentID) {
        this.bookDocumentID = bookDocumentID;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }     
    
    public Date getDateTo() {
        return dateTo;
    }
    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }       
}
