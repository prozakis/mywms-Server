/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.query.gui.gui_builder;

import de.linogistix.common.bobrowser.query.gui.component.ProviderChangeEventListener;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.res.icon.IconResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Icon;

/**
 *
 * @author  trautm
 */
public class AbstractBOQueryQuickSearchPanel extends javax.swing.JPanel {

    private String hint =  org.openide.util.NbBundle.getMessage(CommonBundleResolver.class, "quickSearchHint");

    protected Icon quickSearchIcon = new javax.swing.ImageIcon(
                                    IconResolver.class.getResource(
                                    "/de/linogistix/common/res/icon/Search.png"));
    
    private String lastSearchStr = hint;

    private static final Logger log = Logger.getLogger(AbstractBOQueryQuickSearchPanel.class.getName());
    
    List<ProviderChangeEventListener> listeners = new ArrayList<ProviderChangeEventListener>();

        
    /** Creates new form AbstractBOQueryQuickSearchPanel */
    public AbstractBOQueryQuickSearchPanel() {
        initComponents();
        
        quickSearchLabel.setIcon(quickSearchIcon);
    }

    public void removeProviderChangeEventListeners() {
        listeners = new ArrayList<ProviderChangeEventListener>();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        quickSearchLabel = new javax.swing.JLabel();
        quickSearchtextField = new javax.swing.JTextField();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        add(quickSearchLabel);

        quickSearchtextField.setText(getHint());
        quickSearchtextField.setPreferredSize(new java.awt.Dimension(250, 22));
        quickSearchtextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quickSearchtextFieldActionPerformed(evt);
            }
        });
        quickSearchtextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                quickSearchtextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                quickSearchtextFieldFocusLost(evt);
            }
        });
        quickSearchtextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                quickSearchtextFieldKeyTyped(evt);
            }
        });
        quickSearchtextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                quickSearchtextFieldMouseClicked(evt);
            }
        });
        add(quickSearchtextField);
    }// </editor-fold>//GEN-END:initComponents

private void quickSearchtextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_quickSearchtextFieldMouseClicked

//    if (showingHintText()){
//        quickSearchtextField.setText("");
//    } else{
//        quickSearchtextField.selectAll();
//    }
    
}//GEN-LAST:event_quickSearchtextFieldMouseClicked

private void quickSearchtextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quickSearchtextFieldActionPerformed
    //log.info("inform " + evt.toString() + " with  " + quickSearchtextField.getText());
    for (ProviderChangeEventListener l : listeners){
            this.lastSearchStr = quickSearchtextField.getText();
            l.reloadRequest();
        }

}//GEN-LAST:event_quickSearchtextFieldActionPerformed

private void quickSearchtextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quickSearchtextFieldFocusGained
if (showingHintText()){
        quickSearchtextField.setText("");
    } else{
        quickSearchtextField.selectAll();
    }
}//GEN-LAST:event_quickSearchtextFieldFocusGained

private void quickSearchtextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_quickSearchtextFieldFocusLost

    if (quickSearchtextField.getText() == null || quickSearchtextField.getText().trim().length() == 0){
        log.info("hint quickSearchtextField.setText("+hint+")");
        quickSearchtextField.setText(hint);
    } 
    else if ( ! quickSearchtextField.getText().equals(lastSearchStr)){
        log.info("last quickSearchtextField.setText("+lastSearchStr+")");
        quickSearchtextField.setText(lastSearchStr);
    }
}//GEN-LAST:event_quickSearchtextFieldFocusLost

private void quickSearchtextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_quickSearchtextFieldKeyTyped
    
//    if (evt.getKeyChar() != KeyEvent.VK_ENTER && evt.getKeyChar() != KeyEvent.VK_TAB ){
//        return;
//    }
}//GEN-LAST:event_quickSearchtextFieldKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel quickSearchLabel;
    private javax.swing.JTextField quickSearchtextField;
    // End of variables declaration//GEN-END:variables

    private boolean showingHintText() {
        
        return quickSearchtextField.getText().equals(hint);
                
    }

    public String getHint() {
        return hint;
    }
    
    public String getQuickSearchString(){
        if (showingHintText()){
            return "";
        } else{
            return quickSearchtextField.getText();
        }
    }
    
     public void addProviderChangeEventListener(ProviderChangeEventListener l) {
        this.listeners.add(l);
    }

    public void removeProviderChangeEventListener(ProviderChangeEventListener l) {
        this.listeners.remove(l);
    }

}
