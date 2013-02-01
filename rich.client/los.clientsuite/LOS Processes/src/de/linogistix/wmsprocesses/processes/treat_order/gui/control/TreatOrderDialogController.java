/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.treat_order.gui.control;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.view.LOSListViewSelectionListener;
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.common.util.GraphicUtil;
import de.linogistix.inventory.gui.component.controls.LOSOrderRequestComboBoxModel;
import de.linogistix.inventory.gui.component.controls.LOSOrderRequestPositionComboBoxModel;
import de.linogistix.location.gui.component.controls.LOSStorageLocationComboBoxModel;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.model.LOSOrderRequest;
import de.linogistix.los.inventory.model.LOSOrderRequestPosition;
import de.linogistix.los.inventory.model.LOSOrderRequestState;
import de.linogistix.los.inventory.pick.facade.PickOrderFacade;
import de.linogistix.los.inventory.query.dto.LOSOrderStockUnitTO;
import de.linogistix.los.inventory.query.dto.LotTO;
import de.linogistix.los.location.model.LOSAreaType;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.processes.treat_order.gui.component.TreatOrderCenterPanel;
import de.linogistix.wmsprocesses.processes.treat_order.gui.model.TreatOrderDialogModel;
import de.linogistix.wmsprocesses.processes.treat_order.gui.model.TreatOrderPickRequestTO;
import de.linogistix.wmsprocesses.processes.treat_order.gui.model.TreatOrderStockSelectionModel;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.model.ItemData;
import org.mywms.model.ItemMeasure;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Jordan
 */
public class TreatOrderDialogController {

    private static final Logger log = Logger.getLogger(TreatOrderDialogController.class.getName());
    
    private BOAutoFilteringComboBox<LOSOrderRequest> orderCombo;
    private BOAutoFilteringComboBox<LOSOrderRequestPosition> orderPositionCombo;
    private BOAutoFilteringComboBox<LOSStorageLocation> targetPlaceCombo;
    
    private TreatOrderStockSelectionModel stockSelectionModel;
    private TreatOrderCenterPanel myCenterPanel;
    private TreatOrderPickRequestTO actuPickRequest = null;
    private TreatOrderDialogModel dialogModel;
    
    private ItemMeasure chosenAmount;

    public TreatOrderDialogController(TreatOrderCenterPanel centerPanel,
                                      BOAutoFilteringComboBox<LOSOrderRequest> orderCombo,
                                      BOAutoFilteringComboBox<LOSOrderRequestPosition> orderPositionCombo,
                                      TreatOrderStockSelectionModel stockSelectionModel,
                                      BOAutoFilteringComboBox<LOSStorageLocation> targetPlaceCombo)
            throws Exception 
    {
        myCenterPanel = centerPanel;

        myCenterPanel.getPickRequestListView().setSingleSelection(true);
        myCenterPanel.getPickRequestListView().addSelectionListener(
                new LOSListViewSelectionListener() {

                    @SuppressWarnings("unchecked")
                    public void selectionChanged(List selectedEntities) {
                        if(selectedEntities.size()>0){
                            pickRequestSelected((TreatOrderPickRequestTO)selectedEntities.get(0));
                        }
                    }
                });

        this.orderCombo = orderCombo;
        LOSOrderRequestComboBoxModel orderModel = new LOSOrderRequestComboBoxModel();
        orderModel.setOrderState(LOSOrderRequestState.RAW);
        this.orderCombo.setComboBoxModel(orderModel);
        this.orderCombo.addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                orderChanged(evt);
            }
        });

        this.orderPositionCombo = orderPositionCombo;
        this.orderPositionCombo.setComboBoxModel(new LOSOrderRequestPositionComboBoxModel());

        this.orderPositionCombo.addItemChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                orderPositionChanged();     
            }
        });
        
        targetPlaceCombo = myCenterPanel.getTargetPlaceComboBox();
        LOSStorageLocationComboBoxModel tpModel = new LOSStorageLocationComboBoxModel();
        tpModel.setAreaType(LOSAreaType.GOODS_OUT);
        targetPlaceCombo.setComboBoxModel(tpModel);

        myCenterPanel.getStockChooserView().setEnabled(false);

        this.stockSelectionModel = stockSelectionModel;
        this.stockSelectionModel.setDialogController(this);
        
        this.targetPlaceCombo = targetPlaceCombo;

        dialogModel = new TreatOrderDialogModel();
    }

    public void clear() {
        
        actuPickRequest = null;
        
        orderPositionCombo.clear();
        
        orderCombo.clear();
        orderCombo.setEnabled(true);
        ((LOSOrderRequestComboBoxModel)orderCombo.getComboBoxModel()).setOrderState(LOSOrderRequestState.RAW);
        
        targetPlaceCombo.clear();
        ((LOSStorageLocationComboBoxModel)targetPlaceCombo.getComboBoxModel()).setAreaType(LOSAreaType.GOODS_OUT);
        
        stockSelectionModel.clear();
        
        myCenterPanel.getPickRequestListView().clear();
        
        myCenterPanel.getChosenAmountLabel().setText("");
        myCenterPanel.getRequiredAmountLabel().setText("");
        
        myCenterPanel.getClientLabel().setText("");
        myCenterPanel.getItemDataLabel().setText("");
        myCenterPanel.getLotLabel().setText("");
        
        myCenterPanel.getPrefixTextField().setText("");
        
        dialogModel.clear();
        
        myCenterPanel.getStockChooserView().setEnabled(false);
        myCenterPanel.getStockChooserView().reload();
        
        myCenterPanel.getCommentArea().setText("");
        myCenterPanel.warningLabel.setIcon(null);
    }

    private void orderChanged(PropertyChangeEvent evt) {

        orderPositionCombo.clear();

        LOSOrderRequest order = orderCombo.getSelectedAsEntity();
        BODTO<LOSOrderRequest> orderTO = orderCombo.getSelectedItem();

        if (orderTO != null && order != null) {
            ((LOSOrderRequestPositionComboBoxModel) orderPositionCombo.getComboBoxModel()).setOrderTO(orderTO);
            orderPositionCombo.setEnabled(true);

            myCenterPanel.clientValueLabel.setText(order.getClient().getName());
            
            if(order.getAdditionalContent() != null && order.getAdditionalContent().length()>0){
                
                myCenterPanel.getCommentArea().setText(order.getAdditionalContent());
                myCenterPanel.getCommentArea().setMargin(new Insets(5, 5, 5, 5));
                
                myCenterPanel.warningLabel.setIcon(GraphicUtil.getInstance().getIcon(IconType.WARNING));
            }
            else{
                myCenterPanel.warningLabel.setIcon(null);
            }
            
            if(order.getDestination() != null){
                
                BODTO<LOSStorageLocation> target;
                target = new BODTO<LOSStorageLocation>(order.getDestination().getId(), 
                                                       order.getDestination().getVersion(), 
                                                       order.getDestination().getName());
                
                targetPlaceCombo.addItem(target);
                targetPlaceCombo.setSelectedItem(target);
            }
            
        } else {
            myCenterPanel.getClientLabel().setText("");
            orderPositionCombo.setEnabled(false);
            myCenterPanel.getCommentArea().setText("");
            myCenterPanel.warningLabel.setIcon(null);
            targetPlaceCombo.clear();
        }

    }

    @SuppressWarnings("unchecked")
    private void orderPositionChanged() {

        LOSOrderRequestPosition pos = orderPositionCombo.getSelectedAsEntity();
        BODTO<LOSOrderRequestPosition> posTO = orderPositionCombo.getSelectedItem();

        if (posTO != null && pos != null) {

            stockSelectionModel.setOrderPositionTO(posTO);
            stockSelectionModel.setItemDataTO(new BODTO<ItemData>(pos.getItemData().getId(), 
                                                                  pos.getItemData().getVersion(), 
                                                                  pos.getItemData().getNumber()));

            myCenterPanel.itemDataValueLabel.setText(pos.getItemData().getNumber());
            
            if(pos.getLot() != null){
                Lot presetLot = pos.getLot();
                myCenterPanel.getLotLabel().setText(presetLot.getName());
                LotTO lotTO = new LotTO(presetLot.getId(), 
                                        presetLot.getVersion(), 
                                        presetLot.getName(), 
                                        presetLot.getItemData().getNumber(),
                                        presetLot.getItemData().getName(),
                                        presetLot.getLock(), 
                                        presetLot.getUseNotBefore(), 
                                        presetLot.getBestBeforeEnd());
                ((TreatOrderStockSelectionModel)myCenterPanel.getStockChooserView().getModel()).setLotTO(lotTO);
            }
            else{
                myCenterPanel.getLotLabel().setText("");
                ((TreatOrderStockSelectionModel)myCenterPanel.getStockChooserView().getModel()).setLotTO(null);
            }

            if (actuPickRequest != null) {
                myCenterPanel.getStockChooserView().setEnabled(true);
            }

            dialogModel.setActuOrderPosition(posTO, pos.getAmount());

            List<BODTO> selPickList = myCenterPanel.getPickRequestListView().getSelectedEntities();

            if (selPickList.size() > 0) {
                List<BODTO<StockUnit>> chosenStockList;
                chosenStockList = dialogModel.getChosenStocks(selPickList.get(0).getName());

                myCenterPanel.getStockChooserView().getModel().clearSelectionList();
                myCenterPanel.getStockChooserView().getModel().setSelectionList(chosenStockList);
            }

            myCenterPanel.getStockChooserView().reload();
            
            ItemMeasure imReq = pos.getDisplayAmount();
            chosenAmount = new ItemMeasure(dialogModel.getChosenAmountByOrderId(pos.getId()), 
                                           imReq.getItemUnit());
            
            myCenterPanel.getRequiredAmountLabel().setText(imReq.toString());
            myCenterPanel.getChosenAmountLabel().setText(chosenAmount.toString());
        }

    }

    @SuppressWarnings("unchecked")
    private void pickRequestSelected(TreatOrderPickRequestTO pickRequest) {
        actuPickRequest = pickRequest;
        if (orderPositionCombo.getSelectedItem() != null) {
            myCenterPanel.getStockChooserView().setEnabled(true);

            List<BODTO<StockUnit>> chosenStockList;
            chosenStockList = dialogModel.getChosenStocks(actuPickRequest.pickRequestNumber);

            myCenterPanel.getStockChooserView().getModel().clearSelectionList();
            myCenterPanel.getStockChooserView().getModel().setSelectionList(chosenStockList);
        }
    }

    public BigDecimal addChosenStock(LOSOrderStockUnitTO selectedStock) {

        BigDecimal chosen = dialogModel.addChosenStock(actuPickRequest, selectedStock);
        
        chosenAmount.setValue(dialogModel.getChosenAmount());
        myCenterPanel.getChosenAmountLabel().setText(chosenAmount.toString());
        
        orderCombo.setEnabled(false);
        
        return chosen;
    }

    @SuppressWarnings("unchecked")
    public void removeChosenStock(LOSOrderStockUnitTO selectedStock) {

       dialogModel.removeChosenStock(selectedStock);
       
        chosenAmount.setValue(dialogModel.getChosenAmount());
        myCenterPanel.getChosenAmountLabel().setText(chosenAmount.toString());
    }

    public void process() {

        try{            
            LOSOrderRequest order = orderCombo.getSelectedAsEntity();
            
            if(dialogModel.getHandledPositions().size() != order.getPositions().size()){
                
                StringBuffer sb = new StringBuffer("\n");
                
                for(LOSOrderRequestPosition pos : order.getPositions()){
                    
                    boolean handled = false;
                    
                    for(BODTO<LOSOrderRequestPosition> handledTO : dialogModel.getHandledPositions()){
                        
                        if(pos.getNumber().equals(handledTO.getName())){
                            handled = true;
                            continue;
                        }
                    }
                    
                    if(!handled){
                        sb.append(pos.getNumber()+"\n");
                    }
                }
                
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ConfirmUnhandled.message", new Object[]{sb.toString()}),
                                        NbBundle.getMessage(WMSProcessesBundleResolver.class,"ConfirmUnhandled.header"),
                                        NotifyDescriptor.YES_NO_OPTION);

                if (DialogDisplayer.getDefault().notify(d) != NotifyDescriptor.YES_OPTION) {
                    return;
                }                
            }          
            
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);        
            PickOrderFacade pickOrderFacade = loc.getStateless(PickOrderFacade.class);
            pickOrderFacade.createPickRequests(dialogModel.getChosenStocks());
            
            clear();
        } catch (Throwable t){
            log.log(Level.INFO,t.getMessage(), t);
            ExceptionAnnotator.annotate(t);
        }
    }
    
    public void reset(){
        
        try{
            dialogModel.clearReservation();
        }catch(InventoryException invex){
            ExceptionAnnotator.annotate(invex);
            return;
        }
        
        clear();
    }
}
