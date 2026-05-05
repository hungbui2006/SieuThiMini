package com.supermarket.view.swing.panels;

import com.supermarket.model.*;
import com.supermarket.service.*;
import com.supermarket.util.InvoicePrinter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Modern Report panel with KPI cards + tabbed content.
 * Chức năng: Thống kê doanh thu, tìm kiếm theo ngày, top bán chạy, tồn kho.
 */
public class ReportPanel extends JPanel {

    private final ReportService reportService;
    private final OrderService orderService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportPanel(ReportService reportService, OrderService orderService) {
        this.reportService = reportService;
        this.orderService = orderService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UIFactory.BG);
        initUI();
    }

    private void initUI() {
        // ── Header ────────────────────────────────────────────────────────
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(Color.WHITE);
        hdr.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIFactory.BORDER),
                new EmptyBorder(16, 24, 16, 24)));
        JLabel title = UIFactory.sectionTitle("📊  Báo cáo & Thống kê");
        JButton btnRefresh = UIFactory.button("↻  Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnRefresh.addActionListener(e -> refresh());
        hdr.add(title, BorderLayout.WEST);
        hdr.add(btnRefresh, BorderLayout.EAST);
        add(hdr, BorderLayout.NORTH);

        // ── Body ──────────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(UIFactory.BG);
        body.setBorder(new EmptyBorder(18, 18, 18, 18));

        // KPI cards row
        body.add(buildKpiRow(), BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = buildTabs();
        tabs.setFont(UIFactory.FONT_BOLD);
        body.add(tabs, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
    }

    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 110));

        double totalRev = reportService.getTotalRevenue();
        int totalOrders = reportService.getTotalOrderCount();
        double todayRev = reportService.getRevenueByDate(LocalDate.now());
        int lowStockCount = reportService.getLowStockReport().size();

        row.add(UIFactory.statCard("TỔNG DOANH THU", UIFactory.vnd(totalRev), "Tất cả thời gian",
                new Color(37, 99, 235), new Color(99, 102, 241)));
        row.add(UIFactory.statCard("DOANH THU HÔM NAY", UIFactory.vnd(todayRev), LocalDate.now().format(FMT),
                new Color(16, 185, 129), new Color(6, 182, 212)));
        row.add(UIFactory.statCard("SỐ HÓA ĐƠN", String.valueOf(totalOrders), "Đã hoàn thành",
                new Color(245, 158, 11), new Color(239, 68, 68)));
        row.add(UIFactory.statCard("TỒN KHO THẤP", String.valueOf(lowStockCount), "Cần nhập thêm",
                new Color(139, 92, 246), new Color(236, 72, 153)));
        return row;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Color.WHITE);

        tabs.addTab(UIFactory.formatEmojiHtml("💰  Doanh thu theo ngày"), buildRevenueTab());
        tabs.addTab(UIFactory.formatEmojiHtml("🏆  Bán chạy nhất"), buildTopSellingTab());
        tabs.addTab(UIFactory.formatEmojiHtml("👔  Theo nhân viên"), buildEmpSalesTab());
        tabs.addTab(UIFactory.formatEmojiHtml("📦  Tồn kho"), buildInventoryTab());
        tabs.addTab(UIFactory.formatEmojiHtml("🧾  Lịch sử hóa đơn"), buildOrderHistTab());
        return tabs;
    }

    // ── Revenue tab ───────────────────────────────────────────────────────
    private JPanel buildRevenueTab() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel queryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        queryRow.setBackground(Color.WHITE);

        JTextField fFrom = UIFactory.textField();
        fFrom.setPreferredSize(new Dimension(120, 36));
        fFrom.setText(LocalDate.now().withDayOfMonth(1).format(FMT));
        JTextField fTo = UIFactory.textField();
        fTo.setPreferredSize(new Dimension(120, 36));
        fTo.setText(LocalDate.now().format(FMT));

        JLabel resultLbl = new JLabel();
        resultLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultLbl.setForeground(UIFactory.PRIMARY);

        JButton btnQ = UIFactory.button("Xem doanh thu", UIFactory.PRIMARY, UIFactory.PRIMARY_DARK);
        btnQ.addActionListener(e -> {
            try {
                LocalDate from = LocalDate.parse(fFrom.getText().trim(), FMT);
                LocalDate to = LocalDate.parse(fTo.getText().trim(), FMT);
                double rev = reportService.getRevenueByDateRange(from, to);
                int count = (int) orderService.getOrdersByDateRange(from, to).stream()
                        .filter(Order::isCompleted).count();
                resultLbl.setText("  →  " + UIFactory.vnd(rev) + "  (" + count + " hóa đơn)");
            } catch (Exception ex) {
                resultLbl.setText("  ⚠ Định dạng sai (dd/MM/yyyy)!");
            }
        });

        queryRow.add(fieldLbl("Từ ngày:"));
        queryRow.add(fFrom);
        queryRow.add(fieldLbl("Đến ngày:"));
        queryRow.add(fTo);
        queryRow.add(btnQ);
        queryRow.add(resultLbl);
        p.add(queryRow, BorderLayout.NORTH);
        return p;
    }

    // ── Top selling tab ───────────────────────────────────────────────────
    private JPanel buildTopSellingTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"Hạng", "Sản phẩm", "Số lượng đã bán"};
        JTable t = UIFactory.createTable(cols);

        JButton btnLoad = UIFactory.button("Tải top 10 bán chạy", UIFactory.PRIMARY, UIFactory.PRIMARY_DARK);
        btnLoad.addActionListener(e -> {
            UIFactory.getModel(t).setRowCount(0);
            int rank = 1;
            for (Map.Entry<String, Integer> entry : reportService.getTopSellingProducts(10))
                UIFactory.getModel(t).addRow(new Object[]{"#" + rank++, entry.getKey(), entry.getValue()});
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.add(btnLoad);

        p.add(top, BorderLayout.NORTH);
        p.add(UIFactory.scrollTable(t), BorderLayout.CENTER);
        return p;
    }

    // ── Employee sales tab ────────────────────────────────────────────────
    private JPanel buildEmpSalesTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"Nhân viên", "Doanh số"};
        JTable t = UIFactory.createTable(cols);
        loadEmpTable(t);

        JButton btnR = UIFactory.button("↻  Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnR.addActionListener(e -> loadEmpTable(t));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.add(btnR);

        p.add(top, BorderLayout.NORTH);
        p.add(UIFactory.scrollTable(t), BorderLayout.CENTER);
        return p;
    }

    private void loadEmpTable(JTable t) {
        UIFactory.getModel(t).setRowCount(0);
        for (Map.Entry<String, Double> e : reportService.getSalesByEmployee().entrySet())
            UIFactory.getModel(t).addRow(new Object[]{e.getKey(), UIFactory.vnd(e.getValue())});
    }

    // ── Inventory tab ─────────────────────────────────────────────────────
    private JPanel buildInventoryTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho", "ĐVT", "Ngày nhập", "Trạng thái"};
        JTable t = UIFactory.createTable(cols);
        loadInvTable(t);

        JButton btnR = UIFactory.button("↻  Làm mới", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnR.addActionListener(e -> loadInvTable(t));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.add(btnR);

        p.add(top, BorderLayout.NORTH);
        p.add(UIFactory.scrollTable(t), BorderLayout.CENTER);
        return p;
    }

    private void loadInvTable(JTable t) {
        UIFactory.getModel(t).setRowCount(0);
        for (Product p : reportService.getInventoryReport()) {
            String status = p.isLowStock() ? "⚠ Tồn thấp" : "✅ Bình thường";
            if (p instanceof PerishableProduct pp && pp.isExpired()) status = "❌ Hết hạn";
            UIFactory.getModel(t).addRow(new Object[]{
                    p.getId(), p.getName(),
                    UIFactory.vnd(p.calculateFinalPrice()),
                    p.getStockQuantity(), p.getUnit(),
                    UIFactory.fmtDate(p.getImportDate()),
                    status
            });
        }
    }

    // ── Order history tab with search by date ─────────────────────────────
    private JPanel buildOrderHistTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = {"Mã HĐ", "Ngày bán", "Thu ngân", "Khách hàng", "Tổng tiền", "Thanh toán"};
        JTable t = UIFactory.createTable(cols);
        loadOrderTable(t, null, null);

        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // ── Top row with search + actions ──
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setBackground(Color.WHITE);

        JButton btnR = UIFactory.button("↻  Tất cả", UIFactory.GRAY_BTN, UIFactory.GRAY_BTN_DK);
        btnR.addActionListener(e -> loadOrderTable(t, null, null));

        // Tìm kiếm theo ngày bán
        JTextField fDateFrom = UIFactory.textField();
        fDateFrom.setPreferredSize(new Dimension(110, 36));
        fDateFrom.setText(LocalDate.now().format(FMT));
        JTextField fDateTo = UIFactory.textField();
        fDateTo.setPreferredSize(new Dimension(110, 36));
        fDateTo.setText(LocalDate.now().format(FMT));

        JButton btnDateSearch = UIFactory.button("📅 Tìm theo ngày bán", UIFactory.TEAL, UIFactory.TEAL_DARK);
        btnDateSearch.addActionListener(e -> {
            try {
                LocalDate from = LocalDate.parse(fDateFrom.getText().trim(), FMT);
                LocalDate to = LocalDate.parse(fDateTo.getText().trim(), FMT);
                loadOrderTable(t, from, to);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Định dạng ngày sai! (dd/MM/yyyy)");
            }
        });

        JButton btnPrint = UIFactory.button("🖨  In lại hóa đơn", UIFactory.PURPLE, UIFactory.PURPLE_DARK);
        btnPrint.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(p, "Chọn hóa đơn cần in!");
                return;
            }
            String id = (String) UIFactory.getModel(t).getValueAt(row, 0);
            try {
                JTextArea ta = new JTextArea(InvoicePrinter.generateInvoiceString(orderService.getOrder(id)));
                ta.setFont(new Font("Courier New", Font.PLAIN, 12));
                ta.setEditable(false);
                JScrollPane sp = new JScrollPane(ta);
                sp.setPreferredSize(new Dimension(520, 460));
                JOptionPane.showMessageDialog(p, sp, "Hóa đơn " + id, JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, ex.getMessage());
            }
        });

        top.add(btnR);
        top.add(Box.createHorizontalStrut(8));
        top.add(fieldLbl("Từ:"));
        top.add(fDateFrom);
        top.add(fieldLbl("Đến:"));
        top.add(fDateTo);
        top.add(btnDateSearch);
        top.add(Box.createHorizontalStrut(8));
        top.add(btnPrint);

        p.add(top, BorderLayout.NORTH);
        p.add(UIFactory.scrollTable(t), BorderLayout.CENTER);
        return p;
    }

    private void loadOrderTable(JTable t, LocalDate from, LocalDate to) {
        UIFactory.getModel(t).setRowCount(0);
        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Order> orders;
        if (from != null && to != null) {
            orders = orderService.getOrdersByDateRange(from, to);
        } else {
            orders = orderService.getAllOrders();
        }
        for (Order o : orders)
            UIFactory.getModel(t).addRow(new Object[]{
                    o.getId(),
                    o.getOrderDate().format(dtFmt),
                    o.getEmployeeName(),
                    o.getCustomerName() != null ? o.getCustomerName() : "Khách vãng lai",
                    UIFactory.vnd(o.getFinalAmount()),
                    o.getPaymentMethod() != null ? o.getPaymentMethod().getDisplayName() : "—"
            });
    }

    private void refresh() {
        removeAll();
        initUI();
        revalidate();
        repaint();
    }

    private JLabel fieldLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UIFactory.FONT_LABEL);
        l.setForeground(UIFactory.TEXT_MID);
        return l;
    }
}
