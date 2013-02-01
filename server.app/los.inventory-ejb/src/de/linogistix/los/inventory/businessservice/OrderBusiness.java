/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.Date;

//import javax.ejb.Local;
import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.facade.OrderPositionTO;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderReceipients;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.OrderType;
import de.linogistix.los.inventory.model.OrderReceiptPosition;
import de.linogistix.los.inventory.pick.exception.PickingException;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;

/**
 *
 * @author trautm
 */
//@Local
@Remote
public interface OrderBusiness {

    void finishOrder(LOSOrderRequest req, boolean force) throws FacadeException;
    OrderReceiptPosition finishFuelOrder(LOSOrderRequest req, boolean force, LOSOrderReceipients receipient) throws FacadeException;

    LOSOrderRequest createOrder(Client c, String orderRef,
			OrderPositionTO[] positions, String documentUrl, String labelUrl,
			String destination, Date delivery, OrderType type) throws FacadeException;
    
	void processOrderPositionPicked(LOSOrderRequestPosition pos, BigDecimal amount, boolean canceled) throws FacadeException;
	
	void processOrderPicked(LOSOrderRequest order) throws FacadeException;
	void processOrderPicked(LOSOrderRequest order, boolean force)	throws FacadeException;

	void remove(LOSOrderRequest req) throws PickingException, FacadeException ;

	void process(LOSOrderRequest r ) throws FacadeException, OrderCannotBeStarted;
	LOSPickRequest processFuel(LOSOrderRequest r ) throws FacadeException, OrderCannotBeStarted;
}
