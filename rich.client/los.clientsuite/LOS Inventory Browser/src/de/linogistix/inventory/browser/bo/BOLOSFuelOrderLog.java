package de.linogistix.inventory.browser.bo;

import org.mywms.model.BasicEntity;
import org.mywms.globals.Role;

import de.linogistix.common.bobrowser.bo.BO;
//import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.inventory.crud.LOSFuelOrderLogCRUDRemote;
import de.linogistix.los.inventory.query.LOSFuelOrderLogQueryRemote;
import de.linogistix.los.inventory.model.LOSFuelOrderLog;

import de.linogistix.inventory.browser.masternode.BOLOSFuelOrderLogMasterNode;

import org.openide.util.Lookup;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;

public class BOLOSFuelOrderLog extends BO {

    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR};
    }

    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }

    protected String initName() {
        return "BOLOSFuelOrderLog";
    }

    protected String initIconBaseWithExtension() {
        return "de/linogistix/common/res/icon/Document.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote)loc.getStateless(LOSFuelOrderLogQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
            return null;
        }

        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        LOSFuelOrderLog o;

        o = new LOSFuelOrderLog();
        //o.setLabelId("");

        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        BusinessObjectCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSFuelOrderLogCRUDRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected String[] initIdentifiableProperties() {
        return new String[] {"transactionId"};
    }

    @Override
    public Class initBundleResolver() {
        return InventoryBundleResolver.class;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSFuelOrderLogMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSFuelOrderLogMasterNode.class;
    }

}
