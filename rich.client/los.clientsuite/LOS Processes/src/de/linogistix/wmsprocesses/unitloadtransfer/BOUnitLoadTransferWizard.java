/*
 * OrderByWizard.java
 *
 * Created on 27. Juli 2006, 00:27
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.wmsprocesses.unitloadtransfer;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.CursorControl;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;
import de.linogistix.wmsprocesses.res.WMSProcessesBundleResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * A Wizard for creating new BusinessObjects.
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOUnitLoadTransferWizard extends WizardDescriptor implements ActionListener, ChangeListener {

    private static final Logger log = Logger.getLogger(BOUnitLoadTransferWizard.class.getName());
    
    private BODTO<LOSUnitLoad> unitLoadTO;
    
    private BODTO<LOSStorageLocation> storageLocationTO;

    private String hint;

    private boolean ignoreLock;
    
    /**
     * Creates a new instance of OrderByWizard
     */
    @SuppressWarnings("unchecked")
    public BOUnitLoadTransferWizard(BODTO unitLoadTo) throws InstantiationException {
        super(createPanels());

        putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
        putProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
        putProperty("WizardPanel_contentData", getContentData());
        putProperty("WizardPanel_contentNumbered", Boolean.TRUE);
        putProperty("WizardPanel_image", Utilities.loadImage("de/linogistix/wmsprocesses/res/img/TransferUnitLoad.png"));
        putProperty("WizardDescriptor.setTitle", NbBundle.getMessage(WMSProcessesBundleResolver.class, "transferUnitLoad"));
        setTitle(NbBundle.getMessage(WMSProcessesBundleResolver.class, "transferUnitLoad"));
        
        setHelpCtx(new HelpCtx("de.linogistix.wmsprocesses.unitloadtransfer"));

        CursorControl.showWaitCursor();
        try {
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            if (unitLoadTo != null) {
                setUnitLoadTO(unitLoadTo);
            }
        } catch (Throwable t) {
            ExceptionAnnotator.annotate(t);
        } finally {
            CursorControl.showNormalCursor();
        }

        setButtonListener(this);

    }

    //-------------------------------------------------------------------------------
    public final static Panel[] createPanels() throws InstantiationException {
        List<Panel> panels = new ArrayList<Panel>();

        ValidatingPanel p1 = new BOUnitLoadChooseDestinationPanel();
        panels.add(p1);

        FinishablePanel p2 = new BOUnitLoadHintPanel();   
        panels.add(p2);
        
        return (Panel[]) panels.toArray(new Panel[0]);
    }

    public void stateChanged(ChangeEvent e) {
        putProperty("WizardPanel_errorMessage", null);
        updateState();
    }

    public JButton getFinishOption() {
        for (Object o : getClosingOptions()) {
            if (o instanceof JButton) {
                JButton b = (JButton) o;
                return b;
            }
        }

        return null;
    }
    //-----------------------------------------------------------

    public void actionPerformed(ActionEvent e) {
        //
    }

    public BODTO<LOSUnitLoad> getUnitLoadTO() {
        return unitLoadTO;
    }

    public void setUnitLoadTO(BODTO<LOSUnitLoad> unitLoadTO) {
        this.unitLoadTO = unitLoadTO;
    }

    public BODTO<LOSStorageLocation> getStorageLocationTO() {
        return storageLocationTO;
    }

    public void setStorageLocationTO(BODTO<LOSStorageLocation> storageLocationTO) {
        this.storageLocationTO = storageLocationTO;
    }

    String getHint() {
        return this.hint;
    }

    void setHint(String hint) {
        this.hint = hint;
    }

    private String[] getContentData() {
       return new String[]{
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOUnitLoadChooseDestinationPanel.contentData"),
        NbBundle.getMessage(WMSProcessesBundleResolver.class, "BOUnitLoadHintPanel.contentData"),
       
       };
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("de.linogistix.wmsprocesses.unitloadtransfer");
    }

    public boolean isIgnoreLock() {
        return ignoreLock;
    }

    public void setIgnoreLock(boolean ignoreLock) {
        this.ignoreLock = ignoreLock;
    }
    
    

}


