/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.query.dto.LOSOrderRequestTO;
import de.linogistix.los.query.BODTO;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOReplenishRequestMasterNode extends BOMasterNode {

    LOSOrderRequestTO to;

    /** Creates a new instance of BODeviceNode */
    public BOReplenishRequestMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSOrderRequestTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.getOrderState().equals(LOSOrderRequestState.FINISHED.name())) {
             ret = "<font color=\"#C0C0C0\">" + ret + "</font>";
        }
        return ret;
    }
    
    @Override
    public Image getIcon(int arg0) {
        return super.getIcon(arg0);
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), InventoryBundleResolver.class);
            sheet.put(clientNumber);
            BOMasterNodeProperty<String> destinationName = new BOMasterNodeProperty<String>("destinationName", String.class, to.getDestinationName(), InventoryBundleResolver.class);
            sheet.put(destinationName);
            BOMasterNodeProperty<String> orderState = new BOMasterNodeProperty<String>("orderState", String.class, "LOSOrderRequestState." + to.getOrderState(), CommonBundleResolver.class, true);
            sheet.put(orderState);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> destinationName = new BOMasterNodeProperty<String>("destinationName", String.class, "", InventoryBundleResolver.class);
            BOMasterNodeProperty<String> orderState = new BOMasterNodeProperty<String>("orderState", String.class, "", CommonBundleResolver.class);
        return new Property[]{clientNumber, destinationName, orderState};
    }
}
