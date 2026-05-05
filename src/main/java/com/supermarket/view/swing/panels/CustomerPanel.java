package com.supermarket.view.swing.panels;

import com.supermarket.model.Customer;
import com.supermarket.service.CustomerService;
import com.supermarket.util.IdGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Customer management panel.
 */
public class CustomerPanel extends JPanel {

    private final CustomerService customerService;
    private JTable table;
    private JTextField txtSearch;
    private JLabel lblCount;

    public CustomerPanel(CustomerService customerService) {
        this.customerService = customerService;
        setLayout(new BorderLayout());
        setBackground(UIFactory.BG);
        initUI();
        loadData();
    }

    private void initUI() {
        // ── TOP: Toolbar ──────────────────────────────────────────────────
        JPanel top = UIFactory.toolbar();
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.add(UIFactory.sectionTitle("👥  Quản lý Khách hàng"));
        lblCount = UIFactory.countBadge(0);
        titleRow.add(lblCount);
        top.add(titleRow, BorderLayout.WEST);

        txtSearch = UIFactory.searchBar("Tìm theo tên hoặc SĐT...");
        txtSearch.addActionListener(e -> search());
        JButton btnSearch = UIFactory.button("Tìm", UIFactory.PRIMARY, UIFactory.PRIMARY_DARK);
        btnSearch.addActionListener(e -> search());
        JButton btnAdd = UIFactory.button("＋ Thêm KH", UIFactory.SUCCESS, UIFactory.SUCCESS_DARK);
        btnAdd.addActionListener(e -> addCustomer());
        JButton btnRefresh = UIFactory.button("↻ Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRefresh.addActionListener(e -> loadData());

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acts.setOpaque(false);
        acts.add(txtSearch); acts.add(btnSearch); acts.add(btnAdd); acts.add(btnRefresh);
        top.add(acts, BorderLayout.EAST);

        // ── CENTER: Table ─────────────────────────────────────────────────
        String[] cols = {"Mã KH", "Tên KH", "SĐT", "Email", "Điểm", "Hạng", "Giảm giá"};
        table = UIFactory.createTable(cols);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        JPanel center = UIFactory.card();
        center.setLayout(new BorderLayout());
        center.add(UIFactory.scrollTable(table), BorderLayout.CENTER);

        // ── BOTTOM: Actions ───────────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
        bottom.setOpaque(false);
        JButton btnEdit = UIFactory.button("✏ Sửa", UIFactory.WARNING, UIFactory.WARNING_DARK);
        btnEdit.addActionListener(e -> editSelected());
        JButton btnDel = UIFactory.button("🗑 Xóa", UIFactory.DANGER, UIFactory.DANGER_DARK);
        btnDel.addActionListener(e -> deleteSelected());
        bottom.add(btnEdit); bottom.add(btnDel);
        center.add(bottom, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIFactory.BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        wrapper.add(center, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private void loadData() {
        populateTable(customerService.getAllCustomers());
    }

    private void search() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty() || kw.startsWith("🔍")) { loadData(); return; }
        populateTable(customerService.searchByName(kw));
    }

    private void populateTable(List<Customer> list) {
        DefaultTableModel m = UIFactory.getModel(table);
        m.setRowCount(0);
        for (Customer c : list) {
            m.addRow(new Object[]{
                    c.getId(), c.getName(), c.getPhone(), c.getEmail(),
                    c.getRewardPoints(), c.getTier().getDisplayName(),
                    String.format("%.0f%%", c.getDiscountRate() * 100)
            });
        }
        lblCount.setText(String.valueOf(m.getRowCount()));
    }

    private void addCustomer() {
        JTextField fName = UIFactory.textField(), fPhone = UIFactory.textField(), fEmail = UIFactory.textField();
        JPanel form = buildForm(new String[]{"Tên KH:*", "SĐT:*", "Email:"}, new JComponent[]{fName, fPhone, fEmail});
        if (JOptionPane.showConfirmDialog(this, form, "Thêm KH", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (fName.getText().trim().isEmpty() || fPhone.getText().trim().isEmpty()) return;
            String id = IdGenerator.nextId(IdGenerator.CUSTOMER_PREFIX);
            customerService.addCustomer(new Customer(id, fName.getText().trim(), fPhone.getText().trim(), fEmail.getText().trim()));
            loadData();
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn KH cần sửa!"); return; }
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        try {
            Customer c = customerService.getCustomer(id);
            JTextField fName = UIFactory.textField(); fName.setText(c.getName());
            JTextField fPhone = UIFactory.textField(); fPhone.setText(c.getPhone());
            JTextField fEmail = UIFactory.textField(); fEmail.setText(c.getEmail());
            JPanel form = buildForm(new String[]{"Tên KH:*", "SĐT:*", "Email:"}, new JComponent[]{fName, fPhone, fEmail});
            if (JOptionPane.showConfirmDialog(this, form, "Sửa KH", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                c.setName(fName.getText().trim()); c.setPhone(fPhone.getText().trim()); c.setEmail(fEmail.getText().trim());
                customerService.updateCustomer(c);
                loadData();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa KH " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            customerService.deleteCustomer(id);
            loadData();
        }
    }

    private JPanel buildForm(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        for (int i = 0; i < labels.length; i++) {
            p.add(UIFactory.formRow(labels[i], fields[i]));
        }
        return p;
    }
}
