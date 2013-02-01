/*
 * OrderByWizardPanel1.java
 *
 * Created on 27. Juli 2006, 00:46
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.wmsprocesses.stockunittransfer;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.facade.ManageInventoryFacade;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import de.linogistix.wmsprocesses.stockunittransfer.gui.component.ChooseSourceDestinationPanel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import javax.swing.event.ChangeListener;
import org.mywms.facade.FacadeException;
import org.mywms.model.StockUnit;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
final public class BOStockUnitTransferPanelChoose implements WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {

    private BOStockUnitTransferWizard wizard;
    private ChooseSourceDestinationPanel ui;
    private boolean finishPanel = true;
    /** listener to changes in the wizard */
    private ChangeListener listener;

    private ChooseSourceDestinationPanel getPanelUI() {
        if (ui == null) {
            ui = new ChooseSourceDestinationPanel(wizard);
            ui.getUnitLoadAutofilteringComboBox().addItemChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {

                    if (wizard == null) {
                        return;
                    }

                    if (getPanelUI().getUnitLoadAutofilteringComboBox().getSelectedItem() != null) {
                        wizard.setUl((BODTO<LOSUnitLoad>) getPanelUI().getUnitLoadAutofilteringComboBox().getSelectedItem());
                        wizard.setUnitLoad((LOSUnitLoad) getPanelUI().getUnitLoadAutofilteringComboBox().getSelectedAsEntity());
                    }
                    wizard.stateChanged(null);
                    getPanelUI().setAdditionalFields();
                }
            });
            ui.getStockUnitAutoFilteringComboBox().addItemChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {

                    if (wizard == null) {
                        return;
                    }

                    if (getPanelUI().getStockUnitAutoFilteringComboBox().getSelectedItem() != null) {
                        wizard.setSu((BODTO<StockUnit>) getPanelUI().getStockUnitAutoFilteringComboBox().getSelectedItem());
                        wizard.setStockUnit((StockUnit) getPanelUI().getStockUnitAutoFilteringComboBox().getSelectedAsEntity());
                    }
                    getPanelUI().getStockUnitRemoveLockLabel().setText();
                    getPanelUI().getStockUnitRemoveLockLabel().setEnabled(false);
                    getPanelUI().getStockUnitRemoveLockField().setEnabled(false);
                    getPanelUI().getStockUnitRemoveLockField().setSelected(false);
                    if (wizard.getStockUnit() != null && wizard.getStockUnit().isLocked()) {
//                        String s = NbBundle.getMessage(WMSProcessesBundleResolver.class, "suIsLocked");
//                        getPanelUI().getStockUnitRemoveLockLabel().setText(s, IconType.ERROR);
                        getPanelUI().getStockUnitRemoveLockLabel().setEnabled(true);
                        getPanelUI().getStockUnitRemoveLockField().setEnabled(true);
                    }

                    getPanelUI().getStockUnitRemoveReservationLabel().setText();
                    getPanelUI().getStockUnitRemoveReservationLabel().setEnabled(false);
                    getPanelUI().getStockUnitRemoveReservationField().setEnabled(false);
                    getPanelUI().getStockUnitRemoveReservationField().setSelected(false);
                    if (wizard.getStockUnit() != null && wizard.getStockUnit().getReservedAmount().compareTo(new BigDecimal(0)) > 0) {
//                        String s = NbBundle.getMessage(WMSProcessesBundleResolver.class, "suHasReserved");
//                        getPanelUI().getStockUnitRemoveReservationLabel().setText(s, IconType.WARNING);
                        getPanelUI().getStockUnitRemoveReservationLabel().setText();
                        getPanelUI().getStockUnitRemoveReservationLabel().setEnabled(true);
                        getPanelUI().getStockUnitRemoveReservationField().setEnabled(true);
                    }
                    getPanelUI().setAdditionalFields();
                    wizard.stateChanged(null);
                }
            });
        }
        return ui;
    }

    /** Add a listener to changes of the panel's validity.
     * @param l the listener to add
     * @see #isValid
     */
    public void addChangeListener(ChangeListener l) {
        if (listener != null) {
            throw new IllegalStateException();
        }
        listener = l;
    }

    /** Remove a listener to changes of the panel's validity.
     * @param l the listener to remove
     */
    public void removeChangeListener(ChangeListener l) {
        listener = null;

    }

    /** Get the component displayed in this panel.
     *
     * Note; method can be called from any thread, but not concurrently
     * with other methods of this interface.
     *
     * @return the UI component of this wizard panel
     *
     */
    public java.awt.Component getComponent() {
        return getPanelUI();
    }

    /** Help for this panel.
     * @return the help or <code>null</code> if no help is supplied
     */
    public HelpCtx getHelp() {
        return null;
    }

    /** Test whether the panel is finished and it is safe to proceed to the next one.
     * If the panel is valid, the "Next" (or "Finish") button will be enabled.
     * @return <code>true</code> if the user has entered satisfactory information
     */
    public boolean isValid() {

        if (ui == null) {
            return false;
        }

        try {

            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ManageInventoryFacade manageInv = loc.getStateless(ManageInventoryFacade.class);

            if (this.wizard.getSu() == null) {
                throw new WizardValidationException(getPanelUI(), "no stock unit set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStockUnit", new Object[]{}));
            }

            if (this.wizard.getUl() == null) {
                throw new WizardValidationException(getPanelUI(), "no unit load set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noUnitLoad", new Object[]{}));
            }

            if (this.wizard.getUnitLoad().isLocked()) {
                throw new WizardValidationException(getPanelUI(), "Set 'Unlock stock unit' option or choose other unit load",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.unitLoadIsLocked", new Object[]{}));
            }

//            if (wizard.getStockUnit().isLocked() && !getPanelUI().isStockUnitRemoveLockSelected() ) {
//                throw new WizardValidationException(getPanelUI(), "Set 'Unlock stock unit' option or choose other unit load",
//                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.setSuRemoveLockOption", new Object[]{}));
//            }

            try {
                if (!manageInv.testSuitable(wizard.getSu(), wizard.getUl())) {
                    InventoryException ex = new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new Object[]{"", ""});
                    throw new WizardValidationException(getPanelUI(), ex.getMessage(), ex.getLocalizedMessage());
                }
            } catch (FacadeException ex) {
                throw new WizardValidationException(getPanelUI(), ex.getMessage(), ex.getLocalizedMessage());
            }
        } catch (WizardValidationException ex) {

            wizard.putProperty("WizardPanel_errorMessage", ex.getLocalizedMessage());
            return false;
        } catch (FacadeException ex){
            ExceptionAnnotator.annotate(ex);
        }
        return true;
    }

    /** Provides the wizard panel with the current data--either
     * the default data or already-modified settings, if the user used the previous and/or next buttons.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * @param settings the object representing wizard panel state, as originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}
     */
    @SuppressWarnings("unchecked")
    public void readSettings(Object settings) {
        this.wizard = (BOStockUnitTransferWizard) settings;
        getPanelUI().implReadSettings(settings);
        this.wizard = (BOStockUnitTransferWizard) settings;
        if (this.wizard.getSu() != null) {
            getPanelUI().getStockUnitAutoFilteringComboBox().getAutoFilteringComboBox().addItem(wizard.getSu().getName());
        }
    }

    /** Provides the wizard panel with the opportunity to update the
     * settings with its current customized state.
     * Rather than updating its settings with every change in the GUI, it should collect them,
     * and then only save them when requested to by this method.
     * Also, the original settings passed to {@link #readSettings} should not be modified (mutated);
     * rather, the (copy) passed in here should be mutated according to the collected changes.
     * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
     * @param settings the object representing a settings of the wizard
     */
    public void storeSettings(Object settings) {
        this.wizard = (BOStockUnitTransferWizard) settings;
        getPanelUI().implStoreSettings(settings);

    }

    public boolean isFinishPanel() {
        return finishPanel;
    }

    public void setFinishPanel(boolean finishPanel) {
        this.finishPanel = finishPanel;
    }

    public void validate() throws WizardValidationException {

        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            ManageInventoryFacade manageInv = loc.getStateless(ManageInventoryFacade.class);

            if (this.wizard.getSu() == null) {
                throw new WizardValidationException(getPanelUI(), "no stock unit set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noStockUnit", new Object[]{}));
            }

            if (this.wizard.getUl() == null) {
                throw new WizardValidationException(getPanelUI(), "no unit load set",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.noUnitLoad", new Object[]{}));
            }

            if (this.wizard.getUnitLoad().isLocked()) {
                throw new WizardValidationException(getPanelUI(), "Set 'Unlock stock unit' option or choose other unit load",
                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.unitLoadIsLocked", new Object[]{}));
            }

//            if (wizard.getStockUnit().isLocked() && !getPanelUI().isStockUnitRemoveLockSelected()) {
//                throw new WizardValidationException(getPanelUI(), "Set 'Unlock stock unit' option or choose other unit load",
//                        NbBundle.getMessage(WMSProcessesBundleResolver.class, "ERROR.setSuRemoveLockOption", new Object[]{}));
//            }

            if (!manageInv.testSuitable(wizard.getSu(), wizard.getUl())) {
                InventoryException ex = new InventoryException(InventoryExceptionKey.STOCKUNIT_TRANSFER_NOT_ALLOWED, new Object[]{"", ""});
                throw new WizardValidationException(getPanelUI(), ex.getMessage(), ex.getLocalizedMessage());
            }

//            wizard.process();
        } catch (FacadeException ex) {
            throw new WizardValidationException(getPanelUI(), ex.getMessage(), ex.getLocalizedMessage());
        }

    }


}
