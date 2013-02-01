/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.action;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.facade.ManageExtinguishFacade;
import de.linogistix.los.inventory.facade.OrderFacade;
import de.linogistix.los.inventory.model.ExtinguishOrder;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.query.OrderRequestQueryRemote;
import de.linogistix.los.query.BODTO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

public final class BOOrderStartAction extends NodeAction {

    private static final Logger log = Logger.getLogger(BOLotExtinguishAction.class.getName());
    private static String[] allowedRoles = new String[]{
        Role.ADMIN_STR,Role.INVENTORY_STR
    };

    public String getName() {
        return NbBundle.getMessage(InventoryBundleResolver.class, "start");
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
        if( login.checkRolesAllowed(allowedRoles) == false ) {
            return false;
        }
        
        for (Node n : activatedNodes) {
            if (n == null) {
                continue;
            }
            if (!(n instanceof BOMasterNode)) {
                continue;
            }
            J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);

            OrderRequestQueryRemote orderQuery;
            LOSOrderRequest r;
            try {
                orderQuery = loc.getStateless(OrderRequestQueryRemote.class);
                r = orderQuery.queryById(((BOMasterNode)n).getEntity().getId());
            } catch (Exception e) {
                return false;
            }
            if( r.getOrderState() != LOSOrderRequestState.RAW ) {
                return false;
            }
        }
        return true;
    }

    protected void performAction(Node[] activatedNodes) {

        BasicEntity e;

        if (activatedNodes == null) {
            return;
        }

        NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(InventoryBundleResolver.class, "NotifyDescriptor.ReallyStart"),
                    NbBundle.getMessage(InventoryBundleResolver.class, "start"),
                    NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.OK_OPTION) {
            return;
        }
        
        try {
            List<BODTO<ExtinguishOrder>> l = new ArrayList<BODTO<ExtinguishOrder>>();
            for (Node n : activatedNodes) {
                l = new ArrayList<BODTO<ExtinguishOrder>>();
                if (n == null) {
                    continue;
                }
                if (!(n instanceof BOMasterNode)) {
                    log.warning("Not a BOMasterNodeType: " + n.toString());
                }
                l.add(((BOMasterNode)n).getEntity());

                J2EEServiceLocator loc = (J2EEServiceLocator) Lookup.getDefault().lookup(J2EEServiceLocator.class);
                OrderRequestQueryRemote orderQuery = loc.getStateless(OrderRequestQueryRemote.class);

                LOSOrderRequest r = orderQuery.queryById(((BOMasterNode)n).getEntity().getId());

                if (r instanceof ExtinguishOrder ){
                    ManageExtinguishFacade m = loc.getStateless(ManageExtinguishFacade.class);
                    m.startExtinguishOrder(l.get(0));
                } else if (r  instanceof LOSOrderRequest){
                    OrderFacade m = loc.getStateless(OrderFacade.class);                        
                    BODTO<LOSOrderRequest> to = ((BOMasterNode)n).getEntity();
                    m.startOrder(to);
                }
                else{
                    log.warning("Ignored: " + r.toDescriptiveString());
                }
            }
        } catch (FacadeException ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
        
        if( activatedNodes.length>0 ) {
            Node n = activatedNodes[0];
            if (n instanceof BOMasterNode) {
                BOMasterNode ma = (BOMasterNode)n;
                BO bo = ma.getBo();
                bo.fireOutdatedEvent(ma);
            }
        }

    }
}

