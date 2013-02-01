/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.wmsprocesses.processes.goodsreceipt.gui.component;

import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBox;
import de.linogistix.common.gui.component.controls.BOAutoFilteringComboBoxModel;
import de.linogistix.common.gui.component.controls.LOSNumericFormattedTextField;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.gui.component.controls.ItemDataComboBoxModel;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.wmsprocesses.processes.goodsreceipt.gui.gui_builder.AbstractPositionWizardSUPanelUI;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.mywms.model.ItemData;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author trautm
 */
public class PositionWizardSUPanelUI extends AbstractPositionWizardSUPanelUI 
                                     implements PropertyChangeListener
{

    private static final Logger log = Logger.getLogger(PositionWizardSUPanelUI.class.getName());
    PropertyChangeListener delegateTo;
    private BOAutoFilteringComboBox<LOSAdvice> adviceComboBox = null;
    private BOAutoFilteringComboBox<ItemData> itemDataComboBox = null;
    private JLabel itemDataNameLabel = new JLabel();
    private LOSGoodsReceipt gr;

    public PositionWizardSUPanelUI(PropertyChangeListener delegateTo) {
        super();
        this.delegateTo = delegateTo;
    }

    protected void postInit() {

        getAmountTextField().setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizardSUPanelUI.Amount"));
        getAmountTextField().setMandatory(true);
        getAmountTextField().setMinimumValue(BigDecimal.ZERO, false);
        getAmountTextField().addItemChangeListener(this);
        
        getUnitLoadAmountTextField().setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "PositionWizardSUPanelUI.UnitLoadAmount"));
        getUnitLoadAmountTextField().setMinimumValue(BigDecimal.ZERO, false);
    }

    public void clear() {
        itemDataNameLabel.setText(" ");
        if (getItemDataComboBox() != null) {
            getItemDataComboBox().clear();
        }
        if (getAdviceComboBox() != null) {
            getAdviceComboBox().clear();
        }

        getAmountTextField().setValue(new BigDecimal(0));

    }

    //------------------------------------------------------------------------
    protected BOAutoFilteringComboBox<LOSAdvice> getAdviceComboBox() {

        if (adviceComboBox == null) {
            adviceComboBox = new BOAutoFilteringComboBox<LOSAdvice>(LOSAdvice.class);
            adviceComboBox.getEditorLabel().setTitleText(NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "LOSAdvice"));
            adviceComboBoxPanel.add(adviceComboBox, BorderLayout.CENTER);
            try {
                adviceComboBox.setComboBoxModel(new AssignedAdviceComboBoxModel(gr));
            } catch (Exception ex) {
                ExceptionAnnotator.annotate(ex);
            }
            adviceComboBox.addItemChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    adviceChanged(evt);
                }
            });

        }

        return adviceComboBox;
    }

    //------------------------------------------------------------------------
    protected BOAutoFilteringComboBox<ItemData> getItemDataComboBox() {

        if (itemDataComboBox == null) {

            itemDataComboBox = new BOAutoFilteringComboBox<ItemData>(ItemData.class);
            itemDataComboBox.getEditorLabel().setTitleText(NbBundle.getMessage(de.linogistix.common.res.CommonBundleResolver.class, "ItemData"));
            itemDataComboBoxPanel.add(itemDataComboBox, BorderLayout.CENTER);
            itemDataComboBoxPanel.add(itemDataNameLabel, BorderLayout.SOUTH);
            try {
                itemDataComboBox.setComboBoxModel(new ItemDataComboBoxModel());
            } catch (Exception ex) {
                ExceptionAnnotator.annotate(ex);
            }
            itemDataComboBox.addItemChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    itemDataChanged(evt);
                }
            });
        }

        return itemDataComboBox;
    }

    //------------------------------------------------------------------------
    protected LOSNumericFormattedTextField getAmountTextField() {
        return (LOSNumericFormattedTextField) amountTextField;
    }

    public void itemDataChanged(PropertyChangeEvent evt) {



        ItemData selItemData = getItemDataComboBox().getSelectedAsEntity();

        if (selItemData != null) {
            getAmountTextField().setEnabled(true);
            getAmountTextField().setUnitName(selItemData.getHandlingUnit().getUnitName());
            getAmountTextField().setScale(selItemData.getScale());
        } else {
            getAmountTextField().setEnabled(false);
        }

        if (delegateTo != null) {
            delegateTo.propertyChange(evt);
        }


    }

    public void adviceChanged(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                initCombosFromAdvice();
            }
        });
    }

    public void initCombosFromAdvice() {

        LOSAdvice advice = getAdviceComboBox().getSelectedAsEntity();
        BODTO<LOSAdvice> adviceTO = getAdviceComboBox().getSelectedItem();
        ItemDataComboBoxModel itemComboModel = (ItemDataComboBoxModel) getItemDataComboBox().getComboBoxModel();
        // if user clears advice combo box
        if (advice == null || adviceTO == null) {
            itemComboModel.setSingleResult(null);
            log.info("*** " + "null");
            return;
        }

        // preset item data
        getItemDataComboBox().clear();

        ItemData item = advice.getItemData();
        BODTO<ItemData> itemTO = new BODTO<ItemData>(item.getId(), item.getVersion(), item.getNumber());
        itemComboModel.setSingleResult(itemTO);
        
        getItemDataComboBox().getAutoFilteringComboBox().addItem(itemTO.getName());
        itemDataNameLabel.setText(item.getName());

        log.info("*** added " + itemTO);

        ((LOSNumericFormattedTextField) amountTextField).setEnabled(true);
        ((LOSNumericFormattedTextField) amountTextField).setUnitName(item.getHandlingUnit().getUnitName());
        ((LOSNumericFormattedTextField) amountTextField).setScale(item.getScale());

        if (delegateTo != null) {
            delegateTo.propertyChange(null);
        }
    }

    void initValues(PositionWizardModel wm) {
                
        clear();

        // if an advice is selected, init from advice
        if (wm.selectedAdvice != null) {

//            getAdviceComboBox().addItem(wm.selectedAdviceTO);
            getAdviceComboBox().getAutoFilteringComboBox().addItem(wm.selectedAdviceTO.getName());
            getAdviceComboBox().getComboBoxModel().setSingleResult(wm.selectedAdviceTO);
            getAdviceComboBox().setEnabled(false);

            ItemData item = wm.selectedAdvice.getItemData();
            BODTO<ItemData> itemTO = new BODTO<ItemData>(item.getId(), 
                                                         item.getVersion(), 
                                                         item.getNumber());

//            getItemDataComboBox().addItem(itemTO);
            getItemDataComboBox().getAutoFilteringComboBox().addItem(itemTO.getName());
            getItemDataComboBox().getComboBoxModel().setSingleResult(itemTO);
            getItemDataComboBox().setEnabled(false);
            itemDataNameLabel.setText(item.getName());

            getAmountTextField().setScale(item.getScale());
            getAmountTextField().setUnitName(item.getHandlingUnit().getUnitName());
        }
        // else init from client
        else{
            getItemDataComboBox().setEnabled(true);
            getAdviceComboBox().setEnabled(true);
            ((ItemDataComboBoxModel)getItemDataComboBox().getComboBoxModel()).setClientTO(wm.client);
        }

        if (wm.amount != null) {
            getAmountTextField().setValue(wm.amount);
        }
        else{
            getAmountTextField().setValue(BigDecimal.ZERO);
        }
        
        if(wm.isSingleUnitLoad){
            getUnitLoadAmountTextField().setValue(new BigDecimal(1));
            getUnitLoadAmountTextField().setEnabled(false);
        }
        else{
            getUnitLoadAmountTextField().setValue(new BigDecimal(wm.sameCount));
            getUnitLoadAmountTextField().setEnabled(true);
        }

        if (delegateTo != null) {
            delegateTo.propertyChange(null);
        }
    }

    void setGoodsREceipt(LOSGoodsReceipt gr) {
        if (this.gr == null) {
            this.gr = gr;
            postInit();
        }
    }

    //------------------------------------------------------------------------
    public boolean validateItemData() {
        return getItemDataComboBox().getSelectedItem() != null;
    }
    
    public boolean validateAmount(){
        
        if(getAmountTextField().checkSanity()
           && getUnitLoadAmountTextField().checkSanity())
        {
            return true;
        }
        else{
            return false;
        }
    }

    static class AssignedAdviceComboBoxModel extends BOAutoFilteringComboBoxModel<LOSAdvice> {

        BODTO<LOSGoodsReceipt> gr;
        LOSAdviceQueryRemote advQuery;

        public AssignedAdviceComboBoxModel(LOSGoodsReceipt gr) throws Exception {
            super(LOSAdvice.class);
            if (gr == null) {
                throw new NullPointerException();
            }
            this.gr = new BODTO<LOSGoodsReceipt>(gr.getId(), gr.getVersion(), gr.toUniqueString());
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            this.advQuery = loc.getStateless(LOSAdviceQueryRemote.class);
        }

        @Override
        public LOSResultList<BODTO<LOSAdvice>> getResults(String searchString, QueryDetail detail) {
            return advQuery.queryByAssingigGoodsReceipt(gr, detail);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        delegateTo.propertyChange(evt);
    }
}
