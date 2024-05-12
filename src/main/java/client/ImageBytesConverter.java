package client;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageBytesConverter {

    public static byte[] ImageToBytes(WritableImage writableImage) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static WritableImage BytesToImage(byte[] snapshotBytes) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(snapshotBytes));
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
