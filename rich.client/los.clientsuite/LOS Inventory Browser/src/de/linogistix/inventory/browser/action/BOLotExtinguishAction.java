/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.ManageExtinguishFacade;
import de.linogistix.los.query.BODTO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.mywms.model.Lot;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class BOLotExtinguishAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOLotExtinguishAction.class.getName());
    private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,
        Role.INVENTORY_STR
    };

    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "extinguish");
    }

    protected String iconResource() {
        return "de/linogistix/bobrowser/res/icon/Action.png";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

    protected boolean enable(Node[] activatedNodes) {
        LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
        return login.checkRolesAllowed(allowedRoles);
    }

    @SuppressWarnings("unchecked")
    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null) {
            return;
        }

        try {
            NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.ReallyExtinguish"),
                    NbBundle.getMessage(InventoryBundleResolver.class, "extinguish"),
                    NotifyDescriptor.OK_CANCEL_OPTION);

            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                
                for (Node n : activatedNodes) {
                    List<BODTO<Lot>> l = new ArrayList<BODTO<Lot>>();
                    if (n == null) {
                        continue;
                    }
                    if (!(n instanceof BOMasterNode)) {
                        log.warning("Not a BOMasterNodeType: " + n.toString());
                    }
                    l.add(((BOMasterNode)n).getEntity());
                    J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                    ManageExtinguishFacade m = loc.getStateless(ManageExtinguishFacade.class);
                    m.createExtinguishOrders(l);
                }
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }
}

