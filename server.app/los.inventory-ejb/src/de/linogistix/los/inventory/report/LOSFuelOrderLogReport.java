/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.report;
import de.linogistix.los.inventory.model.LOSFuelOrderLogDocument;
import de.linogistix.los.location.model.LOSStorageLocation;

import java.util.Date;

import javax.ejb.Remote;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSFuelOrderLog;
import de.linogistix.los.report.businessservice.ReportException;

@Remote
public interface LOSFuelOrderLogReport {
	
	 /**
     * Prints a persisted LOSFuelLogReport.
     * @param storageLocation
     * @param LogDateFrom
     * @param LogDateTo
     * @param printer
     * @return
     * @throws org.mywms.service.EntityNotFoundException
     * @throws de.linogistix.los.report.businessservice.ReportException
     */
    public LOSFuelOrderLogDocument printFuelOrderLogReport(LOSStorageLocation storageLocation, Date LogDateFrom, Date LogDateTo, String printer) throws ReportException;
        
}
