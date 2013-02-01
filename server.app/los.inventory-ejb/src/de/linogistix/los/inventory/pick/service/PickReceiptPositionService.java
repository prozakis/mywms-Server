/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.service;

import javax.ejb.Remote;

import org.mywms.service.BasicService;

import de.linogistix.los.inventory.pick.model.PickReceiptPosition;

@Remote
public interface PickReceiptPositionService extends BasicService<PickReceiptPosition>{

}
