package com.supermarket.view.swing.panels;

import com.supermarket.factory.ProductFactory;
import com.supermarket.model.*;
import com.supermarket.model.enums.ProductType;
import com.supermarket.service.CategoryService;
import com.supermarket.service.SupplierService;
import com.supermarket.util.IdGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for adding or editing a product.
 * Supports all 3 product types: Regular, Promotion, Perishable.
 */
public class ProductDialog extends JDialog {

    private final CategoryService categoryService;
    private final SupplierService supplierService;
    private final Product existingProduct;

    private boolean confirmed = false;
    private Product result;

    private JTextField txtName, txtPrice, txtStock, txtUnit, txtImportDate;
    private JComboBox<String> cboType, cboCategory, cboSupplier;

    // Promotion fields
    private JPanel promoPanel;
    private JTextField txtDiscount, txtStartDate, txtEndDate;

    // Perishable fields
    private JPanel perishPanel;
    private JTextField txtMfgDate, txtExpDate;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProductDialog(Frame parent, Product existing,
                         CategoryService catSvc, SupplierService supSvc) {
        super(parent, existing == null ? "Thêm Sản Phẩm Mới" : "Sửa Sản Phẩm", true);
        this.existingProduct = existing;
        this.categoryService = catSvc;
        this.supplierService = supSvc;
        initUI();
        if (existing != null) fillForm(existing);
    }

    private void initUI() {
        setSize(480, 600);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIFactory.BG);

        // Scrollable form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UIFactory.CARD_BG);
        form.setBorder(new EmptyBorder(16, 24, 8, 24));

        // Product type selector (disabled for edits)
        cboType = new JComboBox<>(new String[]{"Sản phẩm thường", "Sản phẩm khuyến mãi", "Sản phẩm HSD"});
        cboType.setEnabled(existingProduct == null);
        form.add(row("Loại sản phẩm:", cboType));

        // Common fields
        txtName = UIFactory.textField();
        txtPrice = UIFactory.textField();
        txtStock = UIFactory.textField();
        txtUnit = UIFactory.textField(); txtUnit.setText("cái");
        txtImportDate = UIFactory.textField(); txtImportDate.setText(LocalDate.now().format(FMT));
        form.add(row("Tên sản phẩm: *", txtName));
        form.add(row("Giá gốc / Đơn giá (VND): *", txtPrice));
        form.add(row("Số lượng tồn kho: *", txtStock));
        form.add(row("Đơn vị tính: *", txtUnit));
        form.add(row("Ngày nhập (dd/MM/yyyy):", txtImportDate));

        // Category
        List<Category> cats = categoryService.getAllCategories();
        String[] catArr = cats.stream().map(c -> c.getId() + " - " + c.getName()).toArray(String[]::new);
        cboCategory = new JComboBox<>(catArr);
        form.add(row("Danh mục:", cboCategory));

        // Supplier
        List<Supplier> sups = supplierService.getAllSuppliers();
        String[] supArr = sups.stream().map(s -> s.getId() + " - " + s.getName()).toArray(String[]::new);
        cboSupplier = new JComboBox<>(supArr);
        form.add(row("Nhà cung cấp:", cboSupplier));

        // Promotion extra fields
        promoPanel = new JPanel();
        promoPanel.setLayout(new BoxLayout(promoPanel, BoxLayout.Y_AXIS));
        promoPanel.setBackground(UIFactory.CARD_BG);
        txtDiscount = UIFactory.textField(); txtDiscount.setText("0.15");
        txtStartDate = UIFactory.textField(); txtStartDate.setText(LocalDate.now().format(FMT));
        txtEndDate = UIFactory.textField(); txtEndDate.setText(LocalDate.now().plusDays(30).format(FMT));
        promoPanel.add(row("Tỷ lệ giảm giá (vd: 0.15):", txtDiscount));
        promoPanel.add(row("Ngày bắt đầu KM (dd/MM/yyyy):", txtStartDate));
        promoPanel.add(row("Ngày kết thúc KM (dd/MM/yyyy):", txtEndDate));
        form.add(promoPanel);

        // Perishable extra fields
        perishPanel = new JPanel();
        perishPanel.setLayout(new BoxLayout(perishPanel, BoxLayout.Y_AXIS));
        perishPanel.setBackground(UIFactory.CARD_BG);
        txtMfgDate = UIFactory.textField(); txtMfgDate.setText(LocalDate.now().format(FMT));
        txtExpDate = UIFactory.textField(); txtExpDate.setText(LocalDate.now().plusDays(180).format(FMT));
        perishPanel.add(row("Ngày sản xuất (dd/MM/yyyy):", txtMfgDate));
        perishPanel.add(row("Ngày hết hạn (dd/MM/yyyy):", txtExpDate));
        form.add(perishPanel);

        updateExtraVisibility(0);
        cboType.addActionListener(e -> updateExtraVisibility(cboType.getSelectedIndex()));

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnBar.setBackground(UIFactory.CARD_BG);
        JButton btnCancel = UIFactory.button("Hủy", new Color(149, 165, 166));
        btnCancel.addActionListener(e -> dispose());
        JButton btnOk = UIFactory.button(existingProduct == null ? "✓ Thêm sản phẩm" : "✓ Lưu thay đổi", UIFactory.SUCCESS);
        btnOk.addActionListener(e -> save());
        btnBar.add(btnCancel);
        btnBar.add(btnOk);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);
        root.add(btnBar, BorderLayout.SOUTH);
        setContentPane(root);
    }

    /** Creates a label + field pair as a vertical panel. */
    private JPanel row(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UIFactory.CARD_BG);
        p.setBorder(new EmptyBorder(4, 0, 8, 0));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = UIFactory.fieldLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private void updateExtraVisibility(int typeIdx) {
        promoPanel.setVisible(typeIdx == 1);
        perishPanel.setVisible(typeIdx == 2);
        revalidate();
        repaint();
    }

    private void fillForm(Product product) {
        txtName.setText(product.getName());
        txtPrice.setText(String.valueOf((long) product.getPrice()));
        txtStock.setText(String.valueOf(product.getStockQuantity()));
        txtUnit.setText(product.getUnit());
        if (product.getImportDate() != null) {
            txtImportDate.setText(product.getImportDate().format(FMT));
        }

        if (product instanceof PromotionProduct pp) {
            cboType.setSelectedIndex(1);
            txtDiscount.setText(String.valueOf(pp.getDiscountRate()));
            txtStartDate.setText(pp.getPromotionStartDate().format(FMT));
            txtEndDate.setText(pp.getPromotionEndDate().format(FMT));
            updateExtraVisibility(1);
        } else if (product instanceof PerishableProduct pp) {
            cboType.setSelectedIndex(2);
            txtMfgDate.setText(pp.getManufacturingDate().format(FMT));
            txtExpDate.setText(pp.getExpiryDate().format(FMT));
            updateExtraVisibility(2);
        }
    }

    private String catId() {
        String sel = (String) cboCategory.getSelectedItem();
        return sel == null ? "" : sel.split(" - ")[0];
    }

    private String supId() {
        String sel = (String) cboSupplier.getSelectedItem();
        return sel == null ? "" : sel.split(" - ")[0];
    }

    private void save() {
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String unit = txtUnit.getText().trim();
            if (unit.isEmpty()) throw new IllegalArgumentException("Đơn vị tính không được để trống!");

            String id = existingProduct != null ? existingProduct.getId()
                    : IdGenerator.nextId(IdGenerator.PRODUCT_PREFIX);
            int typeIdx = cboType.getSelectedIndex();

            if (typeIdx == 1) {
                double discount = Double.parseDouble(txtDiscount.getText().trim());
                LocalDate start = LocalDate.parse(txtStartDate.getText().trim(), FMT);
                LocalDate end = LocalDate.parse(txtEndDate.getText().trim(), FMT);
                result = ProductFactory.createPromotionProduct(id, name, price, stock, unit,
                        catId(), supId(), discount, start, end);
            } else if (typeIdx == 2) {
                LocalDate mfg = LocalDate.parse(txtMfgDate.getText().trim(), FMT);
                LocalDate exp = LocalDate.parse(txtExpDate.getText().trim(), FMT);
                result = ProductFactory.createPerishableProduct(id, name, price, stock, unit,
                        catId(), supId(), mfg, exp);
            } else {
                result = ProductFactory.createProduct(ProductType.REGULAR, id, name, price,
                        stock, unit, catId(), supId());
            }
            // Set ngày nhập
            try {
                LocalDate impDate = LocalDate.parse(txtImportDate.getText().trim(), FMT);
                result.setImportDate(impDate);
            } catch (Exception ignored) {
                result.setImportDate(LocalDate.now());
            }
            confirmed = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá và số lượng phải là số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày phải là dd/MM/yyyy!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() { return confirmed; }
    public Product getProduct() { return result; }
}
