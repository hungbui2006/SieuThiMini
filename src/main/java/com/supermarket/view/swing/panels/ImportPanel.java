package com.supermarket.view.swing.panels;

import com.supermarket.model.Product;
import com.supermarket.model.Supplier;
import com.supermarket.model.User;
import com.supermarket.service.GoodsImportService;
import com.supermarket.service.ProductService;
import com.supermarket.service.SupplierService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Goods Import panel.
 */
public class ImportPanel extends JPanel {

    private final GoodsImportService importService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final User currentUser;

    private JTable table;
    private JComboBox<String> cboSupplier, cboProduct;
    private JTextField txtQty, txtCost;

    public ImportPanel(GoodsImportService importService, ProductService productService,
                       SupplierService supplierService, User currentUser) {
        this.importService = importService;
        this.productService = productService;
        this.supplierService = supplierService;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(UIFactory.BG);
        initUI();
        loadData();
    }

    private void initUI() {
        // ── TOP: Toolbar ──────────────────────────────────────────────────
        JPanel top = UIFactory.toolbar();
        top.add(UIFactory.sectionTitle("📥  Nhập Hàng Từ Nhà Cung Cấp"), BorderLayout.WEST);

        JButton btnRefresh = UIFactory.button("↻ Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRefresh.addActionListener(e -> loadData());

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acts.setOpaque(false);
        acts.add(btnRefresh);
        top.add(acts, BorderLayout.EAST);

        // ── CENTER: Layout ────────────────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout(18, 0));
        wrapper.setBackground(UIFactory.BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // LEFT: Form
        JPanel formCard = UIFactory.card();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(320, 0));

        cboSupplier = new JComboBox<>();
        cboProduct = new JComboBox<>();
        txtQty = UIFactory.textField();
        txtCost = UIFactory.textField();

        formCard.add(UIFactory.formRow("Chọn Nhà Cung Cấp:", cboSupplier));
        formCard.add(UIFactory.formRow("Chọn Sản Phẩm:", cboProduct));
        formCard.add(UIFactory.formRow("Số Lượng Nhập:", txtQty));
        formCard.add(UIFactory.formRow("Giá Nhập (VND):", txtCost));

        formCard.add(Box.createVerticalStrut(10));
        JButton btnImport = UIFactory.button("XÁC NHẬN NHẬP HÀNG", UIFactory.PRIMARY, UIFactory.PRIMARY_DARK);
        btnImport.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnImport.addActionListener(e -> processImport());
        formCard.add(btnImport);

        // RIGHT: Table
        String[] cols = {"Mã Phiếu", "Thời gian", "Nhân viên", "Sản phẩm", "SL", "Giá nhập", "Tổng tiền"};
        table = UIFactory.createTable(cols);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        JPanel tableCard = UIFactory.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(UIFactory.scrollTable(table), BorderLayout.CENTER);

        wrapper.add(formCard, BorderLayout.WEST);
        wrapper.add(tableCard, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }

    private void loadData() {
        // Load dropdowns
        cboSupplier.removeAllItems();
        for (Supplier s : supplierService.getAllSuppliers()) {
            cboSupplier.addItem(s.getId() + " - " + s.getName());
        }
        cboProduct.removeAllItems();
        for (Product p : productService.getAllProducts()) {
            cboProduct.addItem(p.getId() + " - " + p.getName());
        }

        // Load history table
        DefaultTableModel m = UIFactory.getModel(table);
        m.setRowCount(0);
        List<com.supermarket.model.GoodsImport> receipts = importService.getAllImports();
        for (com.supermarket.model.GoodsImport r : receipts) {
            for (com.supermarket.model.ImportItem item : r.getItems()) {
                m.addRow(new Object[]{
                        r.getId(),
                        r.getImportDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        r.getEmployeeName(),
                        item.getProductName(),
                        item.getQuantity(),
                        UIFactory.vnd(item.getImportPrice()),
                        UIFactory.vnd(item.getSubTotal())
                });
            }
        }
    }

    private void processImport() {
        if (cboSupplier.getSelectedIndex() < 0 || cboProduct.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn NCC và Sản phẩm!"); return;
        }
        try {
            String sid = ((String) cboSupplier.getSelectedItem()).split(" - ")[0];
            String sname = ((String) cboSupplier.getSelectedItem()).split(" - ")[1];
            String pid = ((String) cboProduct.getSelectedItem()).split(" - ")[0];
            int qty = Integer.parseInt(txtQty.getText().trim());
            double cost = Double.parseDouble(txtCost.getText().trim());
            if (qty <= 0 || cost < 0) throw new NumberFormatException();

            com.supermarket.model.GoodsImport gi = importService.createImport(sid, sname, currentUser.getId(), currentUser.getFullName());
            importService.addImportItem(gi, pid, qty, cost);
            importService.saveImport(gi);
            
            JOptionPane.showMessageDialog(this, "Nhập hàng thành công!");
            txtQty.setText(""); txtCost.setText("");
            loadData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng và giá nhập phải là số hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
