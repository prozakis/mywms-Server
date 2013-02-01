package de.linogistix.los.inventory.service;

//import java.util.List;

import javax.ejb.Remote;

import org.mywms.service.EntityNotFoundException;

//import de.linogistix.los.common.exception.UnAuthorizedException;
//import de.linogistix.los.inventory.model.LOSAdvice;
//import de.linogistix.los.inventory.model.LOSAdviceState;
//import de.linogistix.los.inventory.model.LOSGoodsReceipt;
//import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
//import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;

import de.linogistix.los.inventory.model.LOSOrderReceipients;

@Remote
public interface LOSOrderReceipientsServiceRemote {
	
	public LOSOrderReceipients getByIdentityCard(String identityCard) throws EntityNotFoundException;
	
}
