package com.supermarket.view.swing.panels;

import com.supermarket.model.Supplier;
import com.supermarket.service.SupplierService;
import com.supermarket.util.IdGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Modern Supplier management panel.
 */
public class SupplierPanel extends JPanel {

    private final SupplierService supplierService;
    private JTable table;
    private JLabel lblCount;

    public SupplierPanel(SupplierService supplierService) {
        this.supplierService = supplierService;
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
        titleRow.add(UIFactory.sectionTitle("🚛  Quản lý Nhà cung cấp"));
        lblCount = UIFactory.countBadge(0);
        titleRow.add(lblCount);
        top.add(titleRow, BorderLayout.WEST);

        JButton btnAdd = UIFactory.button("＋ Thêm NCC", UIFactory.SUCCESS, UIFactory.SUCCESS_DARK);
        btnAdd.addActionListener(e -> addSupplier());
        JButton btnRefresh = UIFactory.button("↻ Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRefresh.addActionListener(e -> loadData());

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acts.setOpaque(false);
        acts.add(btnAdd); acts.add(btnRefresh);
        top.add(acts, BorderLayout.EAST);

        // ── CENTER: Table ─────────────────────────────────────────────────
        String[] cols = {"Mã NCC", "Tên NCC", "SĐT", "Địa chỉ", "Email"};
        table = UIFactory.createTable(cols);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);

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
        DefaultTableModel m = UIFactory.getModel(table);
        m.setRowCount(0);
        for (Supplier s : supplierService.getAllSuppliers())
            m.addRow(new Object[]{s.getId(), s.getName(), s.getContactPhone(), s.getAddress(), s.getEmail()});
        lblCount.setText(String.valueOf(m.getRowCount()));
    }

    private void addSupplier() {
        JTextField fName = UIFactory.textField(), fPhone = UIFactory.textField(),
                fAddr = UIFactory.textField(), fEmail = UIFactory.textField();
        JPanel form = buildForm(new String[]{"Tên NCC:*", "Số điện thoại:", "Địa chỉ:", "Email:"},
                new JComponent[]{fName, fPhone, fAddr, fEmail});
        if (JOptionPane.showConfirmDialog(this, form, "Thêm nhà cung cấp", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (fName.getText().trim().isEmpty()) return;
            String id = IdGenerator.nextId(IdGenerator.SUPPLIER_PREFIX);
            supplierService.addSupplier(new Supplier(id, fName.getText().trim(),
                    fPhone.getText().trim(), fAddr.getText().trim(), fEmail.getText().trim()));
            loadData();
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn NCC cần sửa!"); return; }
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        try {
            Supplier s = supplierService.getSupplier(id);
            JTextField fName = UIFactory.textField(); fName.setText(s.getName());
            JTextField fPhone = UIFactory.textField(); fPhone.setText(s.getContactPhone());
            JTextField fAddr = UIFactory.textField(); fAddr.setText(s.getAddress());
            JTextField fEmail = UIFactory.textField(); fEmail.setText(s.getEmail());
            JPanel form = buildForm(new String[]{"Tên NCC:*", "SĐT:", "Địa chỉ:", "Email:"},
                    new JComponent[]{fName, fPhone, fAddr, fEmail});
            if (JOptionPane.showConfirmDialog(this, form, "Sửa NCC", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                s.setName(fName.getText().trim()); s.setContactPhone(fPhone.getText().trim());
                s.setAddress(fAddr.getText().trim()); s.setEmail(fEmail.getText().trim());
                supplierService.updateSupplier(s);
                loadData();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa NCC " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            supplierService.deleteSupplier(id);
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
