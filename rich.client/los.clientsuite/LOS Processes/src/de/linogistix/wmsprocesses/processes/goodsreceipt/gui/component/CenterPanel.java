/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.controls.BOChooser;
import de.linogistix.common.gui.component.controls.BOMultiSelectionChooser;
import de.linogistix.common.gui.component.gui_builder.AbstractBOChooser;
import de.linogistix.common.gui.component.other.LOSExplorerProviderPanel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractCenterPanel;
import de.linogistix.common.gui.listener.TopComponentListener;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.common.util.GraphicUtil;
import de.linogistix.inventory.browser.masternode.BOLOSAdviceMasterNode;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.LOSStorageLocationQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.mywms.model.Client;
import org.mywms.model.Lot;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.TreeTableView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author jordan
 * 
 */
public class CenterPanel extends AbstractCenterPanel implements TopComponentListener {

    private static final Logger logger = Logger.getLogger(CenterPanel.class.getName());
    TopComponentPanel topComponentPanel;
    
    public ExplorerManager wePosManager;
    
    private ExplorerManager adviceManager;
    
    AbstractNode root;
    Lookup lookup;
    private boolean initialized;
    private BOAutoFilteringComboBox<Client> clientComboBox = null;
    private BOAutoFilteringComboBox<LOSGoodsReceipt> goodsReceiptComboBox = null;
    private BODTO<Client> myClient;
    private BODTO<LOSStorageLocation> defaultLocation = null;
    
    BOMultiSelectionChooser adviceSelectionDialog;
    
    public CenterPanel(TopComponentPanel topComponentPanel) {
        this.topComponentPanel = topComponentPanel;

        infoLabel.setIcon(GraphicUtil.getInstance().getIcon(IconType.INFORMATION));
        infoLabel.setText(NbBundle.getMessage(WMSProcessesBundleResolver.class, "GoodsreceiptCenterPanel.infoLabel.text"));
        
//        removeAdviceButton.setEnabled(false);
    }

    //------------------------------------------------------------------------
    public BOAutoFilteringComboBox<LOSGoodsReceipt> getGoodsReceiptComboBox() {
        if (goodsReceiptComboBox == null) {
            goodsReceiptComboBox = new BOAutoFilteringComboBox<LOSGoodsReceipt>(LOSGoodsReceipt.class);
            goodsreceiptComboPanel.add(goodsReceiptComboBox, BorderLayout.CENTER);
            goodsReceiptComboBox.addItemChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    goodsReceiptChange(evt);
                }
            });
        }

        return goodsReceiptComboBox;
    }

    private void initAutofiltering() {

        getGoodsReceiptComboBox().setEnabled(true);
        getGoodsReceiptComboBox().setEditorLabelTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "GoodsReceipt"));
        getGoodsReceiptComboBox().setMandatory(false);

    }

    protected void postInit() {

        topComponentPanel.controller = new GoodsReceiptController(topComponentPanel);

        beanView = new TreeTableView();
        beanView.setProperties(GoodsReceiptPositiontNode.templateProperties());
        positionTablePanel.add(beanView, BorderLayout.CENTER);

        adviceView = new TreeTableView();
        adviceView.setProperties(BOLOSAdviceMasterNode.boMasterNodeProperties());
        adviceTablePanel.add(adviceView, BorderLayout.CENTER);

        topComponentPanel.controller.onComponentOpened();
        
    }

    //-----------------------------------------------------------------------
    public void componentOpened() {

        if (initialized) {
            return;
        }

        postInit();
        initAutofiltering();

        initialized = true;
        
        LoginService login = Lookup.getDefault().lookup(LoginService.class);
        
        try {
            Client c = login.getUsersClient();
            myClient = new BODTO<Client>(c.getId(), c.getVersion(), c.toUniqueString());
            
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            LOSSystemPropertyServiceRemote propertyFacade = loc.getStateless(LOSSystemPropertyServiceRemote.class);
            String locationName = propertyFacade.getString(loc.getWorkstationName(), LOSInventoryPropertyKey.DEFAULT_GOODS_RECEIPT_LOCATION_NAME);
            if( locationName != null ) {
                LOSStorageLocationQueryRemote locationQuery = loc.getStateless(LOSStorageLocationQueryRemote.class); 

                LOSStorageLocation sl = locationQuery.queryByIdentity(locationName);
                defaultLocation = new BODTO<LOSStorageLocation>(sl.getId(), sl.getVersion(), sl.getName());

            }
            
        } catch (Throwable ex) {
            logger.severe(ex.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                getGoodsReceiptComboBox().requestFocus();
            }
        });
        
        checkState_AllButtons();
        
    }

    //-----------------------------------------------------------------------

    public ExplorerManager getExplorerManager() {

        if (this.wePosManager == null) {
            this.wePosManager = ((LOSExplorerProviderPanel) positionTablePanel).getExplorerManager();
            this.wePosManager.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                        
                        LOSGoodsReceipt gr = getGoodsReceiptComboBox().getSelectedAsEntity();
                        
                        checkState_RemovePositionButton(gr);
                    }
                }
            });
        }
        return this.wePosManager;
    }

    public Lookup getLookup() {
        return this.lookup;
    }

    public ExplorerManager getPositionManager() {
        return getExplorerManager();
    }

    public ExplorerManager getAdviceManager() {
        if (this.adviceManager == null) {
            this.adviceManager = ((LOSExplorerProviderPanel) adviceTablePanel).getExplorerManager();
            
            this.adviceManager.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                        
                        LOSGoodsReceipt gr = getGoodsReceiptComboBox().getSelectedAsEntity();
                        
                        checkState_RemoveAdviceButton(gr);
                        checkState_AddPositionButton(gr);
                    }
                }
            });
        }
        return this.adviceManager;
    }

    public void checkState_AllButtons(){
        
        LOSGoodsReceipt gr = getGoodsReceiptComboBox().getSelectedAsEntity();
        
        checkState_EditButton(gr);
        checkState_FinishButton(gr);
        checkState_AddAdviceButton(gr);
        checkState_RemoveAdviceButton(gr);
        checkState_AddPositionButton(gr);
        checkState_RemovePositionButton(gr);
        
    }
    
    private void checkState_EditButton(LOSGoodsReceipt gr){
        
        if(gr == null){
            editButton.setEnabled(false);
        }
        else if(gr.getReceiptState() == LOSGoodsReceiptState.FINISHED
                || gr.getReceiptState() == LOSGoodsReceiptState.TRANSFER)
        {
            editButton.setEnabled(false);
        }
        else{
            editButton.setEnabled(true);
        }
    }
    
    private void checkState_FinishButton(LOSGoodsReceipt gr){
        
        if(gr == null){
            finishButton.setEnabled(false);
        }
        else if(gr.getReceiptState() == LOSGoodsReceiptState.FINISHED)
        {
            finishButton.setEnabled(false);
        }
        else{
            finishButton.setEnabled(true);
        }
    }
    
    private void checkState_AddAdviceButton(LOSGoodsReceipt gr){
                
        if(gr == null){
            addAdviceButton.setEnabled(false);
        }
        else if(gr.getReceiptState() == LOSGoodsReceiptState.FINISHED
                || gr.getReceiptState() == LOSGoodsReceiptState.TRANSFER)
        {
            addAdviceButton.setEnabled(false);
        }
        else{
            addAdviceButton.setEnabled(true);
        }
        
    }
    
    private void checkState_RemoveAdviceButton(LOSGoodsReceipt gr){
              
        if(gr == null){
            removeAdviceButton.setEnabled(false);
        }
        else if(gr.getReceiptState() == LOSGoodsReceiptState.FINISHED
                || gr.getReceiptState() == LOSGoodsReceiptState.TRANSFER)
        {
            removeAdviceButton.setEnabled(false);
        }
        else if( adviceManager == null ) {
            removeAdviceButton.setEnabled(false);
        } 
        else {
            Node[] selNodes = adviceManager.getSelectedNodes();
            if(selNodes == null || selNodes.length == 0){
                removeAdviceButton.setEnabled(false);
            }
            else {
                removeAdviceButton.setEnabled(true);
            }
        }
        
    }
    
    private void checkState_AddPositionButton(LOSGoodsReceipt gr){
               
        if(gr == null){
            addPositionButton.setEnabled(false);
        }
        else if(gr.getReceiptState() == LOSGoodsReceiptState.FINISHED
                || gr.getReceiptState() == LOSGoodsReceiptState.TRANSFER)
        {
            addPositionButton.setEnabled(false);
        }
        else {
            Node[] selNodes = getAdviceManager().getSelectedNodes();
            if(selNodes == null || selNodes.length == 0){
                addPositionButton.setEnabled(false);
            }
            else {
                addPositionButton.setEnabled(true);
            }
        }
    }
    
    private void checkState_RemovePositionButton(LOSGoodsReceipt gr){
                
        if(gr == null){
            removePositionButton.setEnabled(false);
        }
        else if(gr.getReceiptState() == LOSGoodsReceiptState.FINISHED
                || gr.getReceiptState() == LOSGoodsReceiptState.TRANSFER)
        {
            removePositionButton.setEnabled(false);
        }
        else {
            if( wePosManager == null ) {
                removePositionButton.setEnabled(false);
            }
            else {
                Node[] selNodes = wePosManager.getSelectedNodes();
                if(selNodes == null || selNodes.length == 0){
                    removePositionButton.setEnabled(false);
                }
                else {
                    if(selNodes[0] instanceof GoodsReceiptPositiontNode){
                        GoodsReceiptPositiontNode grPosNode = (GoodsReceiptPositiontNode) selNodes[0];
                        if(grPosNode.pos.getState() != LOSGoodsReceiptState.RAW){
                            removePositionButton.setEnabled(false);
                        }
                        else{
                            removePositionButton.setEnabled(true);
                        }
                    }
                    else{
                        removePositionButton.setEnabled(false);
                    }
                }
            }
        }
        
    }

    @Override
    protected void addAdviceButtonActionPerformedListener(ActionEvent evt) {
        
        topComponentPanel.controller.reloadGoodsReceipt();
        
        List<BODTO> advList = new ArrayList<BODTO>();
        LOSGoodsReceipt gr = this.topComponentPanel.controller.goodsReceiptNode.getGoodsReceipt();
                
        if(adviceSelectionDialog == null){
            adviceSelectionDialog = new BOMultiSelectionChooser(LOSAdvice.class);
        }
        adviceSelectionDialog.showDialog();

        if (adviceSelectionDialog.dialogDescriptor.getValue() instanceof AbstractBOChooser.CustomButton) {
            BOChooser.CustomButton button = (BOChooser.CustomButton) adviceSelectionDialog.dialogDescriptor.getValue();
            if (button.getActionCommand().equals(BOChooser.OK_BUTTON)) {
                List<BODTO> dtoList = adviceSelectionDialog.getSelectedValues();
                for(BODTO dto:dtoList){
                    try {
                        LOSAdvice adv = topComponentPanel.controller.getAdvice(dto.getId());
                        if(!gr.getAssignedAdvices().contains(adv)){
                            LOSAdviceTO to = new LOSAdviceTO(adv);
                            topComponentPanel.controller.addAssignedAdvice(to);
                        }

                    } catch (Throwable ex) {
                        ExceptionAnnotator.annotate(ex);
                    }
                }
                topComponentPanel.controller.reloadGoodsReceipt();
            }
        }
    }

    @Override
    protected void addPositionButtonActionPerformedListener(ActionEvent evt) {
        LOSAdviceTO advTO = null;
        Node node = null;
        
        Node[] advices = adviceManager.getSelectedNodes();
        if (advices != null && advices.length != 0){
            if (advices[0] instanceof BOLOSAdviceMasterNode){
                
                advTO = (LOSAdviceTO) ((BOLOSAdviceMasterNode)advices[0]).getEntity();
                node = advices[0];
            }
        }
        
        boolean retry = false;
        
        do{
            try {
                // Read the client from the GoodsReceipt
                BODTO<Client> grClient = null;
                LOSGoodsReceipt gr = this.topComponentPanel.controller.goodsReceiptNode.getGoodsReceipt();
                if( gr != null ) {
                    Client c = gr.getClient();
                    grClient = new BODTO<Client>(c.getId(), c.getVersion(), c.toUniqueString());
                }
                if( grClient == null ) {
                    grClient = myClient;
                }

                PositionWizard w = new PositionWizard(grClient, 
                                                      this.topComponentPanel.controller.goodsReceiptNode.getGoodsReceipt(),
                                                      advTO, this.topComponentPanel.controller.readUnitLoad);
                
                Dialog d = DialogDisplayer.getDefault().createDialog(w);
                d.setVisible(true);

                if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                    CursorControl.showWaitCursor();
                                        
                    for (int i=0;i<w.model.sameCount;i++){
                    
                        if (w.model.createLot() && i==0){ // create lot just once!

                            LOSGoodsReceiptPosition grPos;
                            grPos = topComponentPanel.controller.createPositionAndLot(
                                                            w.model.client,
                                                            w.model.lotStr,
                                                            w.model.validFrom,
                                                            w.model.validTo,
                                                            w.model.expire,
                                                            w.model.item,
                                                            w.model.unitLoadLabelId,
                                                            w.model.ulType,
                                                            w.model.amount,
                                                            w.model.advice,
                                                            w.model.type,
                                                            w.model.lock,
                                                            w.model.info
                                                            );
                            
                            Lot lot = grPos.getStockUnit().getLot();
                            w.model.lot = new BODTO<Lot>(lot.getId(), lot.getVersion(), lot.getName());
                            
                        } else{

                        topComponentPanel.controller.createPosition(
                                w.model.client,
                                w.model.lot,
                                w.model.item,
                                w.model.unitLoadLabelId,
                                w.model.ulType,
                                w.model.amount,
                                w.model.advice,
                                w.model.type,
                                w.model.lock,
                                w.model.info
                                );
                        }
                    }
                }
                this.topComponentPanel.controller.readUnitLoad = w.model.isSingleUnitLoad;
                retry = false;
                                
            } catch (Throwable ex) {
                ExceptionAnnotator.annotate(ex);
                retry = true;
            } finally {
                CursorControl.showNormalCursor();
            }
        } while(retry);
        
        // Try to set the Cursor to the position where it was before
        try {
            if(node != null ) {
                Node[] newNodes = adviceManager.getRootContext().getChildren().getNodes();
                if( newNodes != null ) {
                    for( int i = 0; i < newNodes.length; i++ ) {
                        Node newNode = newNodes[i];
                        if( newNode.getName().equals(node.getName()) ) {
                            adviceManager.setSelectedNodes(new Node[]{newNode});
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // cannot do anything
            logger.severe(ex.getMessage());
        }
    }

    @Override
    protected void removePositionButtonActionPerformedListener(ActionEvent evt) {
        
        // Try to set the Cursor to the position where it was before
        Node node = null;
        Node[] advices = adviceManager.getSelectedNodes();
        if (advices != null && advices.length > 0){
            if (advices[0] instanceof BOLOSAdviceMasterNode){
                node = advices[0];
            }
        }


        for (Node n : wePosManager.getSelectedNodes()){
            try {
                LOSGoodsReceiptPosition p = (LOSGoodsReceiptPosition) ((GoodsReceiptPositiontNode)n).pos;
                topComponentPanel.controller.removePosition(p);

            } catch (Throwable ex) {
                ExceptionAnnotator.annotate(ex);
            }
        }
        

        // Try to set the Cursor to the position where it was before
        try {
            if(node != null ) {
                Node[] newNodes = adviceManager.getRootContext().getChildren().getNodes();
                if( newNodes != null ) {
                    for( int i = 0; i < newNodes.length; i++ ) {
                        Node newNode = newNodes[i];
                        if( newNode.getName().equals(node.getName()) ) {
                            adviceManager.setSelectedNodes(new Node[]{newNode});
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // cannot do anything
            logger.severe(ex.getMessage());
        }

    }

    @Override
    protected void removeAdviceButtonActionPerformedListener(ActionEvent evt) {
     
        for (Node n : adviceManager.getSelectedNodes()){
            try {
                LOSAdviceTO adv = (LOSAdviceTO) ((BOLOSAdviceMasterNode)n).getEntity();
                topComponentPanel.controller.removeAssignedAdvice(adv);

            } catch (Throwable ex) {
                ExceptionAnnotator.annotate(ex);
            }
        }
    }

    @Override
    protected void createButtonActionPerformedListener(ActionEvent evt) {
        try {

            CreateWizard w = new CreateWizard(this.topComponentPanel.controller);
            w.client = myClient;
            w.gate = defaultLocation;

            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);

            if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                CursorControl.showWaitCursor();
                                
                topComponentPanel.controller.createGoodsReceipt(
                        w.client,
                        "", "",
                        w.deliverer,
                        w.externNumber,
                        w.date,
                        w.gate,
                        w.info);
                
                // Do not change the client of the user
                //myClient = w.client;
            }

            checkState_AllButtons();
            
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }

    }

    @Override
    protected void editButtonActionPerformedListener(ActionEvent evt) {
        try {
            LOSGoodsReceipt r = topComponentPanel.controller.goodsReceiptNode.getGoodsReceipt();
            
            if (r == null) return;
            
            CreateWizard w = new CreateWizard(this.topComponentPanel.controller);
            w.client = new BODTO<Client>(r.getClient().getId(), r.getVersion(), r.getClient().toUniqueString());
            w.allowChangeOfClient = false;
            w.date = r.getReceiptDate();
            w.deliverer = r.getForwarder();
            w.gate = new BODTO<LOSStorageLocation>(r.getGoodsInLocation().getId(), r.getGoodsInLocation().getVersion(), r.getGoodsInLocation().toUniqueString());
            w.externNumber = r.getDeliveryNoteNumber();
            w.info = r.getAdditionalContent();
            
            Dialog d = DialogDisplayer.getDefault().createDialog(w);
            d.setVisible(true);

            if (w.getValue().equals(NotifyDescriptor.OK_OPTION)) {
                CursorControl.showWaitCursor();
                topComponentPanel.controller.edit(
                        w.client,
                        "", "",
                        w.deliverer,
                        w.externNumber,
                        w.date,
                        w.gate,
                        w.info);
            }

            checkState_AllButtons();
            
        } catch (Throwable ex) {
            ExceptionAnnotator.annotate(ex);
        } finally {
            CursorControl.showNormalCursor();
        }
    }

    @Override
    protected void reloadButtonActionPerformedListener(ActionEvent evt) {
        topComponentPanel.controller.reload();
        
        checkState_AllButtons();
    }

    @Override
    protected void goodsReceiptChange(PropertyChangeEvent evt) {
        topComponentPanel.controller.onGoodsReceiptChanged(evt);
        
        checkState_AllButtons();
    }

    @Override
    protected void finishButtonActionPerformedListener(ActionEvent evt) {
        topComponentPanel.controller.finishGoodsReceipt();
    }
    
    public void componentClosed() {
        //clear();
    }

    public void componentActivated() {
    }

    public void componentDeactivated() {
    }

    public void componentHidden() {
    }

    public void componentShowing() {
    }

    @Override
    protected void printButtonActionPerformedListener(ActionEvent evt) {
        topComponentPanel.controller.printGoodsReceipt();
    }
}
