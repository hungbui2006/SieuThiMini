package com.supermarket.view.swing.panels;

import com.supermarket.model.Category;
import com.supermarket.service.CategoryService;
import com.supermarket.util.IdGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Modern Category management panel.
 */
public class CategoryPanel extends JPanel {

    private final CategoryService categoryService;
    private JTable table;
    private JLabel lblCount;

    public CategoryPanel(CategoryService categoryService) {
        this.categoryService = categoryService;
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
        titleRow.add(UIFactory.sectionTitle("🏷️  Quản lý Danh mục"));
        lblCount = UIFactory.countBadge(0);
        titleRow.add(lblCount);
        top.add(titleRow, BorderLayout.WEST);

        JButton btnAdd = UIFactory.button("＋ Thêm danh mục", UIFactory.SUCCESS, UIFactory.SUCCESS_DARK);
        btnAdd.addActionListener(e -> addCategory());
        JButton btnRefresh = UIFactory.button("↻ Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRefresh.addActionListener(e -> loadData());

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acts.setOpaque(false);
        acts.add(btnAdd); acts.add(btnRefresh);
        top.add(acts, BorderLayout.EAST);

        // ── CENTER: Table ─────────────────────────────────────────────────
        String[] cols = {"Mã DM", "Tên danh mục", "Mô tả"};
        table = UIFactory.createTable(cols);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(450);

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
        for (Category c : categoryService.getAllCategories())
            m.addRow(new Object[]{c.getId(), c.getName(), c.getDescription()});
        lblCount.setText(String.valueOf(m.getRowCount()));
    }

    private void addCategory() {
        JTextField fName = UIFactory.textField();
        JTextField fDesc = UIFactory.textField();
        JPanel form = buildForm(new String[]{"Tên danh mục:*", "Mô tả:"}, new JComponent[]{fName, fDesc});
        if (JOptionPane.showConfirmDialog(this, form, "Thêm danh mục", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (fName.getText().trim().isEmpty()) return;
            String id = IdGenerator.nextId(IdGenerator.CATEGORY_PREFIX);
            categoryService.addCategory(new Category(id, fName.getText().trim(), fDesc.getText().trim()));
            loadData();
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn danh mục cần sửa!"); return; }
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        try {
            Category cat = categoryService.getCategory(id);
            JTextField fName = UIFactory.textField(); fName.setText(cat.getName());
            JTextField fDesc = UIFactory.textField(); fDesc.setText(cat.getDescription());
            JPanel form = buildForm(new String[]{"Tên danh mục:*", "Mô tả:"}, new JComponent[]{fName, fDesc});
            if (JOptionPane.showConfirmDialog(this, form, "Sửa danh mục", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                cat.setName(fName.getText().trim());
                cat.setDescription(fDesc.getText().trim());
                categoryService.updateCategory(cat);
                loadData();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa danh mục " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            categoryService.deleteCategory(id);
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
