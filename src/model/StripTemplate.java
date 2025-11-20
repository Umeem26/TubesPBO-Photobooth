package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public interface StripTemplate {
    
    BufferedImage applyTemplate(ArrayList<BufferedImage> images);
    
    String getTemplateName();
    
    String getTemplateId();

    /**
     * Menghasilkan gambar pratinjau kecil untuk layout template.
     * @return BufferedImage pratinjau
     */
    BufferedImage getPreviewImage();
}