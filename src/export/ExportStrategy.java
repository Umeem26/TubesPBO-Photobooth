package export;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ExportStrategy {
    
    // Tambahkan parameter File videoFile
    boolean export(BufferedImage image, File videoFile);
    
    String getStrategyName();
}