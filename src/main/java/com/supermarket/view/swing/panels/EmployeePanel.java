package com.supermarket.view.swing.panels;

import com.supermarket.model.Employee;
import com.supermarket.service.EmployeeService;
import com.supermarket.util.IdGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Modern Employee management panel.
 */
public class EmployeePanel extends JPanel {

    private final EmployeeService employeeService;
    private JTable table;
    private JLabel lblCount;

    public EmployeePanel(EmployeeService employeeService) {
        this.employeeService = employeeService;
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
        titleRow.add(UIFactory.sectionTitle("👤  Quản lý Nhân viên"));
        lblCount = UIFactory.countBadge(0);
        titleRow.add(lblCount);
        top.add(titleRow, BorderLayout.WEST);

        JButton btnAdd = UIFactory.button("＋ Thêm nhân viên", UIFactory.SUCCESS, UIFactory.SUCCESS_DARK);
        btnAdd.addActionListener(e -> addEmployee());
        JButton btnRefresh = UIFactory.button("↻ Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRefresh.addActionListener(e -> loadData());

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acts.setOpaque(false);
        acts.add(btnAdd); acts.add(btnRefresh);
        top.add(acts, BorderLayout.EAST);

        // ── CENTER: Table ─────────────────────────────────────────────────
        String[] cols = {"Mã NV", "Họ tên", "Tên ĐN", "Mật khẩu", "SĐT", "Lương CB", "Hoa hồng", "Tổng lương", "Trạng thái"};
        table = UIFactory.createTable(cols);

        JPanel center = UIFactory.card();
        center.setLayout(new BorderLayout());
        center.add(UIFactory.scrollTable(table), BorderLayout.CENTER);

        // ── BOTTOM: Actions ───────────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
        bottom.setOpaque(false);
        JButton btnEdit = UIFactory.button("✏ Sửa", UIFactory.WARNING, UIFactory.WARNING_DARK);
        btnEdit.addActionListener(e -> editSelected());
        JButton btnToggle = UIFactory.button("⏻ Đổi trạng thái", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnToggle.addActionListener(e -> toggleStatus());
        bottom.add(btnEdit); bottom.add(btnToggle);
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
        for (Employee e : employeeService.getAllEmployees()) {
            m.addRow(new Object[]{
                    e.getId(), e.getFullName(), e.getUsername(), e.getPassword(), e.getPhone(),
                    UIFactory.vnd(e.getBaseSalary()),
                    String.format("%.0f%%", e.getSalesCommissionRate() * 100),
                    UIFactory.vnd(e.calculateTotalSalary()),
                    e.isActive() ? "✅ Hoạt động" : "❌ Vô hiệu"
            });
        }
        lblCount.setText(String.valueOf(m.getRowCount()));
    }

    private void addEmployee() {
        JTextField fUser = UIFactory.textField(), fPass = UIFactory.textField(),
                fName = UIFactory.textField(), fPhone = UIFactory.textField(),
                fSal = UIFactory.textField(), fCom = UIFactory.textField();
        fSal.setText("5000000"); fCom.setText("0.02");
        JPanel form = buildForm(new String[]{"Tên đăng nhập:*", "Mật khẩu:*", "Họ tên:*", "SĐT:", "Lương cơ bản:", "Tỉ lệ hoa hồng:"},
                new JComponent[]{fUser, fPass, fName, fPhone, fSal, fCom});
        
        if (JOptionPane.showConfirmDialog(this, form, "Thêm Nhân Viên", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                String id = IdGenerator.nextId(IdGenerator.EMPLOYEE_PREFIX);
                double sal = Double.parseDouble(fSal.getText().trim());
                double com = Double.parseDouble(fCom.getText().trim());
                Employee e = new Employee(id, fUser.getText().trim(), fPass.getText().trim(),
                        fName.getText().trim(), fPhone.getText().trim(), sal, com);
                employeeService.addEmployee(e);
                loadData();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn nhân viên cần sửa!"); return; }
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        try {
            Employee e = employeeService.getEmployee(id);
            JTextField fUser = UIFactory.textField(), fPass = UIFactory.textField(),
                    fName = UIFactory.textField(), fPhone = UIFactory.textField(),
                    fSal = UIFactory.textField(), fCom = UIFactory.textField();
            fUser.setText(e.getUsername()); fPass.setText(e.getPassword());
            fName.setText(e.getFullName()); fPhone.setText(e.getPhone());
            fSal.setText(String.valueOf(e.getBaseSalary())); fCom.setText(String.valueOf(e.getSalesCommissionRate()));

            JPanel form = buildForm(new String[]{"Tên đăng nhập:*", "Mật khẩu:*", "Họ tên:*", "SĐT:", "Lương cơ bản:", "Tỉ lệ hoa hồng:"},
                    new JComponent[]{fUser, fPass, fName, fPhone, fSal, fCom});
            if (JOptionPane.showConfirmDialog(this, form, "Sửa Nhân Viên", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                e.setUsername(fUser.getText().trim()); e.setPassword(fPass.getText().trim());
                e.setFullName(fName.getText().trim()); e.setPhone(fPhone.getText().trim());
                e.setBaseSalary(Double.parseDouble(fSal.getText().trim()));
                e.setSalesCommissionRate(Double.parseDouble(fCom.getText().trim()));
                employeeService.updateEmployee(e);
                loadData();
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void toggleStatus() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        try {
            Employee e = employeeService.getEmployee(id);
            e.setActive(!e.isActive());
            employeeService.updateEmployee(e);
            loadData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
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
