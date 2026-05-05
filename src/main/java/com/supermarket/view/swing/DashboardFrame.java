package com.supermarket.view.swing;

import com.supermarket.model.User;
import com.supermarket.model.enums.UserRole;
import com.supermarket.service.*;
import com.supermarket.view.swing.panels.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Main application frame — Phenikaa University theme.
 * Navy sidebar + orange accent topbar + CardLayout content.
 */
public class DashboardFrame extends JFrame {

    // ── Phenikaa Sidebar palette ─────────────────────────────────────────
    private static final Color SB_BG      = new Color(0, 35, 102);       // Very dark navy
    private static final Color SB_ACTIVE  = new Color(243, 112, 33);     // Phenikaa orange
    private static final Color SB_HOVER   = new Color(0, 50, 130);       // Slightly lighter navy
    private static final Color SB_TEXT    = new Color(160, 180, 220);    // Muted blue-gray
    private static final Color SB_TEXT_A  = Color.WHITE;
    private static final Color SB_SECTION = new Color(80, 110, 170);     // Section label blue
    private static final Color TOP_FROM   = new Color(0, 51, 153);       // Phenikaa navy
    private static final Color TOP_TO     = new Color(243, 112, 33);     // Phenikaa orange

    private final User currentUser;
    private final AuthService authService;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel   = new JPanel(cardLayout);
    private JButton activeBtn = null;
    private JLabel lblClock;

    public DashboardFrame(User currentUser, AuthService authService,
                          ProductService productService, CategoryService categoryService,
                          SupplierService supplierService, EmployeeService employeeService,
                          CustomerService customerService, OrderService orderService,
                          GoodsImportService importService, ReportService reportService) {
        this.currentUser = currentUser;
        this.authService = authService;

        setTitle("Hệ Thống Quản Lý Siêu Thị — Phenikaa University");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        try {
            java.net.URL iconUrl = getClass().getResource("/icon.png");
            if (iconUrl != null) setIconImage(new ImageIcon(iconUrl).getImage());
        } catch (Exception ignored) {}

        // ── Root layout ────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIFactory.BG);

        root.add(buildTopBar(),   BorderLayout.NORTH);
        root.add(buildSidebar(productService, categoryService, supplierService,
                              employeeService, customerService, orderService,
                              importService, reportService), BorderLayout.WEST);

        // Content
        contentPanel.setBackground(UIFactory.BG);
        contentPanel.add(new POSPanel(orderService, productService, customerService),             "POS");
        contentPanel.add(new ProductPanel(productService, categoryService, supplierService),      "PRODUCT");
        contentPanel.add(new CategoryPanel(categoryService),                                      "CATEGORY");
        contentPanel.add(new SupplierPanel(supplierService),                                      "SUPPLIER");
        contentPanel.add(new CustomerPanel(customerService),                                      "CUSTOMER");
        contentPanel.add(new EmployeePanel(employeeService),                                      "EMPLOYEE");
        contentPanel.add(new ImportPanel(importService, productService, supplierService, currentUser), "IMPORT");
        contentPanel.add(new ReportPanel(reportService, orderService),                            "REPORT");
        cardLayout.show(contentPanel, "POS");

        root.add(contentPanel, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        startClock();
    }

    // ── Top bar — Phenikaa gradient ────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, TOP_FROM, getWidth(), 0, TOP_TO);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Left — logo
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel iconLbl = new JLabel("🏪");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        JLabel logoTxt = new JLabel("SIÊU THỊ MINI");
        logoTxt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoTxt.setForeground(Color.WHITE);
        JLabel tagline = new JLabel("  |  Quản lý Bán hàng — PHENIKAA");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(255, 255, 255, 180));
        left.add(iconLbl); left.add(logoTxt); left.add(tagline);

        // Right — user + clock + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblClock.setForeground(new Color(255, 255, 255, 210));
        lblClock.setPreferredSize(new Dimension(140, 32));

        JPanel userPill = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose(); super.paintComponent(g);
            }
        };
        userPill.setOpaque(false);
        userPill.setBorder(new EmptyBorder(5, 12, 5, 12));
        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel userName = new JLabel(currentUser.getFullName() + "  (" + currentUser.getRole().getDisplayName() + ")");
        userName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userName.setForeground(Color.WHITE);
        userPill.add(avatar); userPill.add(userName);

        JButton btnLogout = new JButton(UIFactory.formatEmojiHtml("⏻  Đăng xuất")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isRollover() ? new Color(190, 30, 30) : new Color(220, 53, 53);
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setOpaque(false); btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false); btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(new EmptyBorder(7, 16, 7, 16));
        btnLogout.addActionListener(e -> logout());

        right.add(lblClock); right.add(userPill); right.add(btnLogout);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar — Phenikaa dark navy ──────────────────────────────────────
    private JPanel buildSidebar(ProductService productService, CategoryService categoryService,
                                SupplierService supplierService, EmployeeService employeeService,
                                CustomerService customerService, OrderService orderService,
                                GoodsImportService importService, ReportService reportService) {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, SB_BG, 0, getHeight(), new Color(0, 25, 75));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right edge highlight
                g2.setColor(new Color(243, 112, 33, 25));
                g2.fillRect(getWidth() - 2, 0, 2, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));

        sectionLabel(sidebar, "BÁN HÀNG");

        JButton btnPOS = navBtn("🛒", "Bán hàng (POS)", "POS");
        sidebar.add(btnPOS);
        activateBtn(btnPOS);

        sectionLabel(sidebar, "QUẢN LÝ HÀNG HÓA");
        sidebar.add(navBtn("📦", "Sản phẩm", "PRODUCT"));
        sidebar.add(navBtn("🏷️", "Danh mục", "CATEGORY"));
        
        if (currentUser.getRole() == com.supermarket.model.enums.UserRole.ADMIN) {
            sidebar.add(navBtn("🚛", "Nhà cung cấp", "SUPPLIER"));
            sidebar.add(navBtn("📥", "Nhập hàng", "IMPORT"));
        }

        sectionLabel(sidebar, "KHÁCH HÀNG & BÁO CÁO");
        sidebar.add(navBtn("👥", "Khách hàng", "CUSTOMER"));
        sidebar.add(navBtn("📊", "Báo cáo & Thống kê", "REPORT"));

        if (currentUser.getRole() == UserRole.ADMIN) {
            sidebar.add(Box.createVerticalStrut(8));
            JPanel sepLine = new JPanel();
            sepLine.setMaximumSize(new Dimension(220, 1));
            sepLine.setBackground(new Color(255, 255, 255, 15));
            sidebar.add(sepLine);
            sectionLabel(sidebar, "QUẢN TRỊ HỆ THỐNG");
            sidebar.add(navBtn("👤", "Nhân viên", "EMPLOYEE"));
        }

        sidebar.add(Box.createVerticalGlue());

        // Version footer with Phenikaa branding
        JLabel ver = new JLabel("  PHENIKAA  •  OOP Project", SwingConstants.LEFT);
        ver.setFont(new Font("Segoe UI", Font.BOLD, 9));
        ver.setForeground(new Color(243, 112, 33, 120));
        ver.setBorder(new EmptyBorder(0, 18, 0, 0));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(ver);

        JLabel ver2 = new JLabel("  v2.0  •  Quản lý Siêu thị", SwingConstants.LEFT);
        ver2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        ver2.setForeground(SB_SECTION);
        ver2.setBorder(new EmptyBorder(2, 18, 0, 0));
        ver2.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(ver2);

        return sidebar;
    }

    private void sectionLabel(JPanel parent, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbl.setForeground(SB_SECTION);
        lbl.setBorder(new EmptyBorder(12, 18, 6, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
    }

    private JButton navBtn(String icon, String text, String card) {
        JButton btn = new JButton() {
            boolean hovered = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isActive = this == activeBtn;
                if (isActive) {
                    // Active: Phenikaa orange pill
                    g2.setColor(SB_ACTIVE);
                    g2.fillRoundRect(10, 3, getWidth() - 20, getHeight() - 6, 10, 10);
                    // Left indicator bar (orange)
                    g2.setColor(new Color(243, 112, 33));
                    g2.fillRoundRect(0, 6, 4, getHeight() - 12, 4, 4);
                } else if (hovered) {
                    g2.setColor(SB_HOVER);
                    g2.fillRoundRect(10, 3, getWidth() - 20, getHeight() - 6, 10, 10);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel("<html><font face='Segoe UI Emoji'>" + icon + "</font></html>");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        iconLbl.setForeground(SB_TEXT);
        iconLbl.setPreferredSize(new Dimension(52, 44));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLbl.setForeground(SB_TEXT);

        btn.add(iconLbl); btn.add(textLbl);

        btn.addActionListener(e -> {
            activateBtn(btn);
            cardLayout.show(contentPanel, card);
        });
        return btn;
    }

    private void activateBtn(JButton btn) {
        JButton oldBtn = activeBtn;
        // Reset old button styling
        if (oldBtn != null && oldBtn != btn) {
            for (Component c : oldBtn.getComponents()) {
                if (c instanceof JLabel lbl) lbl.setForeground(SB_TEXT);
            }
            oldBtn.repaint();
        }
        // Activate new button
        activeBtn = btn;
        for (Component c : btn.getComponents()) {
            if (c instanceof JLabel lbl) lbl.setForeground(SB_TEXT_A);
        }
        btn.repaint();
    }

    // ── Status bar — Phenikaa branded ─────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0, 35, 102));
        bar.setPreferredSize(new Dimension(0, 28));
        bar.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel left = new JLabel(UIFactory.formatEmojiHtml("✅  Hệ thống hoạt động bình thường"));
        left.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        left.setForeground(new Color(130, 160, 210));

        JLabel right = new JLabel("© 2026 Đại học Phenikaa — Quản lý Siêu thị Mini");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        right.setForeground(new Color(243, 112, 33, 150));

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timer t = new Timer(1000, e -> lblClock.setText(UIFactory.formatEmojiHtml("🕐  " + LocalTime.now().format(fmt))));
        t.start();
        lblClock.setText(UIFactory.formatEmojiHtml("🕐  " + LocalTime.now().format(fmt)));
    }

    private void logout() {
        int res = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất không?", "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            JOptionPane.showMessageDialog(null,
                    "Đã đăng xuất thành công!\nVui lòng khởi động lại ứng dụng.",
                    "Đăng xuất", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}
