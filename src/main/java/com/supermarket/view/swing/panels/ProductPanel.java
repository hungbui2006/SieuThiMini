package com.supermarket.view.swing.panels;

import com.supermarket.exception.EntityNotFoundException;
import com.supermarket.model.PerishableProduct;
import com.supermarket.model.Product;
import com.supermarket.service.CategoryService;
import com.supermarket.service.ProductService;
import com.supermarket.service.SupplierService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modern Product Management panel.
 * Hiển thị đầy đủ: Mã hàng, tên hàng, số lượng, đơn giá, đơn vị tính, ngày nhập, giá bán.
 * Chức năng: thêm, sửa, xóa, tìm kiếm theo mặt hàng, tìm kiếm theo ngày, thống kê.
 */
public class ProductPanel extends JPanel {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    private JTable table;
    private JTextField txtSearch, txtDateSearch;
    private JLabel lblCount;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProductPanel(ProductService productService, CategoryService categoryService,
                        SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
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
        titleRow.add(UIFactory.sectionTitle("📦  Quản lý Sản phẩm"));
        lblCount = UIFactory.countBadge(0);
        titleRow.add(lblCount);
        top.add(titleRow, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        // Tìm kiếm theo tên/mã sản phẩm
        txtSearch = UIFactory.searchBar("Tìm theo mã hoặc tên...");
        txtSearch.addActionListener(e -> searchByName());

        JButton btnSearch = UIFactory.button("🔍 Tìm", UIFactory.PRIMARY, UIFactory.PRIMARY_DARK);
        btnSearch.setBorder(new EmptyBorder(6, 14, 6, 14));
        btnSearch.addActionListener(e -> searchByName());

        // Tìm kiếm theo ngày nhập
        txtDateSearch = UIFactory.textField();
        txtDateSearch.setPreferredSize(new Dimension(110, 36));
        txtDateSearch.setText(LocalDate.now().format(FMT));
        txtDateSearch.setToolTipText("Ngày nhập (dd/MM/yyyy)");

        JButton btnDateSearch = UIFactory.button("📅 Theo ngày", UIFactory.TEAL, UIFactory.TEAL_DARK);
        btnDateSearch.setBorder(new EmptyBorder(6, 14, 6, 14));
        btnDateSearch.addActionListener(e -> searchByDate());

        JButton btnAdd = UIFactory.button("＋ Thêm mới", UIFactory.SUCCESS, UIFactory.SUCCESS_DARK);
        btnAdd.addActionListener(e -> showAddDialog());

        JButton btnRefresh = UIFactory.button("↻ Tất cả", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRefresh.addActionListener(e -> loadData());

        actions.add(txtSearch);
        actions.add(btnSearch);
        actions.add(Box.createHorizontalStrut(4));
        actions.add(txtDateSearch);
        actions.add(btnDateSearch);
        actions.add(Box.createHorizontalStrut(4));
        actions.add(btnAdd);
        actions.add(btnRefresh);
        top.add(actions, BorderLayout.EAST);

        // ── CENTER: Table ─────────────────────────────────────────────────
        // Đầy đủ: Mã hàng, Tên hàng, Số lượng, Đơn giá, ĐVT, Ngày nhập, Giá bán, Loại, Danh mục
        String[] cols = {"Mã hàng", "Tên hàng", "Số lượng", "Đơn giá", "ĐVT", "Ngày nhập", "Giá bán", "Loại", "Danh mục"};
        table = UIFactory.createTable(cols);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(95);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);

        JPanel center = UIFactory.card();
        center.setLayout(new BorderLayout());
        center.add(UIFactory.scrollTable(table), BorderLayout.CENTER);

        // ── BOTTOM: Actions ───────────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        bottom.setOpaque(false);

        JButton btnEdit = UIFactory.button("✏ Sửa", UIFactory.WARNING, UIFactory.WARNING_DARK);
        btnEdit.addActionListener(e -> editSelected());
        JButton btnDelete = UIFactory.button("🗑 Xóa", UIFactory.DANGER, UIFactory.DANGER_DARK);
        btnDelete.addActionListener(e -> deleteSelected());
        JButton btnLow = UIFactory.button("⚠ Tồn kho thấp", new Color(234, 88, 12), new Color(194, 65, 12));
        btnLow.addActionListener(e -> showLowStock());
        JButton btnExpiry = UIFactory.button("📅 Sắp hết hạn", UIFactory.PURPLE, UIFactory.PURPLE_DARK);
        btnExpiry.addActionListener(e -> showNearExpiry());

        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(Box.createHorizontalStrut(12));
        bottom.add(btnLow);
        bottom.add(btnExpiry);
        center.add(bottom, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIFactory.BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        wrapper.add(center, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    public void loadData() {
        populateTable(productService.getAllProducts());
    }

    private void populateTable(List<Product> list) {
        DefaultTableModel model = UIFactory.getModel(table);
        model.setRowCount(0);
        for (Product p : list) {
            model.addRow(new Object[]{
                    p.getId(), p.getName(),
                    p.getStockQuantity(),
                    UIFactory.vnd(p.getPrice()),
                    p.getUnit(),
                    UIFactory.fmtDate(p.getImportDate()),
                    UIFactory.vnd(p.calculateFinalPrice()),
                    p.getProductTypeDescription(),
                    p.getCategoryId()
            });
        }
        lblCount.setText(String.valueOf(list.size()));
    }

    /** Tìm kiếm theo mặt hàng (mã hoặc tên) */
    private void searchByName() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty() || kw.startsWith("🔍")) { loadData(); return; }
        List<Product> results = productService.searchByName(kw);
        // Cũng thử tìm theo mã
        try {
            Product byId = productService.getProduct(kw);
            if (results.stream().noneMatch(p -> p.getId().equals(byId.getId()))) {
                results.add(0, byId);
            }
        } catch (Exception ignored) {}
        populateTable(results);
    }

    /** Tìm kiếm theo ngày nhập */
    private void searchByDate() {
        String dateStr = txtDateSearch.getText().trim();
        try {
            LocalDate targetDate = LocalDate.parse(dateStr, FMT);
            List<Product> filtered = productService.getAllProducts().stream()
                    .filter(p -> p.getImportDate() != null && p.getImportDate().equals(targetDate))
                    .collect(Collectors.toList());
            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy sản phẩm nào nhập ngày " + dateStr,
                        "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }
            populateTable(filtered);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Định dạng ngày phải là dd/MM/yyyy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        ProductDialog dlg = new ProductDialog((Frame) SwingUtilities.getWindowAncestor(this),
                null, categoryService, supplierService);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            productService.addProduct(dlg.getProduct());
            loadData();
            JOptionPane.showMessageDialog(this, "✅ Đã thêm sản phẩm thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!"); return; }
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        try {
            Product product = productService.getProduct(id);
            ProductDialog dlg = new ProductDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    product, categoryService, supplierService);
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                productService.updateProduct(dlg.getProduct());
                loadData();
                JOptionPane.showMessageDialog(this, "✅ Đã cập nhật thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (EntityNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!"); return; }
        String id = (String) UIFactory.getModel(table).getValueAt(row, 0);
        String name = (String) UIFactory.getModel(table).getValueAt(row, 1);
        int res = JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa sản phẩm: " + name + "?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            productService.deleteProduct(id);
            loadData();
            JOptionPane.showMessageDialog(this, "✅ Đã xóa thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showLowStock() {
        List<Product> low = productService.getLowStockProducts();
        if (low.isEmpty()) {
            JOptionPane.showMessageDialog(this, "✅ Tất cả sản phẩm đều đủ tồn kho!", "Tồn kho", JOptionPane.INFORMATION_MESSAGE);
        } else {
            populateTable(low);
            JOptionPane.showMessageDialog(this,
                    "⚠ Hiển thị " + low.size() + " sản phẩm tồn kho thấp (< 10)", "Cảnh báo tồn kho", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showNearExpiry() {
        List<Product> near = productService.getNearExpiryProducts();
        if (near.isEmpty()) {
            JOptionPane.showMessageDialog(this, "✅ Không có sản phẩm nào sắp hết hạn!", "HSD", JOptionPane.INFORMATION_MESSAGE);
        } else {
            populateTable(near);
            JOptionPane.showMessageDialog(this,
                    "⚠ Hiển thị " + near.size() + " sản phẩm sắp/đã hết hạn", "Hạn sử dụng", JOptionPane.WARNING_MESSAGE);
        }
    }
}
