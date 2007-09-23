/**
 * 
 */
package org.ranjith.jspent.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.apache.commons.beanutils.BeanUtils;
import org.ranjith.jspent.JSpent;
import org.ranjith.plugin.PluginInfo;
import org.ranjith.plugin.SavingsPlugin;

/**
 * @author ranjith
 * 
 */
public class SavingsTypeListener implements ActionListener {
    private JSpent testFrame;
    private List pluginList;

    public SavingsTypeListener(JSpent testFrame, List pluginList) {
        this.testFrame = testFrame;
        this.pluginList = pluginList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox) e.getSource();
        if (comboBox.getSelectedItem() instanceof PluginInfo) {
            PluginInfo plugin = (PluginInfo) comboBox.getSelectedItem();
            try {
                Class clazz = Class.forName(plugin.getClassName());
                Object pluginObject = clazz.newInstance();
                SavingsPlugin savingsPlugin = (SavingsPlugin) pluginObject;
                testFrame.setForm(savingsPlugin.getAddUI());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}