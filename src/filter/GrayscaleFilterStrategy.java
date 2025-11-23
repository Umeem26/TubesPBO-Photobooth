
package filter;

import java.awt.image.BufferedImage;
import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;

public class GrayscaleFilterStrategy implements FilterStrategy {

    @Override
    public String getFilterName() { return "Grayscale"; }

    @Override
    public BufferedImage applyFilter(BufferedImage original) {
        BufferedImage grayImage = new BufferedImage(
            original.getWidth(),
            original.getHeight(),
            BufferedImage.TYPE_BYTE_GRAY
        );
        
        ColorConvertOp op = new ColorConvertOp(
            original.getColorModel().getColorSpace(),
            ColorSpace.getInstance(ColorSpace.CS_GRAY),
            null
        );
        
        op.filter(original, grayImage);
        return grayImage;
    }
}
