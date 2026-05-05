package com.supermarket.view.swing.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Shared UI factory — Phenikaa University brand theme.
 * Bảng màu: Xanh dương đậm (navy) + Cam (orange) theo nhận diện thương hiệu Phenikaa.
 */
public class UIFactory {
    
    /**
     * Loads an icon from resources and scales it.
     */
    public static ImageIcon getIcon(String name, int size) {
        try {
            java.net.URL imgUrl = UIFactory.class.getResource("/icons/" + name + ".png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + name);
        }
        return null;
    }

    // ── PHENIKAA Palette ───────────────────────────────────────────────────
    public static final Color PRIMARY      = new Color(0, 51, 153);      // Phenikaa Navy Blue
    public static final Color PRIMARY_DARK = new Color(0, 38, 115);      // Darker navy
    public static final Color PRIMARY_LIGHT= new Color(23, 75, 180);     // Lighter navy
    public static final Color ACCENT       = new Color(243, 112, 33);    // Phenikaa Orange
    public static final Color ACCENT_DARK  = new Color(210, 90, 20);     // Darker orange
    public static final Color ACCENT_LIGHT = new Color(255, 140, 60);    // Lighter orange

    public static final Color SUCCESS      = new Color(16, 185, 129);    // emerald-500
    public static final Color SUCCESS_DARK = new Color(5,  150, 105);
    public static final Color DANGER       = new Color(220, 53,  53);    // red
    public static final Color DANGER_DARK  = new Color(190, 30,  30);
    public static final Color WARNING      = new Color(245, 158, 11);    // amber
    public static final Color WARNING_DARK = new Color(217, 119,  6);
    public static final Color PURPLE       = new Color(109, 75, 200);
    public static final Color PURPLE_DARK  = new Color(85, 50, 170);
    public static final Color GRAY_BTN     = new Color(100, 116, 139);
    public static final Color GRAY_BTN_DK  = new Color(71,  85, 105);
    public static final Color TEAL         = new Color(20, 150, 136);
    public static final Color TEAL_DARK    = new Color(10, 120, 110);

    public static final Color BG           = new Color(240, 243, 248);   // Light grayish blue
    public static final Color CARD_BG      = Color.WHITE;
    public static final Color TEXT_DARK    = new Color(15,  23,  42);    // slate-900
    public static final Color TEXT_MID     = new Color(71,  85, 105);    // slate-600
    public static final Color TEXT_GRAY    = new Color(148, 163, 184);   // slate-400
    public static final Color BORDER       = new Color(220, 225, 235);   // subtle border
    public static final Color ROW_ALT      = new Color(245, 247, 252);   // very light blue
    public static final Color ROW_HOVER    = new Color(232, 240, 255);   // blue tinted hover
    public static final Color TABLE_HDR    = new Color(0, 51, 153);      // Phenikaa navy header
    public static final Color TABLE_HDR2   = new Color(0, 38, 115);      // Slightly darker gradient end

    private UIFactory() {}

    // ── Typography ─────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 12);

    private static String bestIconFont = null;
    public static String getBestIconFont() {
        if (bestIconFont != null) return bestIconFont;
        // Font list order: Preferred → Fallback → Legacy
        String[] fonts = {"Segoe UI Emoji", "Segoe UI Symbol", "Arial Unicode MS", "Symbola", "Lucida Sans Unicode"};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] available = ge.getAvailableFontFamilyNames();
        for (String f : fonts) {
            for (String a : available) {
                if (f.equalsIgnoreCase(a)) {
                    bestIconFont = f;
                    return f;
                }
            }
        }
        return "Serif"; 
    }

    public static String formatEmojiHtml(String text) {
        if (text == null || text.isEmpty() || text.startsWith("<html>")) return text;
        int i = 0;
        while (i < text.length() && text.charAt(i) == ' ') i++;
        if (i < text.length()) {
            int cp = text.codePointAt(i);
            if (cp >= 0x2000) {
                int count = Character.charCount(cp);
                String leadingSpaces = text.substring(0, i).replace(" ", "&nbsp;");
                String icon = text.substring(i, i + count);
                String rest = text.substring(i + count).replace("  ", "&nbsp;&nbsp;");
                return "<html>" + leadingSpaces + "<font face='" + getBestIconFont() + "'>" + icon + "</font><font face='Segoe UI'>" + rest + "</font></html>";
            }
        }
        return text;
    }

    // ── Section title ──────────────────────────────────────────────────────
    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(formatEmojiHtml(text));
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    // ── Animated Button ───────────────────────────────────────────────────
    public static JButton button(String text, Color bg) {
        return button(text, bg, bg.darker());
    }

    public static JButton button(String text, Color bg, Color hoverBg) {
        JButton btn = new JButton(formatEmojiHtml(text)) {
            private float hoverProgress = 0f;
            private Timer animTimer;
            private boolean hovering = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovering = true; startAnim(); }
                    public void mouseExited(MouseEvent e) { hovering = false; startAnim(); }
                });
            }

            private void startAnim() {
                if (animTimer != null && animTimer.isRunning()) animTimer.stop();
                animTimer = new Timer(16, ev -> {
                    if (hovering && hoverProgress < 1f) {
                        hoverProgress = Math.min(1f, hoverProgress + 0.15f);
                    } else if (!hovering && hoverProgress > 0f) {
                        hoverProgress = Math.max(0f, hoverProgress - 0.15f);
                    } else {
                        ((Timer) ev.getSource()).stop();
                    }
                    repaint();
                });
                animTimer.start();
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int r = (int)(bg.getRed() + (hoverBg.getRed()-bg.getRed())*hoverProgress);
                int gr = (int)(bg.getGreen() + (hoverBg.getGreen()-bg.getGreen())*hoverProgress);
                int b = (int)(bg.getBlue() + (hoverBg.getBlue()-bg.getBlue())*hoverProgress);
                // Shadow on hover
                if (hoverProgress > 0.1f) {
                    g2.setColor(new Color(0,0,0,(int)(20*hoverProgress)));
                    g2.fillRoundRect(2,2,getWidth()-2,getHeight(),10,10);
                }
                g2.setColor(new Color(r,gr,b));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                // Shine
                g2.setColor(new Color(255,255,255,(int)(25+15*hoverProgress)));
                g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        return btn;
    }

    // ── Text Field ─────────────────────────────────────────────────────────
    public static JTextField textField() {
        JTextField tf = new JTextField() {
            private float focusProg = 0f;
            private Timer aTimer;
            { addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { anim(true); }
                public void focusLost(FocusEvent e) { anim(false); }
            }); }
            private void anim(boolean gain) {
                if (aTimer!=null) aTimer.stop();
                aTimer = new Timer(16, ev -> {
                    if (gain && focusProg<1f) focusProg=Math.min(1f,focusProg+0.2f);
                    else if (!gain && focusProg>0f) focusProg=Math.max(0f,focusProg-0.2f);
                    else ((Timer)ev.getSource()).stop();
                    repaint();
                });
                aTimer.start();
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                super.paintComponent(g2); g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                int r=(int)(BORDER.getRed()+(ACCENT.getRed()-BORDER.getRed())*focusProg);
                int gr2=(int)(BORDER.getGreen()+(ACCENT.getGreen()-BORDER.getGreen())*focusProg);
                int b=(int)(BORDER.getBlue()+(ACCENT.getBlue()-BORDER.getBlue())*focusProg);
                g2.setColor(new Color(r,gr2,b));
                g2.setStroke(new BasicStroke(1f+focusProg));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
            }
        };
        tf.setFont(FONT_BODY); tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(8,12,8,12));
        tf.setBackground(Color.WHITE);
        return tf;
    }

    /** Password field with the same rounded styling. */
    public static JPasswordField passwordField() {
        JPasswordField pf = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                super.paintComponent(g2); g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus()?ACCENT:BORDER);
                g2.setStroke(new BasicStroke(hasFocus()?2f:1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose();
            }
        };
        pf.setFont(FONT_BODY); pf.setOpaque(false);
        pf.setBorder(new EmptyBorder(8,12,8,12));
        pf.setBackground(Color.WHITE);
        return pf;
    }

    /** Field label above inputs. */
    public static JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(formatEmojiHtml(text));
        lbl.setFont(FONT_LABEL); lbl.setForeground(TEXT_MID);
        return lbl;
    }

    // ── Table ──────────────────────────────────────────────────────────────
    public static JTable createTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model) {
            private int hoveredRow = -1;
            { addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    int row = rowAtPoint(e.getPoint());
                    if (row != hoveredRow) { hoveredRow = row; repaint(); }
                }
            });
            addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) { hoveredRow = -1; repaint(); }
            }); }
            public int getHoveredRow() { return hoveredRow; }
        };
        table.setFont(FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setSelectionForeground(TEXT_DARK);
        table.setBackground(CARD_BG);

        // ── Custom header — Phenikaa navy ──
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val==null?"":val.toString()) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2=(Graphics2D)g.create();
                        GradientPaint gp=new GradientPaint(0,0,TABLE_HDR,0,getHeight(),TABLE_HDR2);
                        g2.setPaint(gp);
                        g2.fillRect(0,0,getWidth(),getHeight());
                        super.paintComponent(g2); g2.dispose();
                    }
                };
                lbl.setFont(new Font("Segoe UI",Font.BOLD,12));
                lbl.setForeground(Color.WHITE);
                lbl.setBorder(new EmptyBorder(0,12,0,12));
                lbl.setOpaque(false);
                lbl.setPreferredSize(new Dimension(0,42));
                return lbl;
            }
        });
        header.setBackground(TABLE_HDR);
        header.setPreferredSize(new Dimension(0,42));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // ── Row renderer with hover ──
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                int hovRow=-1;
                try { hovRow=(int)t.getClass().getMethod("getHoveredRow").invoke(t); } catch(Exception ignored){}
                if (sel) {
                    setBackground(new Color(200,220,255));
                    setForeground(TEXT_DARK);
                } else if (row==hovRow) {
                    setBackground(ROW_HOVER);
                    setForeground(TEXT_DARK);
                } else {
                    setBackground(row%2==0?CARD_BG:ROW_ALT);
                    setForeground(TEXT_DARK);
                }
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(0,12,0,12));
                return this;
            }
        });
        return table;
    }

    public static DefaultTableModel getModel(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    // ── Card ───────────────────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i=4;i>0;i--) {
                    g2.setColor(new Color(0,0,0,5*i));
                    g2.fillRoundRect(i,i,getWidth()-i,getHeight()-i,14,14);
                }
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0,0,getWidth()-4,getHeight()-4,12,12);
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(16,16,16,16));
        return p;
    }

    public static JPanel shadowCard() {
        return new JPanel() {
            { setOpaque(false); setBackground(CARD_BG); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,20));
                g2.fillRoundRect(3,3,getWidth()-3,getHeight()-3,12,12);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0,0,getWidth()-4,getHeight()-4,12,12);
                g2.dispose();
            }
        };
    }

    // ── Custom Scrollbar ──────────────────────────────────────────────────
    private static void customizeScrollBar(JScrollBar bar) {
        bar.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(160,170,190);
                this.trackColor = CARD_BG;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty()||!scrollbar.isEnabled()) return;
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(120,140,170,150));
                g2.fillRoundRect(r.x+2,r.y+2,r.width-4,r.height-4,8,8);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
            private JButton zeroBtn() { JButton b=new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        });
        bar.setPreferredSize(new Dimension(8,8));
    }

    // ── Scroll pane ────────────────────────────────────────────────────────
    public static JScrollPane scrollTable(JTable table) {
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(CARD_BG);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        customizeScrollBar(sc.getVerticalScrollBar());
        customizeScrollBar(sc.getHorizontalScrollBar());
        return sc;
    }

    // ── Stat card ─────────────────────────────────────────────────────────
    public static JPanel statCard(String title, String value, String subtitle, Color from, Color to) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(from.getRed(),from.getGreen(),from.getBlue(),40));
                g2.fillRoundRect(3,3,getWidth()-3,getHeight()-3,16,16);
                GradientPaint gp=new GradientPaint(0,0,from,getWidth(),getHeight(),to);
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth()-4,getHeight()-4,14,14);
                g2.setColor(new Color(255,255,255,20));
                g2.fillOval(getWidth()-80,-30,120,120);
                g2.setColor(new Color(255,255,255,10));
                g2.fillOval(getWidth()-50,40,80,80);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20,22,20,22));
        JLabel lT=new JLabel(title); lT.setFont(new Font("Segoe UI",Font.PLAIN,12)); lT.setForeground(new Color(255,255,255,200));
        JLabel lV=new JLabel(value); lV.setFont(new Font("Segoe UI",Font.BOLD,24)); lV.setForeground(Color.WHITE);
        JLabel lS=new JLabel(subtitle); lS.setFont(new Font("Segoe UI",Font.PLAIN,11)); lS.setForeground(new Color(255,255,255,160));
        JPanel text=new JPanel(new GridLayout(3,1,0,4)); text.setOpaque(false);
        text.add(lT); text.add(lV); text.add(lS);
        card.add(text);
        return card;
    }

    // ── Search bar ────────────────────────────────────────────────────────
    public static JTextField searchBar(String placeholder) {
        JTextField tf = textField();
        tf.setPreferredSize(new Dimension(260,36));
        tf.setText("  🔍  "+placeholder); tf.setForeground(TEXT_GRAY);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (tf.getForeground().equals(TEXT_GRAY)) { tf.setText(""); tf.setForeground(TEXT_DARK); } }
            public void focusLost(FocusEvent e) { if (tf.getText().isEmpty()) { tf.setText("  🔍  "+placeholder); tf.setForeground(TEXT_GRAY); } }
        });
        return tf;
    }

    public static JSeparator separator() {
        JSeparator sep=new JSeparator(); sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); return sep;
    }

    public static JPanel toolbar() {
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(CARD_BG);
        p.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BORDER),new EmptyBorder(14,24,14,24)));
        return p;
    }

    public static JLabel badge(String text, Color bg) {
        JLabel lbl=new JLabel(formatEmojiHtml(text),SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(bg.getRed(),bg.getGreen(),bg.getBlue(),30));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                super.paintComponent(g2); g2.dispose();
            }
        };
        lbl.setFont(new Font("Segoe UI",Font.BOLD,11)); lbl.setForeground(bg.darker());
        lbl.setOpaque(false); lbl.setBorder(new EmptyBorder(3,10,3,10));
        return lbl;
    }

    /** Count badge with Phenikaa orange accent */
    public static JLabel countBadge(int count) {
        JLabel lbl=new JLabel(String.valueOf(count),SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                super.paintComponent(g2); g2.dispose();
            }
        };
        lbl.setFont(new Font("Segoe UI",Font.BOLD,11)); lbl.setForeground(Color.WHITE);
        lbl.setOpaque(false); lbl.setBorder(new EmptyBorder(2,8,2,8));
        return lbl;
    }

    public static String vnd(double amount) { return String.format("%,.0f đ",amount); }

    public static JPanel formRow(String labelText, JComponent field) {
        JPanel p=new JPanel(); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.setBackground(CARD_BG); p.setBorder(new EmptyBorder(0,0,14,0));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl=fieldLabel(labelText); lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0,0,5,0));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl); p.add(field); return p;
    }

    public static String fmtDate(java.time.LocalDate d) {
        if (d==null) return "—";
        return d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
