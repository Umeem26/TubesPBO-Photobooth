package export;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class DriveExportStrategy implements ExportStrategy {

    private static final String APPLICATION_NAME = "Photobooth App";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json"; 

    @Override
    public String getStrategyName() { return "Google Drive"; }

    @Override
    public boolean export(BufferedImage image, File videoFile) {
        System.out.println("LOG: Memulai proses upload ke Drive (Folder)...");
        
        File tempImage = new File("temp_strip.png");
        try {
            ImageIO.write(image, "PNG", tempImage);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Drive service = getDriveService();

            // 1. BUAT FOLDER DULU
            com.google.api.services.drive.model.File folderMetadata = new com.google.api.services.drive.model.File();
            folderMetadata.setName("Photobooth Session " + System.currentTimeMillis());
            folderMetadata.setMimeType("application/vnd.google-apps.folder");

            com.google.api.services.drive.model.File folder = service.files().create(folderMetadata)
                    .setFields("id, webViewLink")
                    .execute();
            String folderId = folder.getId();
            System.out.println("Folder ID: " + folderId);

            // 2. UPLOAD FOTO KE DALAM FOLDER
            com.google.api.services.drive.model.File photoMetadata = new com.google.api.services.drive.model.File();
            photoMetadata.setName("Photo_Strip.png");
            photoMetadata.setParents(Collections.singletonList(folderId)); // Masukkan ke folder
            
            FileContent photoContent = new FileContent("image/png", tempImage);
            service.files().create(photoMetadata, photoContent).execute();
            System.out.println("Foto terupload.");

            // 3. UPLOAD VIDEO (Jika Ada) KE DALAM FOLDER
            if (videoFile != null && videoFile.exists()) {
                com.google.api.services.drive.model.File videoMetadata = new com.google.api.services.drive.model.File();
                videoMetadata.setName("Video_Strip.mp4");
                videoMetadata.setParents(Collections.singletonList(folderId));

                FileContent videoContent = new FileContent("video/mp4", videoFile);
                service.files().create(videoMetadata, videoContent).execute();
                System.out.println("Video terupload.");
            }

            // 4. SET PERMISSION FOLDER JADI PUBLIC
            Permission userPermission = new Permission().setType("anyone").setRole("reader");
            service.permissions().create(folderId, userPermission).execute();

            // 5. AMBIL LINK FOLDER UNTUK QR CODE
            String shareLink = folder.getWebViewLink();
            
            // 6. Tampilkan QR
            BufferedImage qrImage = generateQRCode(shareLink);
            showQRCodeDialog(qrImage);

            tempImage.delete();
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal Upload: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Helpers (Sama seperti sebelumnya) ---
    private Drive getDriveService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = DriveExportStrategy.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) throw new IOException("Resource not found: " + CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, new AuthorizationCodeInstalledApp(flow, receiver).authorize("user"))
                .setApplicationName(APPLICATION_NAME).build();
    }

    private BufferedImage generateQRCode(String text) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private void showQRCodeDialog(BufferedImage qrImage) {
        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        JLabel textLabel = new JLabel("Scan untuk Download Foto & Video!", SwingConstants.CENTER);
        textLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        JOptionPane.showMessageDialog(null, new Object[]{textLabel, qrLabel}, "Upload Berhasil!", JOptionPane.PLAIN_MESSAGE);
    }
}