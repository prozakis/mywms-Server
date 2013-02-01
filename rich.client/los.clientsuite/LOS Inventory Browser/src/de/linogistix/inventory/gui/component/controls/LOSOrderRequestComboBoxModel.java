/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.gui.component.controls;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBoxModel;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.query.OrderRequestQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class LOSOrderRequestComboBoxModel extends BOAutoFilteringComboBoxModel<LOSOrderRequest>{

    private OrderRequestQueryRemote orderQuery;
    
    private LOSOrderRequestState orderState;
    
    public LOSOrderRequestComboBoxModel() throws Exception{
        super(LOSOrderRequest.class);
        
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        orderQuery = loc.getStateless(OrderRequestQueryRemote.class);
    }
    
    @Override
    public LOSResultList<BODTO<LOSOrderRequest>> getResults(String searchString, QueryDetail detail) {
        
        LOSResultList<BODTO<LOSOrderRequest>> resList;
        resList = orderQuery.autoCompletionByState(searchString, null, orderState, detail);
        return resList;
    }

    @Override
    public void clear() {
        super.clear();
        orderState = null;
    }
    
     public LOSOrderRequestState getOrderState() {
        return orderState;
    }

    public void setOrderState(LOSOrderRequestState orderState) {
        this.orderState = orderState;
    }
    
}
