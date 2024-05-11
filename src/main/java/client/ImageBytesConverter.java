package client;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;

public class ImageBytesConverter {

    public static byte[] ImageToBytes(WritableImage writableImage) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
