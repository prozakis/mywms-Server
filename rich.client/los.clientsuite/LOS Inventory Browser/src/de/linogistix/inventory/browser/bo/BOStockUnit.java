/*
 * BONodeUser.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.inventory.browser.action.BOStockUnitNirwanaAction;
import de.linogistix.inventory.browser.masternode.BOStockUnitMasterNode;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.inventory.browser.query.gui.component.StockUnitDefaultQueryProvider;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.entityservice.BusinessObjectLock;
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.query.StockUnitQueryRemote;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.Action;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.mywms.model.StockUnit;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOStockUnit extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR};
    }
 
    Vector<Action> actions;

    protected String initName() {
        return "StockUnits";
    }

    protected String initIconBaseWithExtension() {

        return "de/linogistix/bobrowser/res/icon/StockUnit.png";
    }

    protected BusinessObjectQueryRemote initQueryService() {

        BusinessObjectQueryRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectQueryRemote) loc.getStateless(StockUnitQueryRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected BasicEntity initEntityTemplate() {
        StockUnit o;

        o = new StockUnit();
        o.setLabelId("");

        return o;

    }

    protected BusinessObjectCRUDRemote initCRUDService() {
        BusinessObjectCRUDRemote ret = null;

        try {
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ret = (BusinessObjectCRUDRemote) loc.getStateless(StockUnitCRUDRemote.class);

        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        }
        return ret;
    }

    protected Class initBundleResolver() {
        return CommonBundleResolver.class;
    }

    protected String[] initIdentifiableProperties() {
        return new String[]{"labelId"};
    }

    protected List<SystemAction> doNot_initMasterActions() {
        List<SystemAction> actions = super.initMasterActions();
        
        SystemAction action = SystemAction.get(BOStockUnitNirwanaAction.class);
        action.setEnabled(true);
        actions.add(action);
        return actions;
    }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOStockUnitMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOStockUnitMasterNode.class;
    }

    @Override
    public List<BusinessObjectLock> getLockStates() {
        List<BusinessObjectLock> ret =  super.getLockStates();
        ret.addAll(Arrays.asList(StockUnitLockState.values()));
        
        return ret;
    }

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        List<BOQueryComponentProvider> coms =  super.initQueryComponentProviders();
        
        StockUnitDefaultQueryProvider d = new StockUnitDefaultQueryProvider((StockUnitQueryRemote) getQueryService());
        coms.add(d);
        
        return coms;
    }

    @Override
    public BOQueryComponentProvider getDefaultBOQueryProvider() {
        
        for (BOQueryComponentProvider p : getQueryComponentProviders()){
            if (p.getClass().equals(StockUnitDefaultQueryProvider.class)){
                return p;
            }
        }
        
        return super.getDefaultBOQueryProvider();
    }
}
