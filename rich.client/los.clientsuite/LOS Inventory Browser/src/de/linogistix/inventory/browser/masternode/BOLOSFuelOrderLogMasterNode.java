package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

//import de.linogistix.common.res.InventoryBundleResolver;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.LOSFuelOrderLogTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;
import java.math.BigDecimal;

public class BOLOSFuelOrderLogMasterNode extends BOMasterNode {

    LOSFuelOrderLogTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSFuelOrderLogMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSFuelOrderLogTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            
	    //BOMasterNodeProperty<String> identityCard = new BOMasterNodeProperty<String>("identityCard", String.class, to.getIdentityCard(), InventoryBundleResolver.class);
	    //sheet.put(identityCard);
                BOMasterNodeProperty<String> orderType= new BOMasterNodeProperty<String>("orderType", String.class, to.getOrderType(), InventoryBundleResolver.class);
                sheet.put(orderType);
		BOMasterNodeProperty<String> storageLocation= new BOMasterNodeProperty<String>("storageLocation", String.class, to.getStorLocName(), InventoryBundleResolver.class);
		sheet.put(storageLocation);
                BOMasterNodeProperty<String> articleDescr= new BOMasterNodeProperty<String>("articleDescr", String.class, to.getRcptArticleDescr(), InventoryBundleResolver.class);
		sheet.put(articleDescr);
		BOMasterNodeProperty<String> plateNumber= new BOMasterNodeProperty<String>("plateNumber", String.class, to.getVehiclePlateNumber(), InventoryBundleResolver.class);
		sheet.put(plateNumber);
		BOMasterNodeProperty<String> receipientName= new BOMasterNodeProperty<String>("receipientName", String.class, to.getReceipientName(), InventoryBundleResolver.class);
		sheet.put(receipientName);
                BOMasterNodeProperty<BigDecimal> rcptQuantity= new BOMasterNodeProperty<BigDecimal>("rcptQuantity", BigDecimal.class, to.getRcptPosQuantity(), InventoryBundleResolver.class);
		sheet.put(articleDescr);
		BOMasterNodeProperty<BigDecimal> tankRemaining= new BOMasterNodeProperty<BigDecimal>("tankRemaining", BigDecimal.class, to.getTankRemaining(), InventoryBundleResolver.class);
		sheet.put(tankRemaining);
        }
        return new PropertySet[] {sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
	    //BOMasterNodeProperty<String> identityCard = new BOMasterNodeProperty<String>("identityCard", String.class, "", InventoryBundleResolver.class);
                BOMasterNodeProperty<String> orderType = new BOMasterNodeProperty<String>("orderType", String.class, "", InventoryBundleResolver.class);
                BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, "", InventoryBundleResolver.class);
                BOMasterNodeProperty<String> articleDescr = new BOMasterNodeProperty<String>("articleDescr", String.class, "", InventoryBundleResolver.class);
		BOMasterNodeProperty<String>  plateNumber = new BOMasterNodeProperty<String>("plateNumber", String.class, "", InventoryBundleResolver.class);
		BOMasterNodeProperty<String> receipientName = new BOMasterNodeProperty<String>("receipientName", String.class, "", InventoryBundleResolver.class);
                BOMasterNodeProperty<BigDecimal> rcptQuantity = new BOMasterNodeProperty<BigDecimal>("rcptQuantity", BigDecimal.class, BigDecimal.ZERO, InventoryBundleResolver.class);
		BOMasterNodeProperty<BigDecimal> tankRemaining = new BOMasterNodeProperty<BigDecimal>("tankRemaining", BigDecimal.class, BigDecimal.ZERO, InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[] {
		//identityCard,
                orderType, storageLocation, articleDescr,
		plateNumber, receipientName, rcptQuantity,
		tankRemaining};
        return props;
    }
}
