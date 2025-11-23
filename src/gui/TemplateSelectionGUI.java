package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import service.PhotoboothService;
import model.StripTemplate;

/**
 * Halaman Awal (JFrame) untuk memilih template.
 * VERSI BARU: Multi-step selection (Layout -> Jumlah Foto).
 */
public class TemplateSelectionGUI extends JFrame {

    private PhotoboothService service;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // State Selection
    private String selectedLayoutType; // "VERTICAL" or "HORIZONTAL"

    // Warna Tema
    private final Color MAIN_BG = new Color(30, 30, 30);
    private final Color CARD_BG = new Color(50, 50, 50);
    private final Color ACCENT_COLOR = new Color(0, 120, 215);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public TemplateSelectionGUI(PhotoboothService service) {
        this.service = service;

        setTitle("Pilih Desain - Photobooth Studio");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Global
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Content dengan CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(MAIN_BG);

        // Tambahkan "Kartu" (Halaman)
        cardPanel.add(createLayoutSelectionPanel(), "STEP_1_LAYOUT");
        cardPanel.add(createPhotoCountSelectionPanel(), "STEP_2_COUNT");

        add(cardPanel, BorderLayout.CENTER);

        // Mulai dari Step 1
        cardLayout.show(cardPanel, "STEP_1_LAYOUT");
    }

    // --- STEP 1: PILIH LAYOUT (VERTICAL / HORIZONTAL) ---
    private JPanel createLayoutSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);

        JLabel lblInstruction = new JLabel("Langkah 1: Pilih Tipe Layout", SwingConstants.CENTER);
        lblInstruction.setFont(HEADER_FONT);
        lblInstruction.setForeground(Color.LIGHT_GRAY);
        lblInstruction.setBorder(new EmptyBorder(30, 0, 30, 0));
        panel.add(lblInstruction, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(MAIN_BG);

        JButton btnVertical = createBigOptionButton("Strip Vertikal", "TPL-V");
        JButton btnHorizontal = createBigOptionButton("Strip Horizontal", "TPL-H");

        btnVertical.addActionListener(e -> {
            selectedLayoutType = "VERTICAL";
            cardLayout.show(cardPanel, "STEP_2_COUNT");
        });

        btnHorizontal.addActionListener(e -> {
            selectedLayoutType = "HORIZONTAL";
            cardLayout.show(cardPanel, "STEP_2_COUNT");
        });

        // Tambahkan ke Grid
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        centerPanel.add(btnVertical, gbc);
        centerPanel.add(btnHorizontal, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    // --- STEP 2: PILIH JUMLAH FOTO (2, 3, atau 4) ---
    private JPanel createPhotoCountSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_BG);

        JLabel lblInstruction = new JLabel("Langkah 2: Pilih Jumlah Foto", SwingConstants.CENTER);
        lblInstruction.setFont(HEADER_FONT);
        lblInstruction.setForeground(Color.LIGHT_GRAY);
        lblInstruction.setBorder(new EmptyBorder(30, 0, 30, 0));
        panel.add(lblInstruction, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(MAIN_BG);

        JButton btn2Photos = createBigOptionButton("2 Foto", null);
        JButton btn3Photos = createBigOptionButton("3 Foto", null);
        JButton btn4Photos = createBigOptionButton("4 Foto", null);

        // Tombol Kembali
        JButton btnBack = new JButton("KEMBALI");
        styleButton(btnBack, Color.GRAY);
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, "STEP_1_LAYOUT"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(MAIN_BG);
        bottomPanel.add(btnBack);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        btn2Photos.addActionListener(e -> launchPhotobooth(2));
        btn3Photos.addActionListener(e -> launchPhotobooth(3));
        btn4Photos.addActionListener(e -> launchPhotobooth(4));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        centerPanel.add(btn2Photos, gbc);
        centerPanel.add(btn3Photos, gbc);
        centerPanel.add(btn4Photos, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private void launchPhotobooth(int photoCount) {
        String finalTemplateId = "";
        if ("VERTICAL".equals(selectedLayoutType)) {
            finalTemplateId = "TPL-V-" + photoCount;
        } else {
            finalTemplateId = "TPL-H-" + photoCount;
        }

        System.out.println("LOG: Meluncurkan Photobooth dengan Template: " + finalTemplateId);

        // WORKAROUND: Kita inject template baru ke service sebelum launch
        StripTemplate newTemplate = new factory.TemplateFactory().createTemplate(finalTemplateId);
        if (newTemplate != null) {
            service.getAvailableTemplates().put(finalTemplateId, newTemplate);
        }

        PhotoboothGUI cameraGUI = new PhotoboothGUI(service, finalTemplateId);
        cameraGUI.setVisible(true);
        dispose();
    }

    // --- HELPERS ---

    private JButton createBigOptionButton(String text, String previewTemplateId) {
        JButton btn = new JButton(text);
        btn.setFont(TITLE_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(CARD_BG);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(300, 200));

        // Jika ada ID template, coba ambil previewnya
        if (previewTemplateId != null) {
            StripTemplate tpl = service.getAvailableTemplates().get(previewTemplateId);
            if (tpl != null) {
                BufferedImage img = tpl.getPreviewImage();
                Image scaled = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Thumbnail kecil
                btn.setIcon(new ImageIcon(scaled));
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
            }
        }

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
                new EmptyBorder(20, 20, 20, 20)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFont(BUTTON_FONT);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(45, 45, 48));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("SIXSEVEN PHOTOBOOTH STUDIO");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setIcon(new ImageIcon("camera.png"));
        title.setIconTextGap(15);

        header.add(title);
        return header;
    }
}