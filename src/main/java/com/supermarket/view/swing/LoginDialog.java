package com.supermarket.view.swing;

import com.supermarket.exception.AuthenticationException;
import com.supermarket.model.User;
import com.supermarket.service.AuthService;
import com.supermarket.view.swing.panels.UIFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern login dialog — Phenikaa University brand theme.
 * Navy + Orange gradient background with floating white card.
 */
public class LoginDialog extends JDialog {

    private final AuthService authService;
    private User authenticatedUser = null;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public LoginDialog(Frame parent, AuthService authService) {
        super(parent, "Đăng Nhập — Phenikaa", true);
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setUndecorated(true);
        setSize(460, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setShape(new RoundRectangle2D.Double(0, 0, 460, 580, 20, 20));

        // ── Root: Phenikaa gradient background ─────────────────────────────
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Phenikaa navy → orange gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 51, 153),
                        getWidth(), getHeight(), new Color(243, 112, 33));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-60, -60, 250, 250);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(getWidth() - 130, getHeight() - 110, 220, 220);
                // Phenikaa pattern — subtle diagonal lines
                g2.setColor(new Color(255, 255, 255, 6));
                for (int i = -300; i < getWidth() + getHeight(); i += 40) {
                    g2.drawLine(i, 0, i - getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        root.setOpaque(false);

        // ── Card ───────────────────────────────────────────────────────────
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow layers
                for (int i = 0; i < 10; i++) {
                    g2.setColor(new Color(0, 0, 0, 10 - i));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 18 - i, 18 - i);
                }
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(360, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 42, 28, 42));

        // ── Logo area ──────────────────────────────────────────────────────
        JPanel logoArea = new JPanel(new GridBagLayout());
        logoArea.setOpaque(false);
        logoArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Phenikaa colored icon circle (navy → orange gradient)
        JPanel iconCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 51, 153),
                        getWidth(), getHeight(), new Color(243, 112, 33));
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(72, 72));
        iconCircle.setMaximumSize(new Dimension(72, 72));
        iconCircle.setLayout(new GridBagLayout());
        JLabel iconLbl = new JLabel("<html><font face='Segoe UI Emoji, Segoe UI Symbol, Symbola, Arial Unicode MS'>🏪</font></html>");
        iconLbl.setForeground(Color.WHITE);
        iconCircle.add(iconLbl);

        logoArea.add(iconCircle);

        JLabel appName = new JLabel("Siêu Thị Mini", SwingConstants.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(new Color(0, 51, 153));
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appSub = new JLabel("Hệ thống Quản lý Bán hàng — Phenikaa", SwingConstants.CENTER);
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        appSub.setForeground(UIFactory.TEXT_GRAY);
        appSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logoArea);
        card.add(Box.createVerticalStrut(10));
        card.add(appName);
        card.add(Box.createVerticalStrut(4));
        card.add(appSub);
        card.add(Box.createVerticalStrut(28));

        // ── Username ───────────────────────────────────────────────────────
        txtUsername = UIFactory.textField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel rowUser = UIFactory.formRow("👤  Tên đăng nhập", txtUsername);
        rowUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(rowUser);

        // ── Password ───────────────────────────────────────────────────────
        txtPassword = UIFactory.passwordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel rowPass = UIFactory.formRow("🔒  Mật khẩu", txtPassword);
        rowPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(rowPass);

        // ── Error label ────────────────────────────────────────────────────
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(UIFactory.DANGER);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        card.add(lblError);
        card.add(Box.createVerticalStrut(10));

        // ── Login button — Phenikaa orange accent ──────────────────────────
        JButton btnLogin = new JButton("ĐĂNG NHẬP") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = getModel().isRollover() ? new Color(210, 90, 20) : new Color(243, 112, 33);
                Color c2 = getModel().isRollover() ? new Color(0, 38, 115) : new Color(0, 51, 153);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(false); btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false); btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(16));

        // ── Hint ───────────────────────────────────────────────────────────
        JLabel hint = new JLabel("💡 Tài khoản: admin/admin123  |  nv01/nv123", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(UIFactory.TEXT_GRAY);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        card.add(hint);

        // ── Close button ───────────────────────────────────────────────────
        card.add(Box.createVerticalStrut(12));
        JButton btnClose = new JButton("Thoát");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setForeground(UIFactory.TEXT_GRAY);
        btnClose.setOpaque(false); btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false); btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnClose.addActionListener(e -> dispose());
        card.add(btnClose);

        // ── Actions ────────────────────────────────────────────────────────
        ActionListener doLogin = e -> performLogin();
        btnLogin.addActionListener(doLogin);
        txtPassword.addActionListener(doLogin);
        txtUsername.addActionListener(doLogin);

        root.add(card);
        setContentPane(root);

        // Allow drag to move window
        MouseAdapter drag = new MouseAdapter() {
            Point start;
            public void mousePressed(MouseEvent e) { start = e.getPoint(); }
            public void mouseDragged(MouseEvent e) {
                Point now = e.getLocationOnScreen();
                setLocation(now.x - start.x, now.y - start.y);
            }
        };
        root.addMouseListener(drag);
        root.addMouseMotionListener(drag);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("⚠ Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        try {
            authenticatedUser = authService.login(username, password);
            dispose();
        } catch (AuthenticationException ex) {
            lblError.setText("⚠ " + ex.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public User getAuthenticatedUser() { return authenticatedUser; }
}
