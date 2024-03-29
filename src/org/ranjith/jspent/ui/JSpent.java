package org.ranjith.jspent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.ranjith.jspent.Application;
import org.ranjith.jspent.action.AddNewActionListener;
import org.ranjith.jspent.action.BackActionListener;
import org.ranjith.jspent.action.ContextMenuListener;
import org.ranjith.jspent.action.DeleteActionListener;
import org.ranjith.jspent.action.ModifyActionListener;
import org.ranjith.jspent.action.MonthYearChangeActionListener;
import org.ranjith.jspent.action.OptionSelectedActionListener;
import org.ranjith.jspent.action.SaveExpenseActionListener;
import org.ranjith.jspent.data.Expense;
import org.ranjith.jspent.data.ExpenseService;
import org.ranjith.swing.EmbossedLabel;
import org.ranjith.swing.GlassToolBar;
import org.ranjith.swing.IconLabelListCellRenderer;
import org.ranjith.swing.IconListItem;
import org.ranjith.swing.ModernButton;
import org.ranjith.swing.MonthYearSpinnerPanel;
import org.ranjith.swing.QTable;
import org.ranjith.swing.QTableModel;
import org.ranjith.swing.RoundButton;
import org.ranjith.swing.SimpleGradientPanel;
import org.ranjith.swing.SwingRConstants;

/**
 * Main UI window class for the application. $Id:$
 */
public class JSpent extends JFrame {
    private QTable table = null;
    private JSplitPane splitPane;
    // ToolBarButton addButton = new ToolBarButton(0);
    ModernButton addButton = new ModernButton();
    ModernButton modifyButton = new ModernButton();
    ModernButton deleteButton = new ModernButton();
    private JList optionsList;

    JScrollPane tableScrollPane;

    public static final String CTX_EXPENSES = "Expenses";
    public static final String CTX_INCOMES = "Incomes";
    public static final String CTX_SAVINGS = "Savings";
    public static final String CTX_LIABILITIES = "Liabilities";
    public static final String CTX_SUMMARY = "Summary";
    private UIFactory uiFactory;

    JPanel centerPanel, buttonPanel;
    // savings form
    SimpleGradientPanel addSavingsForm;
    EmbossedLabel totalLabel;
    private int currentMonth;

    /**
     * Creates default application window instance. Does not show the window -
     * sets size.
     */
    public JSpent() {
        super(Application.getResourceBundle().getString("app.title"));

        uiFactory = UIFactory.getInstance(this);
        this.table = uiFactory.createDataTable();
        getContentPane().setLayout(new BorderLayout());
        JScrollPane categoryScrollPane = getOptionsPane();
        splitPane = getSplitPane(getTablePane(table), categoryScrollPane);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(getBottomPanel(), BorderLayout.PAGE_END);
        SimpleGradientPanel topGradientPanel = getTopPanel();
        getContentPane().add(topGradientPanel, BorderLayout.PAGE_START);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Start with expense always selected.
        optionsList.setSelectedIndex(0);
        optionsList.requestFocusInWindow();
        setSize(800, 640);
    }

    private SimpleGradientPanel getTopPanel() {
        SimpleGradientPanel topGradientPanel = new SimpleGradientPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        topGradientPanel.setLayout(gridBagLayout);
        topGradientPanel.setDrawBottomBorder(true);
        GlassToolBar toolBar = getToolBar();

        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.weightx = 0.5;
        gbConstraints.anchor = GridBagConstraints.PAGE_START;
        topGradientPanel.add(toolBar, gbConstraints);

        topGradientPanel.setBorder(SwingRConstants.EMPTY_BORDER);
        return topGradientPanel;
    }

    private MonthYearSpinnerPanel getMonthYearSpinner() {
        MonthYearSpinnerPanel monthYearSpinner = new MonthYearSpinnerPanel();
        monthYearSpinner.addChangeListener(new MonthYearChangeActionListener(
                this));
        currentMonth = ((Calendar) monthYearSpinner.getValue())
                .get(Calendar.MONTH) + 1;
        return monthYearSpinner;
    }

    private GlassToolBar getToolBar() {
        ResourceBundle bundle = Application.getResourceBundle();
        GlassToolBar toolBar = new GlassToolBar();
        URL resource = JSpent.class.getResource(bundle
                .getString("toolbar.add.new.icon"));
        addButton.setIcon(new ImageIcon(resource, bundle
                .getString("toolbar.add.new")));
        addButton.setButtonStyle(ModernButton.BUTTONSTYLE_TOOLBAR_LEFT);
        addButton.addActionListener(new AddNewActionListener(this));
        resource = JSpent.class.getResource(bundle
                .getString("toolbar.modify.icon"));
        modifyButton.setIcon(new ImageIcon(resource, bundle
                .getString("toolbar.modify")));
        modifyButton.setButtonStyle(ModernButton.BUTTONSTYLE_TOOLBAR_CENTER);
        modifyButton.addActionListener(new ModifyActionListener(this));
        resource = JSpent.class.getResource(bundle
                .getString("toolbar.delete.icon"));
        deleteButton.setIcon(new ImageIcon(resource, bundle
                .getString("toolbar.delete")));
        deleteButton.setButtonStyle(ModernButton.BUTTONSTYLE_TOOLBAR_RIGHT);
        deleteButton.addActionListener(new DeleteActionListener(this));
        // Not enabled on start up. Enable only when table row is selected.
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);

        toolBar.add(addButton);
        toolBar.add(modifyButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator(deleteButton.getPreferredSize());
        toolBar.add(getMonthYearSpinner());
        return toolBar;
    }

    private JSplitPane getSplitPane(JComponent scrollPane,
            JComponent categoryScrollPane) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(1);
        splitPane.setDividerLocation(160);
        splitPane.add(scrollPane, JSplitPane.RIGHT);
        splitPane.add(categoryScrollPane, JSplitPane.LEFT);
        splitPane.setBackground(SwingRConstants.LINE_COLOR);
        splitPane.setBorder(SwingRConstants.EMPTY_BORDER);
        return splitPane;
    }

    private JPanel getTablePane(QTable table) {
        this.table = table;
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(SwingRConstants.EMPTY_BORDER);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        rightPanel.setBorder(SwingRConstants.EMPTY_BORDER);
        return rightPanel;
    }

    private Component getBottomPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        SimpleGradientPanel bottomPanel = new SimpleGradientPanel();
        bottomPanel.setLayout(gridBagLayout);
        bottomPanel.setDrawTopBorder(true);
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 1;
        gbConstraints.anchor = GridBagConstraints.CENTER;
        totalLabel = new EmbossedLabel("");
        totalLabel.setFont(SwingRConstants.DEFAULT_TEXT_FONT);
        bottomPanel.add(totalLabel, gbConstraints);

        // JPanel spinnerPanel = new JPanel();
        // spinnerPanel.setLayout(new BorderLayout());
        // spinnerPanel.setOpaque(false);
        // MonthYearSpinnerPanel monthYearSpinner = getMonthYearSpinner();
        // spinnerPanel.add(new JLabel(" "),BorderLayout.PAGE_START); //spacer
        // spinnerPanel.add(monthYearSpinner,BorderLayout.CENTER);
        // spinnerPanel.add(new JLabel(" "),BorderLayout.PAGE_END); //spacer
        // EmbossedLabel msgLabel = new EmbossedLabel(" for
        // ",EmbossedLabel.TRAILING);
        // msgLabel.setFont(SwingRConstants.DEFAULT_TEXT_FONT);
        //        
        // filterPanel = new JPanel(new BorderLayout());
        // filterPanel.setOpaque(false);
        // filterPanel.add(msgLabel,BorderLayout.CENTER);
        // filterPanel.add(spinnerPanel,BorderLayout.LINE_END);
        // gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        // gbConstraints.gridx = 3;
        // gbConstraints.gridy = 1;
        // gbConstraints.anchor = GridBagConstraints.PAGE_END;
        // bottomPanel.add(filterPanel,gbConstraints);

        bottomPanel.setBorder(SwingRConstants.EMPTY_BORDER);
        return bottomPanel;
    }

    /**
     * Set total value at the total area. This would show total expense, total
     * savings.. etc.
     * 
     * @param totalString
     *            total amount(savings,expense..)
     */
    public void setTotal(String totalString) {
        totalLabel.setText(totalString);
    }

    private String getTotalExpense() {
        return "Total Expenses "
                + NumberFormat.getCurrencyInstance().format(
                        table.getQTableModel().sum(3)) + " ";
    }

    private JScrollPane getOptionsPane() {
        ResourceBundle bundle = Application.getResourceBundle();
        DefaultListModel listModel = new DefaultListModel();
        optionsList = new JList(listModel);
        optionsList.setCellRenderer(new IconLabelListCellRenderer(1));

        URL resource = JSpent.class.getResource(bundle
                .getString("options.expense.icon"));
        listModel.addElement(new IconListItem(new ImageIcon(resource),
                CTX_EXPENSES));

        resource = JSpent.class.getResource(bundle
                .getString("options.income.icon"));
        listModel.addElement(new IconListItem(new ImageIcon(resource),
                CTX_INCOMES));

        resource = JSpent.class.getResource(bundle
                .getString("options.savings.icon"));
        listModel.addElement(new IconListItem(new ImageIcon(resource),
                CTX_SAVINGS));

        resource = JSpent.class.getResource(bundle
                .getString("options.liabilities.icon"));
        listModel.addElement(new IconListItem(new ImageIcon(resource),
                CTX_LIABILITIES));

        resource = JSpent.class.getResource(bundle
                .getString("options.summary.icon"));
        listModel.addElement(new IconListItem(new ImageIcon(resource),
                CTX_SUMMARY));

        optionsList.setBackground(SwingRConstants.PANEL_DEEP_BACKGROUND_COLOR);
        optionsList.addListSelectionListener(new OptionSelectedActionListener(
                this));
        JScrollPane categoryScrollPane = new JScrollPane(optionsList);
        JViewport colHeaderViewPort = new JViewport();
        colHeaderViewPort.setView(getHeader());

        categoryScrollPane.setColumnHeader(colHeaderViewPort);
        // XXX
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

        optionsList.setEnabled(false);
    }

    /**
     * This needs to be called when we have to add a new expense data.
     */
    public void showExpenseForm() {
        showExpenseForm(null);
    }

    /**
     * Adds a new row to the main table view in the application window.
     */
    public void addNewRow() {
        // for expense.
        ((JSpentTableModel) table.getQTableModel()).addEmptyRow();
    }

    /**
     * This needs to be called when we have to edit an expense data.
     */
    public void showExpenseForm(Expense expense) {
        prepareUIForForm();
        ExpenseFormPanel panel = expense == null ? new ExpenseFormPanel()
                : new ExpenseFormPanel(expense);
        updateRightPane(panel);
        panel.setDoneButtonListener(new BackActionListener(this, panel));
        int mode = (expense == null ? SaveExpenseActionListener.ADD_NEW_MODE
                : SaveExpenseActionListener.UPDATE_MODE);
        panel.setSaveButtonListener(new SaveExpenseActionListener(mode, panel,
                this));
    }

    private void updateRightPane(JPanel panel) {
        splitPane.setRightComponent(panel);
        splitPane.setDividerLocation(160);
    }

    /**
     * Show form for adding savings information.
     */
    public void showAddSavings() {
        prepareUIForForm();
        addSavingsForm = uiFactory.createAddSavingsForm();
        updateRightPane(addSavingsForm);
    }

    /**
     * Updates application's UI to match current selection on options. Options
     * selection determines the application's current context. and restore's
     * application to default view of tables/dash board/summary.
     */
    public void refreshUI() {
        if (getCurrentContext().equals(CTX_EXPENSES)) {
            uiFactory.updateExpenseDataTable(table, currentMonth);
            setRightTable(table);
            updateUIElements();
            setTotal(getTotalExpense());
        }
        if (getCurrentContext().equals(CTX_SAVINGS)) {
            JOptionPane.showMessageDialog(this, "Should go to savings view");
            updateUIElements();
        }
    }

    private void updateUIElements() {
        if (table.getSelectedRow() < 0) {
            setModfyToolBarButtonEnabled(false);
            setDeleteToolBarButtonEnabled(false);
        }
        setAddToolBarButtonEnabled(true);
        splitPane.setDividerLocation(160);
        optionsList.setEnabled(true);
        table.addMouseListener(new ContextMenuListener(UIFactory
                .createPopupMenuFor(getCurrentContext(), this)));
    }

    /**
     * Sets the main table in the application.
     * 
     * @param table
     */
    public void setRightTable(QTable table) {
        updateRightPane(getTablePane(table));
        SwingUtilities.updateComponentTreeUI(tableScrollPane);
    }

    /**
     * Sets form component on the right hand panel.
     * 
     * @param component
     *            element to be shown on right hand panel.
     */
    public void setForm(JComponent component) {
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(component, BorderLayout.CENTER);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(new RoundButton("Test"), BorderLayout.NORTH);

        addSavingsForm.add(centerPanel, BorderLayout.CENTER);
        addSavingsForm.updateUI();
    }

    /**
     * Returns window's current context - such as expense, savings..etc. Refer
     * to CTX_* constants in this class.
     * 
     * @return
     */
    public String getCurrentContext() {
        Object selectedObject = optionsList.getSelectedValue();
        return ((IconListItem) selectedObject).getText();
    }

    /**
     * Sets window's current context - such as expense, savings..etc. Refer to
     * CTX_* constants in this class. This call will update UI appearance of the
     * window to reflect appropriate context.
     * 
     * @param context
     */
    public void setCurrentContext(String context) {
        if (context.equals(CTX_EXPENSES)) {
            this.optionsList.setSelectedIndex(0);
        } else if (context.equals(CTX_INCOMES)) {
            this.optionsList.setSelectedIndex(1);
        } else if (context.equals(CTX_SAVINGS)) {
            this.optionsList.setSelectedIndex(2);
        } else if (context.equals(CTX_LIABILITIES)) {
            this.optionsList.setSelectedIndex(3);
        } else if (context.equals(CTX_SUMMARY)) {
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
     * 
     * @param month
     *            1..12 indicating month
     */
    public void updateExpenseTableForMonth(int month) {
        QTableModel model = (QTableModel) table.getModel();
        model.setRows(getExpenses(month));
        model.fireTableDataChanged();
    }

    /**
     * Removes selected row from the table. But this would not make changes to
     * underlying datastore.
     */
    public void removeSelectedRowObject() {
        if (table.getSelectedRow() > -1) {
            QTableModel model = (QTableModel) table.getModel();
            model.removeRow(table.getSelectedRow());
        }
    }

    public void setCenterPanel(JPanel centerPanel) {
        this.centerPanel = centerPanel;
    }

    public void setButtonPanel(JPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
    }

    /**
     * @param currentMonth the currentMonth to set
     */
    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }
}