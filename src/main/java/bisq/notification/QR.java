package bisq.notification;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class QR implements Runnable {

    private ImageView capturedImage;
    private Webcam webcam;
    private String qrString;
    private NotificationApp app;
    private Stage stage;

    Thread runner;

    public QR(NotificationApp app_, final Stage primaryStage, Webcam webcam_) {
        app = app_;
        webcam = webcam_;
        qrString = null;
        capturedImage = new ImageView();
        Dimension[] sizes = webcam.getViewSizes();
        Dimension size = sizes[sizes.length - 1]; // the largest size
        webcam.setViewSize(size);

        BorderPane webCamPane = new BorderPane();
        webCamPane.setStyle("-fx-background-color: #ccc;");
        Scene scene = new Scene(webCamPane, size.width, size.height);
        webCamPane.getChildren().add(capturedImage);
        stage = new Stage();
        stage.setTitle("Scan QR Code");
        stage.setX(primaryStage.getX() + 200);
        stage.setY(0);
        stage.setScene(scene);
        stage.show();
        this.runner = new Thread(this);
        this.runner.start();
    }

    public void close() {
        stage.close();
    }

    @Override
    public void run() {
        boolean run = true;
        int detectQRFramerate = 5;
        int detectQRcounter = 0;
        if (!webcam.isOpen()) {
            webcam.open();
        }
        while (run) {
            BufferedImage bufferedImage = null;
            Result result = null;

            if ((bufferedImage = webcam.getImage()) == null) {
                continue;
            }

            WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
            capturedImage.setImage(writableImage);

            // QR code detection
            detectQRcounter += 1;
            if (detectQRcounter == detectQRFramerate) {
                detectQRcounter = 0;
                LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    // fall thru, it means there is no QR code in image
                }
                if (result != null) {
                    run = false;
                    qrString = result.getText();
                    webcam.close();
                    Platform.runLater(
                            () -> {
                                app.qrString(qrString);
                            }
                    );
                }
            }
        }
    }
}
