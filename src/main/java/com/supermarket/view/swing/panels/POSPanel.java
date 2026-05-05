package com.supermarket.view.swing.panels;

import com.supermarket.exception.OutOfStockException;
import com.supermarket.model.*;
import com.supermarket.model.enums.PaymentMethod;
import com.supermarket.service.*;
import com.supermarket.strategy.*;
import com.supermarket.util.IdGenerator;
import com.supermarket.util.InvoicePrinter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern POS Panel — clean split layout with product search on left, order summary on right.
 */
public class POSPanel extends JPanel {

    private final OrderService orderService;
    private final ProductService productService;
    private final CustomerService customerService;

    private Order currentOrder;
    private final Employee cashierStub;

    private JTable cartTable;
    private JTextField txtSearch, txtQty, txtCustSearch;
    private JLabel lblTotal, lblSubtotal, lblDiscount, lblOrderId, lblCustomer;
    private JPanel customerBadge;

    public POSPanel(OrderService orderService, ProductService productService,
                    CustomerService customerService) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
        this.cashierStub = new Employee("NV00", "pos", "", "Thu Ngân", "", 0, 0);
        this.currentOrder = orderService.createNewOrder(cashierStub);
        setLayout(new BorderLayout(0, 0));
        setBackground(UIFactory.BG);
        initUI();
    }

    private void initUI() {
        // ── TOP: header bar ───────────────────────────────────────────────
        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        // ── CENTER: left cart | right summary ─────────────────────────────
        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setBackground(UIFactory.BG);
        body.setBorder(new EmptyBorder(14, 18, 18, 18));

        body.add(buildCartSection(), BorderLayout.CENTER);
        body.add(buildSummaryPanel(), BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UIFactory.BORDER);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Title + order ID
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        JLabel title = new JLabel(UIFactory.formatEmojiHtml("🛒  Bán hàng (POS)"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIFactory.TEXT_DARK);
        lblOrderId = new JLabel("Mã HĐ: " + currentOrder.getId());
        lblOrderId.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblOrderId.setForeground(UIFactory.PRIMARY);
        left.add(title); left.add(lblOrderId);

        // Search row
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchRow.setOpaque(false);

        txtSearch = UIFactory.textField();
        txtSearch.setPreferredSize(new Dimension(300, 38));
        JLabel searchHint = new JLabel("Mã / Tên SP:");
        searchHint.setFont(UIFactory.FONT_LABEL);
        searchHint.setForeground(UIFactory.TEXT_MID);

        txtQty = UIFactory.textField();
        txtQty.setText("1");
        txtQty.setPreferredSize(new Dimension(56, 38));
        txtQty.setHorizontalAlignment(JTextField.CENTER);
        JLabel qLbl = new JLabel("SL:");
        qLbl.setFont(UIFactory.FONT_LABEL);
        qLbl.setForeground(UIFactory.TEXT_MID);

        JButton btnAdd = UIFactory.button("➕ Thêm vào giỏ", UIFactory.ACCENT, UIFactory.ACCENT_DARK);
        btnAdd.setPreferredSize(new Dimension(140, 38));
        btnAdd.addActionListener(e -> addToCart());
        txtSearch.addActionListener(e -> addToCart());

        searchRow.add(searchHint); searchRow.add(txtSearch);
        searchRow.add(qLbl); searchRow.add(txtQty);
        searchRow.add(btnAdd);

        h.add(left, BorderLayout.WEST);
        h.add(searchRow, BorderLayout.EAST);
        return h;
    }

    private JPanel buildCartSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, UIFactory.BORDER),
                BorderFactory.createEmptyBorder()));

        // Cart header
        JPanel cartHdr = new JPanel(new BorderLayout());
        cartHdr.setBackground(UIFactory.TABLE_HDR);
        cartHdr.setBorder(new EmptyBorder(10, 16, 10, 16));
        JLabel cartLbl = new JLabel("GIỎ HÀNG");
        cartLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cartLbl.setForeground(new Color(226, 232, 240));
        JPanel cartBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        cartBtns.setOpaque(false);
        JButton btnRemove = UIFactory.button("✖ Xóa dòng", UIFactory.DANGER, UIFactory.DANGER_DARK);
        JButton btnClear  = UIFactory.button("🗑 Xóa tất cả", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRemove.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnClear.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        cartBtns.add(btnRemove); cartBtns.add(btnClear);
        cartHdr.add(cartLbl, BorderLayout.WEST);
        cartHdr.add(cartBtns, BorderLayout.EAST);

        // Table
        String[] cols = {"Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        cartTable = UIFactory.createTable(cols);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Empty state label
        JLabel emptyLbl = new JLabel("Chưa có sản phẩm nào trong giỏ hàng", SwingConstants.CENTER);
        emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        emptyLbl.setForeground(UIFactory.TEXT_GRAY);

        JScrollPane scroll = UIFactory.scrollTable(cartTable);

        panel.add(cartHdr, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIFactory.BG);
        panel.setPreferredSize(new Dimension(310, 0));

        // ── Customer card ─────────────────────────────────────────────────
        JPanel custCard = new JPanel();
        custCard.setLayout(new BoxLayout(custCard, BoxLayout.Y_AXIS));
        custCard.setBackground(Color.WHITE);
        custCard.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, UIFactory.BORDER),
                new EmptyBorder(14, 16, 14, 16)));
        custCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        custCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel custTitle = new JLabel(UIFactory.formatEmojiHtml("👤  Khách hàng"));
        custTitle.setFont(UIFactory.FONT_BOLD);
        custTitle.setForeground(UIFactory.TEXT_DARK);
        custCard.add(custTitle);
        custCard.add(Box.createVerticalStrut(10));

        lblCustomer = new JLabel("Khách vãng lai");
        lblCustomer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCustomer.setForeground(UIFactory.TEXT_GRAY);
        custCard.add(lblCustomer);
        custCard.add(Box.createVerticalStrut(10));

        JPanel custRow = new JPanel(new BorderLayout(6, 0));
        custRow.setOpaque(false);
        txtCustSearch = UIFactory.textField();
        JButton btnCust = UIFactory.button("Gắn KH", UIFactory.PURPLE, UIFactory.PURPLE_DARK);
        btnCust.setBorder(new EmptyBorder(7, 12, 7, 12));
        btnCust.addActionListener(e -> attachCustomer(txtCustSearch.getText().trim()));
        custRow.add(txtCustSearch, BorderLayout.CENTER);
        custRow.add(btnCust, BorderLayout.EAST);
        custCard.add(custRow);

        // ── Order total card ──────────────────────────────────────────────
        JPanel totalCard = new JPanel();
        totalCard.setLayout(new BoxLayout(totalCard, BoxLayout.Y_AXIS));
        totalCard.setBackground(Color.WHITE);
        totalCard.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, UIFactory.BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        totalCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sumTitle = new JLabel(UIFactory.formatEmojiHtml("💰  Tóm tắt đơn hàng"));
        sumTitle.setFont(UIFactory.FONT_BOLD);
        sumTitle.setForeground(UIFactory.TEXT_DARK);
        totalCard.add(sumTitle);
        totalCard.add(Box.createVerticalStrut(14));

        lblSubtotal  = summaryRow(totalCard, "Tạm tính:",     "0 đ", UIFactory.TEXT_MID);
        lblDiscount  = summaryRow(totalCard, "Giảm giá KH:",  "0 đ", UIFactory.DANGER);
        totalCard.add(Box.createVerticalStrut(8));

        // Divider
        JPanel div = new JPanel();
        div.setBackground(UIFactory.BORDER);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalCard.add(div);
        totalCard.add(Box.createVerticalStrut(10));

        // Grand total
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel lTotal = new JLabel("TỔNG CỘNG");
        lTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lTotal.setForeground(UIFactory.TEXT_DARK);
        lblTotal = new JLabel("0 đ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(UIFactory.DANGER);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        totalRow.add(lTotal,   BorderLayout.WEST);
        totalRow.add(lblTotal, BorderLayout.EAST);
        totalCard.add(totalRow);

        // ── Checkout button ───────────────────────────────────────────────
        JButton btnCheckout = new JButton(UIFactory.formatEmojiHtml("💳   THANH TOÁN NGAY")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = getModel().isRollover() ? UIFactory.ACCENT_DARK : UIFactory.ACCENT;
                Color c2 = getModel().isRollover() ? new Color(0, 38, 115) : new Color(0, 51, 153);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setOpaque(false); btnCheckout.setContentAreaFilled(false);
        btnCheckout.setBorderPainted(false); btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.setBorder(new EmptyBorder(14, 0, 14, 0));
        btnCheckout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCheckout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btnCheckout.addActionListener(e -> checkout());

        panel.add(custCard);
        panel.add(Box.createVerticalStrut(12));
        panel.add(totalCard);
        panel.add(Box.createVerticalStrut(12));
        panel.add(btnCheckout);
        return panel;
    }

    /** Adds a two-column summary row and returns the value JLabel. */
    private JLabel summaryRow(JPanel parent, String label, String value, Color valColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lKey = new JLabel(label);
        lKey.setFont(UIFactory.FONT_BODY); lKey.setForeground(UIFactory.TEXT_MID);
        JLabel lVal = new JLabel(value);
        lVal.setFont(UIFactory.FONT_BOLD); lVal.setForeground(valColor);
        lVal.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(lKey, BorderLayout.WEST); row.add(lVal, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
        return lVal;
    }

    // ── Logic methods ─────────────────────────────────────────────────────

    private void addToCart() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) return;
        Product product = null;
        try {
            product = productService.getProduct(kw);
        } catch (Exception ignored) {
            List<Product> results = productService.searchByName(kw);
            if (results.isEmpty()) { JOptionPane.showMessageDialog(this, "Không tìm thấy: " + kw); return; }
            if (results.size() == 1) { product = results.get(0); }
            else {
                String[] opts = results.stream().map(p -> "[" + p.getId() + "] " + p.getName() + " — " + UIFactory.vnd(p.calculateFinalPrice())).toArray(String[]::new);
                String chosen = (String) JOptionPane.showInputDialog(this, "Chọn sản phẩm:", "Kết quả tìm kiếm", JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
                if (chosen == null) return;
                product = productService.getProduct(chosen.substring(1, chosen.indexOf(']')));
            }
        }
        int qty = 1;
        try { qty = Math.max(1, Integer.parseInt(txtQty.getText().trim())); } catch (Exception ignored) {}
        try {
            orderService.addToCart(currentOrder, product.getId(), qty);
            refreshCart(); txtSearch.setText(""); txtQty.setText("1"); txtSearch.requestFocus();
        } catch (OutOfStockException e) { JOptionPane.showMessageDialog(this, e.getMessage(), "Không đủ hàng", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); }
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row < 0) return;
        String pid = (String) UIFactory.getModel(cartTable).getValueAt(row, 0);
        currentOrder.removeItem(pid); refreshCart();
    }

    private void clearCart() {
        if (currentOrder.getItems().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa toàn bộ giỏ hàng?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            currentOrder.getItems().clear(); refreshCart();
        }
    }

    private void attachCustomer(String query) {
        if (query.isEmpty()) return;
        Customer c = customerService.findByPhone(query);
        if (c == null) { try { c = customerService.getCustomer(query); } catch (Exception ignored) {} }
        if (c == null) {
            if (JOptionPane.showConfirmDialog(this, "Không tìm thấy KH. Tạo mới?", "KH mới", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String name = JOptionPane.showInputDialog(this, "Họ tên KH:");
                if (name == null || name.trim().isEmpty()) return;
                c = new Customer(IdGenerator.nextId(IdGenerator.CUSTOMER_PREFIX), name.trim(), query, "");
                customerService.addCustomer(c);
            } else return;
        }
        orderService.attachCustomer(currentOrder, c.getId());
        lblCustomer.setText(c.getName() + "  •  " + c.getTier().getDisplayName() + "  •  -" + String.format("%.0f%%", c.getDiscountRate()*100));
        lblCustomer.setFont(UIFactory.FONT_BOLD);
        lblCustomer.setForeground(UIFactory.SUCCESS);
        refreshCart();
    }

    private void refreshCart() {
        DefaultTableModel m = UIFactory.getModel(cartTable);
        m.setRowCount(0);
        for (OrderItem item : currentOrder.getItems()) {
            m.addRow(new Object[]{item.getProductId(), item.getProductName(),
                    UIFactory.vnd(item.getUnitPrice()), item.getQuantity(), UIFactory.vnd(item.getSubTotal())});
        }
        lblSubtotal.setText(UIFactory.vnd(currentOrder.getSubTotal()));
        lblDiscount.setText("- " + UIFactory.vnd(currentOrder.getCustomerDiscount()));
        lblTotal.setText(UIFactory.vnd(currentOrder.getFinalAmount()));
    }

    private void checkout() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] methods = {"💵  Tiền mặt", "💳  Thẻ ngân hàng", "📱  Ví MoMo", "📱  ZaloPay", "📲  VietQR"};
        String pmChoice = (String) JOptionPane.showInputDialog(this,
                "Tổng thanh toán: " + UIFactory.vnd(currentOrder.getFinalAmount()) + "\n\nChọn phương thức:",
                "Thanh toán", JOptionPane.PLAIN_MESSAGE, null, methods, methods[0]);
        if (pmChoice == null) return;

        PaymentStrategy strategy; PaymentMethod method;
        if (pmChoice.contains("Tiền mặt")) {
            method = PaymentMethod.CASH;
            String cs = JOptionPane.showInputDialog(this, "Tiền khách đưa (VND):");
            if (cs == null) return;
            try { strategy = new CashPayment(Double.parseDouble(cs.replaceAll("[^0-9.]", ""))); }
            catch (Exception e) { return; }
        } else if (pmChoice.contains("Thẻ")) {
            method = PaymentMethod.CARD;
            strategy = new CardPayment(JOptionPane.showInputDialog(this, "Số thẻ:"), "");
        } else if (pmChoice.contains("MoMo")) {
            method = PaymentMethod.MOMO; strategy = new EWalletPayment("MoMo", "");
        } else if (pmChoice.contains("Zalo")) {
            method = PaymentMethod.ZALOPAY; strategy = new EWalletPayment("ZaloPay", "");
        } else {
            method = PaymentMethod.VIETQR; strategy = new EWalletPayment("VietQR", "");
        }

        strategy.processPayment(currentOrder.getFinalAmount());
        orderService.completeOrder(currentOrder, method, strategy.getPaymentDetails());
        cashierStub.addSaleAmount(currentOrder.getFinalAmount());

        // Show invoice
        JTextArea ta = new JTextArea(InvoicePrinter.generateInvoiceString(currentOrder));
        ta.setFont(new Font("Courier New", Font.PLAIN, 12)); ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta); sp.setPreferredSize(new Dimension(520, 460));
        Object[] opts = {"Xuất file .txt", "Đóng"};
        if (JOptionPane.showOptionDialog(this, sp, "🧾 Hóa Đơn Bán Hàng", JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, opts, opts[1]) == 0) {
            String fn = "hoadon_" + currentOrder.getId() + ".txt";
            InvoicePrinter.exportToFile(currentOrder, fn);
            JOptionPane.showMessageDialog(this, "✅ Đã xuất: " + fn);
        }

        currentOrder = orderService.createNewOrder(cashierStub);
        refreshCart();
        lblOrderId.setText("Mã HĐ: " + currentOrder.getId());
        lblCustomer.setText("Khách vãng lai");
        lblCustomer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCustomer.setForeground(UIFactory.TEXT_GRAY);
        txtCustSearch.setText("");
    }
}
