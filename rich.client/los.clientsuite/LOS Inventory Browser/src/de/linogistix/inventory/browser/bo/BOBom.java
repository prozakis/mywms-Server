/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.query.gui.component.BOQueryComponentProvider;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOBomMasterNode;
import de.linogistix.inventory.browser.query.gui.component.BomDefaultQueryProvider;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.crud.LOSBomCRUDRemote;
import de.linogistix.los.inventory.model.LOSBom;
import de.linogistix.los.inventory.query.LOSBomQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;



/**
 *
 * @author krane
 */
public class BOBom extends BO {
    
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }

  protected String initName() {
    return "Boms";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ItemData.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(LOSBomQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
      return null;
    }
    
    return ret;
  }
  
  

  
  protected BasicEntity initEntityTemplate() {
    LOSBom o;
    o = new LOSBom();

    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSBomCRUDRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
    @Override
   protected String[] initIdentifiableProperties() {
    return new String[]{"id"};
  }


    @Override
    public Class getBundleResolver() {
        return InventoryBundleResolver.class;
    }
   

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOBomMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOBomMasterNode.class;
    }

    @Override
    public List<BOQueryComponentProvider> initQueryComponentProviders() {
        List<BOQueryComponentProvider> coms =  super.initQueryComponentProviders();

        BomDefaultQueryProvider d = new BomDefaultQueryProvider( (LOSBomQueryRemote)getQueryService() );
        coms.add(d);

        return coms;
    }



    @Override
    public BOQueryComponentProvider getDefaultBOQueryProvider() {

        for (BOQueryComponentProvider p : getQueryComponentProviders()){
            if (p.getClass().equals(BomDefaultQueryProvider.class)){
                return p;
            }
        }

        return super.getDefaultBOQueryProvider();
    }

    
    
}
