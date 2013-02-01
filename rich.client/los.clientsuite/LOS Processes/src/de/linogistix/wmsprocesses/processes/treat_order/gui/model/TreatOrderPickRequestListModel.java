/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.model;

import de.linogistix.common.gui.component.view.DefaultLOSListViewModel;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.los.inventory.model.LOSOrder;
import de.linogistix.los.inventory.pick.query.LOSPickRequestQueryRemote;
import de.linogistix.los.inventory.pick.model.LOSPickRequest;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author Jordan
 */
public class TreatOrderPickRequestListModel extends DefaultLOSListViewModel{

    private BODTO<LOSOrder> orderTO;
    
    private List<TreatOrderPickRequestTO> newPickRequestList;
    
    private LOSPickRequestQueryRemote pickRequestQuery;
    
    public TreatOrderPickRequestListModel(){
        
        newPickRequestList = new ArrayList<TreatOrderPickRequestTO>();
    }
    
    public void init() throws J2EEServiceLocatorException{
               
        J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
        pickRequestQuery = loc.getStateless(LOSPickRequestQueryRemote.class);
    }
    
    @Override
    public List<TreatOrderPickRequestTO> getResultList() {
        
        return newPickRequestList;
    }

    @Override
    public void clear() {
        
        orderTO = null;
        newPickRequestList.clear();
        
        super.clear();
    }
        
    public void addNewPickRequest(String reqNumber, BODTO<LOSStorageLocation> target){
        
        String[] reqNumberParts = reqNumber.split(" ");
        
        String number = reqNumberParts[reqNumberParts.length-1];
        Long dummyId = Long.parseLong(number);
        
        String name = reqNumber;
        
        if(target != null){
            name = name + "/"+target.getName();
        }
        
        TreatOrderPickRequestTO pickTO = new TreatOrderPickRequestTO(dummyId, 0, name);
        pickTO.pickRequestNumber = reqNumber;
        pickTO.targetPlace = target;
        
        newPickRequestList.add(pickTO);
        
        fireModelChangedEvent();    
    }
    
    public BODTO<LOSOrder> getOrderTO() {
        return orderTO;
    }

    public void setOrderTO(BODTO<LOSOrder> orderTO) {
        this.orderTO = orderTO;
    }

}
