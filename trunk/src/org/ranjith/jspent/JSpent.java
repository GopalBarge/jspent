package org.ranjith.jspent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.ranjith.jspent.action.AddNewActionListener;
import org.ranjith.jspent.action.BackActionListener;
import org.ranjith.jspent.action.DeleteActionListener;
import org.ranjith.jspent.action.ModifyActionListener;
import org.ranjith.jspent.action.MonthYearChangeActionListener;
import org.ranjith.jspent.action.OptionSelectedActionListener;
import org.ranjith.jspent.action.RowSelectionActionListener;
import org.ranjith.jspent.action.SaveExpenseActionListener;
import org.ranjith.jspent.action.SavingsTypeListener;
import org.ranjith.jspent.data.Expense;
import org.ranjith.jspent.data.ExpenseService;
import org.ranjith.plugin.PluginInfo;
import org.ranjith.plugin.PluginManager;
import org.ranjith.swing.EmbossedLabel;
import org.ranjith.swing.GlassToolBar;
import org.ranjith.swing.IconLabelListCellRenderer;
import org.ranjith.swing.IconListItem;
import org.ranjith.swing.MonthSpinnerPanel;
import org.ranjith.swing.QTable;
import org.ranjith.swing.QTableModel;
import org.ranjith.swing.RoundButton;
import org.ranjith.swing.SimpleRoundComboBox;
import org.ranjith.swing.SimpleGradientPanel;
import org.ranjith.swing.SimpleRoundSpinner;
import org.ranjith.swing.SwingRConstants;
import org.ranjith.swing.ToolBarButton;

import sun.swing.SwingUtilities2;

/*
 * Main application class.
 *  $Id:$
 */
public class JSpent extends JFrame {
    private QTable table = null;
    private JSplitPane splitPane;
    ToolBarButton addButton = new ToolBarButton(0);
    ToolBarButton modifyButton = new ToolBarButton(1);
    ToolBarButton deleteButton = new ToolBarButton(2);
    private JList optionsList;
    private JPanel filterPanel;
    JScrollPane tableScrollPane;
    private static Map savingsPluginMap = new HashMap(1);
    private static PluginManager pm = PluginManager.getInstance();
    
    public static final String EXPENSES = "Expenses";
    public static final String INCOMES = "Incomes";
    public static final String SAVINGS = "Savings";
    public static final String LIABILITIES = "Liabilities";
    public static final String SUMMARY = "Summary";
    private UIFactory uiFactory;
    //savings form
    private JPanel centerPanel,buttonPanel;
    SimpleGradientPanel addSavingsForm;
    EmbossedLabel totalLabel;
 
    /**
     * Creates default application instance.
     */
    public JSpent() {
        super("jSpent - very very early stages");
        this.table = new QTable();
        uiFactory = UIFactory.getInstance(this);
        
        getContentPane().setLayout(new BorderLayout());
        JScrollPane categoryScrollPane = getOptionsPane();
        splitPane = getSplitPane(getTablePane(table), categoryScrollPane);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(getBottomPanel(),BorderLayout.PAGE_END);
        SimpleGradientPanel topGradientPanel = getTopPanel();
        getContentPane().add(topGradientPanel,BorderLayout.PAGE_START);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Start with expense always selected.
        optionsList.setSelectedIndex(0);
        optionsList.requestFocusInWindow();
        setSize(800, 640);
    }
    
    private SimpleGradientPanel getTopPanel() {
        SimpleGradientPanel topGradientPanel = new SimpleGradientPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        topGradientPanel.setLayout(gridBagLayout);
        
        GlassToolBar toolBar = getToolBar();
        
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.weightx = 0.5;
        gbConstraints.anchor = GridBagConstraints.PAGE_START;
        topGradientPanel.add(toolBar,gbConstraints);
        JPanel spinnerPanel = new JPanel();
        spinnerPanel.setLayout(new BorderLayout());
        spinnerPanel.setOpaque(false);
        SimpleRoundSpinner monthYearSpinner = getMonthYearSpinner();
        spinnerPanel.add(monthYearSpinner,BorderLayout.CENTER);
        spinnerPanel.add(new JLabel(" "),BorderLayout.LINE_END); //spacer
        EmbossedLabel msgLabel = new EmbossedLabel("Expenses for :",EmbossedLabel.TRAILING);
        msgLabel.setFont(SwingRConstants.DEFAULT_TEXT_FONT);   
        
        filterPanel = new JPanel(new BorderLayout());
        filterPanel.setOpaque(false);
        filterPanel.add(msgLabel,BorderLayout.CENTER);
        filterPanel.add(spinnerPanel,BorderLayout.LINE_END);
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.gridx = 2;
        gbConstraints.gridy = 0;
        gbConstraints.anchor = GridBagConstraints.PAGE_END;
        topGradientPanel.add(filterPanel,gbConstraints);
        topGradientPanel.setBorder(SwingRConstants.EMPTY_BORDER);
        return topGradientPanel;
    }

	private SimpleRoundSpinner getMonthYearSpinner() {
		SpinnerDateModel dateModel = new SpinnerDateModel();
        SimpleRoundSpinner monthYearSpinner = new SimpleRoundSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(monthYearSpinner,"MMMMM, yyyy");
        monthYearSpinner.setEditor(dateEditor);
        monthYearSpinner.setFont(SwingRConstants.DEFAULT_TEXT_FONT);
        monthYearSpinner.setOpaque(false);
        monthYearSpinner.addChangeListener(new MonthYearChangeActionListener(this));
        JSpinner.DefaultEditor editor = (DefaultEditor) monthYearSpinner.getEditor();
        editor.getTextField().setEditable(false);
		return monthYearSpinner;
	}

    private GlassToolBar getToolBar() {
        GlassToolBar toolBar = new GlassToolBar();
        URL resource = JSpent.class.getResource("icons/add.png");
        addButton.setIcon(new ImageIcon(resource,"Add New"));
        addButton.addActionListener(new AddNewActionListener(this));
        resource = JSpent.class.getResource("icons/application_form_add.png");
        modifyButton.setIcon(new ImageIcon(resource,"Modify"));
        modifyButton.addActionListener(new ModifyActionListener(this));
        resource = JSpent.class.getResource("icons/delete.png");
        deleteButton.setIcon(new ImageIcon(resource,"Delete"));
        deleteButton.addActionListener(new DeleteActionListener(this));
        //Not enabled on start up. Enable only when table row is selected.
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        toolBar.add(addButton);
        toolBar.add(modifyButton);
        toolBar.add(deleteButton);
        
        return toolBar;
    }

    private JSplitPane getSplitPane(JComponent scrollPane, JComponent categoryScrollPane) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(1);
        splitPane.setDividerLocation(160);
        splitPane.add(scrollPane, JSplitPane.RIGHT);
        splitPane.add(categoryScrollPane, JSplitPane.LEFT);
        splitPane.setBorder(SwingRConstants.EMPTY_BORDER);
        return splitPane;
    }

    private JPanel getTablePane(QTable table) {
        this.table = table;
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(SwingRConstants.EMPTY_BORDER);
        rightPanel.add(tableScrollPane,BorderLayout.CENTER);
        rightPanel.setBorder(SwingRConstants.EMPTY_BORDER);
        return rightPanel;
    }

	private Component getBottomPanel() {
	    GridBagLayout gridBagLayout = new GridBagLayout();
        SimpleGradientPanel bottomPanel = new SimpleGradientPanel();       
        bottomPanel.setLayout(gridBagLayout);
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;
        gbConstraints.anchor = GridBagConstraints.CENTER;
        totalLabel = new EmbossedLabel("");
        totalLabel.setFont(SwingRConstants.DEFAULT_TEXT_FONT);
        bottomPanel.add(totalLabel,gbConstraints);
        bottomPanel.setBorder(SwingRConstants.EMPTY_BORDER);
        return bottomPanel;
    }
	
	public void setTotal() {
	    if(getCurrentContext().equals(EXPENSES)) {
	        totalLabel.setText( getTotalExpense());
	    }
	}

    private String getTotalExpense() {
        return "Total Expenses " +
                NumberFormat.getCurrencyInstance().format(table.getQTableModel().sum(3)) + " ";
    }

    private JScrollPane getOptionsPane() {
        DefaultListModel listModel = new DefaultListModel();
        optionsList = new JList(listModel);
        optionsList.setCellRenderer(new IconLabelListCellRenderer(1));
        
        URL resource = JSpent.class.getResource("icons/money_delete.png");
        listModel.addElement(new IconListItem(new ImageIcon(resource),EXPENSES));

        resource = JSpent.class.getResource("icons/money_add.png");
        listModel.addElement(new IconListItem(new ImageIcon(resource),INCOMES));

        resource = JSpent.class.getResource("icons/money.png");
        listModel.addElement(new IconListItem(new ImageIcon(resource),SAVINGS));

        resource = JSpent.class.getResource("icons/creditcards.png");
        listModel.addElement(new IconListItem(new ImageIcon(resource),LIABILITIES));
        
        resource = JSpent.class.getResource("icons/report.png");
        listModel.addElement(new IconListItem(new ImageIcon(resource),SUMMARY));

        optionsList.setBackground(SwingRConstants.PANEL_DEEP_BACKGROUND_COLOR);
        optionsList.addListSelectionListener(new OptionSelectedActionListener(this));
        JScrollPane categoryScrollPane = new JScrollPane(optionsList);
        JViewport colHeaderViewPort = new JViewport();
        colHeaderViewPort.setView(getHeader());
                
        categoryScrollPane.setColumnHeader(colHeaderViewPort);
        //XXX
        categoryScrollPane.setBorder(SwingRConstants.EMPTY_BORDER);
        return categoryScrollPane;
    }

    private Component getHeader() {
        EmbossedLabel label = new EmbossedLabel("CATEGORY");
        label.setForeground(new Color(0x505D6D));
        label.setFont(SwingRConstants.DEFAULT_HEADER_FONT);
        label.setOpaque(true);
        label.setBackground(SwingRConstants.PANEL_DEEP_BACKGROUND_COLOR);
        return label;
    }
    
    private List getExpenses(int month) {
        return new ExpenseService().getExpenses(month);
    }
    
    private void prepareUIForForm() {
    	setAddToolBarButtonEnabled(false);
    	setModfyToolBarButtonEnabled(false);
    	setDeleteToolBarButtonEnabled(false);
    	filterPanel.setVisible(false);
    	optionsList.setEnabled(false);
    }
    
    /**
     * This needs to be called when we have to add a new
     * expense data.
     */
    public void showExpenseForm() {
        showExpenseForm(null);
    }

    /**
     * This needs to be called when we have to edit an
     * expense data.
     */
    public void showExpenseForm(Expense expense) {
        prepareUIForForm();
        ExpenseFormPanel panel = expense == null? new ExpenseFormPanel():new ExpenseFormPanel(expense);
        updateRightPane(panel);
        panel.setDoneButtonListener(new BackActionListener(this,panel));
        int mode = (expense == null?SaveExpenseActionListener.ADD_NEW_MODE : SaveExpenseActionListener.UPDATE_MODE);
        panel.setSaveButtonListener(new SaveExpenseActionListener(mode,panel,this));
    }

    private void updateRightPane(JPanel panel) {
        splitPane.setRightComponent(panel);
        splitPane.setDividerLocation(160);
    }    
    
    public void showAddSavings() {
    	prepareUIForForm();
        addSavingsForm = new SimpleGradientPanel(new Color(0x505866),new Color(0x7B8596));

        JPanel typeComboPanel = new JPanel();
        centerPanel = new JPanel();
        buttonPanel = new JPanel();
        
        GroupLayout gl = new GroupLayout(addSavingsForm);
        addSavingsForm.setLayout(gl);
        gl.setHorizontalGroup(
                gl.createSequentialGroup().addGroup(
                gl.createParallelGroup().addComponent(typeComboPanel).addComponent(centerPanel).addComponent(buttonPanel)
                )
                );
       gl.setVerticalGroup(
               gl.createParallelGroup().addComponent(typeComboPanel).addComponent(centerPanel).addComponent(buttonPanel)
               );
        typeComboPanel.setOpaque(false);
        JLabel label1 = new JLabel("Please Choose a Savings type to begin :");
        //label1.setForeground(Color.WHITE);
        typeComboPanel.add(label1);
        List pluginList = pm.getPluginInfoList(PluginManager.PLUGIN_TYPE_SAVINGS_KEY);
        
        SimpleRoundComboBox savingsTypeCombo = new SimpleRoundComboBox();
        savingsTypeCombo.addItem("");
        savingsTypeCombo.setFont(SwingRConstants.DEFAULT_TEXT_FONT);
        for (Iterator iterator = pluginList.iterator(); iterator.hasNext();) {
            PluginInfo plugin = (PluginInfo) iterator.next();
            savingsTypeCombo.addItem(plugin);
        }        
        
        //savingsTypeCombo.addActionListener(new SavingsTypeListener(this,pluginList));
        
        typeComboPanel.add(savingsTypeCombo);
        RoundButton cancelButton = new RoundButton("Cancel");
        cancelButton.addActionListener(new BackActionListener(this));
        typeComboPanel.add(cancelButton);
        
        splitPane.setRightComponent(addSavingsForm);
        splitPane.setDividerLocation(160);
    }
    
    public void restoreUI() {
        if(getCurrentContext().equals(EXPENSES)) {
            setRightTable(uiFactory.createExpenseTableForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1));
            if(table.getSelectedRow() < 0) {
                setModfyToolBarButtonEnabled(false);
                setDeleteToolBarButtonEnabled(false);
            }
            setAddToolBarButtonEnabled(true);
            filterPanel.setVisible(true);
            splitPane.setDividerLocation(160);
            optionsList.setEnabled(true);
            setTotal();
        }
    }
    
    /**
     * Sets the main table in the application.
     * @param table
     */
    public void setRightTable(QTable table) {
       updateRightPane(getTablePane(table));
       SwingUtilities.updateComponentTreeUI(tableScrollPane);
    }

    public void setForm(JComponent component) {
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(component,BorderLayout.CENTER);
       
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(new RoundButton("TEsting out"),BorderLayout.NORTH);
        
        addSavingsForm.add(centerPanel,BorderLayout.CENTER);
        addSavingsForm.updateUI();
    }
    
    public String getCurrentContext() {
    	Object selectedObject = optionsList.getSelectedValue();
    	return ((IconListItem)selectedObject).getText();
    }
    
    public void setCurrentContext(String context) {
    	if(context.equals(EXPENSES)) {
    	  this.optionsList.setSelectedIndex(0);
    	}else if(context.equals(INCOMES)) {
    		this.optionsList.setSelectedIndex(1);	
    	}else if(context.equals(SAVINGS)) {
    		this.optionsList.setSelectedIndex(2);	
    	}else if(context.equals(LIABILITIES)) {
    		this.optionsList.setSelectedIndex(3);		
    	}else if(context.equals(SUMMARY)){
    		this.optionsList.setSelectedIndex(4);		
    	}
    }
    
    public void setModfyToolBarButtonEnabled(boolean isEnabled) {
        this.modifyButton.setEnabled(isEnabled);
    }
    
    public void setAddToolBarButtonEnabled(boolean isEnabled) {
        this.addButton.setEnabled(isEnabled);
    }
    
    public void setDeleteToolBarButtonEnabled(boolean isEnabled) {
        this.deleteButton.setEnabled(isEnabled);
    }

    public Object getSelectedRowObject() {
        QTableModel model = (QTableModel) table.getModel();
        return model.getRows().get(table.getSelectedRow());
    } 
    
    public void clearTableSelection() {
        table.getSelectionModel().clearSelection();
    } 
    
    /**
     * updates expense table display for given month
     * @param month 1..12 indicating month
     */
    public void updateExpenseTableForMonth(int month) {
        QTableModel model = (QTableModel)table.getModel();
        model.setRows(getExpenses(month));
        model.fireTableDataChanged();
    }
    

    /**
     * Removes selected row from the table. But this would not make 
     * changes to underlying datastore.
     */
    public void removeSelectedRowObject() {
        if(table.getSelectedRow() > -1) {
            QTableModel model = (QTableModel) table.getModel();
            model.removeRow(table.getSelectedRow());
        }
    }   
    public static void main(String[] args) {
        JSpent frame = new JSpent();
        frame.setVisible(true);
    }
    



}