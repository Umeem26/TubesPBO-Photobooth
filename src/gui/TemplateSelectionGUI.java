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
 * VERSI PREMIUM: Layout Card Grid, Header Branding, Dark Theme.
 */
public class TemplateSelectionGUI extends JFrame {

    private PhotoboothService service;

    // Warna Tema (Konsisten dengan PhotoboothGUI)
    private final Color MAIN_BG = new Color(30, 30, 30);
    private final Color CARD_BG = new Color(50, 50, 50);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public TemplateSelectionGUI(PhotoboothService service) {
        this.service = service;

        setTitle("Pilih Desain - Photobooth Studio");
        setSize(1000, 700); // Ukuran disamakan dengan GUI utama
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set background utama
        getContentPane().setBackground(MAIN_BG);

        // --- 1. HEADER PANEL ---
        add(createHeaderPanel(), BorderLayout.NORTH);

        // --- 2. MAIN CONTENT (Grid Tombol) ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MAIN_BG);
        
        // Judul Instruksi
        JLabel lblInstruction = new JLabel("Silakan Pilih Layout Strip Foto Anda", SwingConstants.CENTER);
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblInstruction.setForeground(Color.LIGHT_GRAY);
        lblInstruction.setBorder(new EmptyBorder(20, 0, 20, 0));
        mainPanel.add(lblInstruction, BorderLayout.NORTH);

        // Grid Tombol (2 Baris, 3 Kolom)
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setBackground(MAIN_BG);
        gridPanel.setBorder(new EmptyBorder(0, 40, 40, 40)); // Padding pinggir lega

        // Loop membuat tombol
        for (StripTemplate template : service.getAvailableTemplates().values()) {
            JButton btn = createTemplateButton(template);
            gridPanel.add(btn);
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Helper: Membuat Header yang sama dengan PhotoboothGUI
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(45, 45, 48));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("SIXSEVEN PHOTOBOOTH STUDIO");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setIcon(new ImageIcon("camera.png")); // Pastikan icon ada
        title.setIconTextGap(15);
        
        header.add(title);
        return header;
    }

    // Helper: Membuat Tombol Template yang Cantik
    private JButton createTemplateButton(StripTemplate template) {
        // 1. Ambil Gambar Pratinjau
        BufferedImage preview = template.getPreviewImage();
        
        // Skala gambar agar pas di tombol (sedikit diperkecil)
        Image scaledPreview = preview.getScaledInstance(120, -1, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledPreview);

        // 2. Buat Tombol
        JButton btn = new JButton(template.getTemplateName());
        btn.setIcon(icon);
        
        // 3. Styling Tombol
        btn.setFont(BUTTON_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(CARD_BG);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1), // Border luar halus
            new EmptyBorder(15, 15, 15, 15) // Padding dalam
        ));
        
        // Pengaturan Posisi Teks & Ikon
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(15); // Jarak antara gambar dan teks
        
        // Ubah kursor saat hover
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 4. Aksi Tombol
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchPhotobooth(template.getTemplateId());
                dispose(); 
            }
        });

        return btn;
    }
    
    private void launchPhotobooth(String selectedTemplateId) {
        System.out.println("LOG: Template dipilih: " + selectedTemplateId);
        PhotoboothGUI cameraGUI = new PhotoboothGUI(service, selectedTemplateId);
        cameraGUI.setVisible(true);
    }
}