package bisq.notification;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class QR {
    private QRCodeWriter qrCodeWriter;

    public QR() {
        qrCodeWriter = new QRCodeWriter();
    }

    public ImageView imageView(String text, int width, int height, Color fg, Color bg) {
        BufferedImage bufferedImage = null;
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();

            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(bg);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(fg);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            System.out.println("Success...");
        } catch (WriterException ex) {
        }

        ImageView pic = new ImageView();
        pic.setFitWidth(130);
        pic.setFitHeight(130);
        pic.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        return pic;
    }

}
