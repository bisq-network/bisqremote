package bisq.notification;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class WebcamQRCodeExample extends JFrame implements Runnable, ThreadFactory {

    private static final long serialVersionUID = 6441489157408381878L;

    private Executor executor = Executors.newSingleThreadExecutor(this);

    private WebcamPanel panel = null;

    public WebcamQRCodeExample() {
        super();

        setLayout(new FlowLayout());
        setTitle("show the Bisq QR code on your phone");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                NotificationApp.webcam.close();
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });

        NotificationApp.webcam = Webcam.getWebcams().get(0);
        Dimension[] sizes = NotificationApp.webcam.getViewSizes();
        NotificationApp.webcam.setViewSize(sizes[sizes.length - 1]);

        panel = new WebcamPanel(NotificationApp.webcam);
        panel.setPreferredSize(sizes[sizes.length - 1]);

        add(panel);

        pack();
        setVisible(true);

        executor.execute(this);
    }

    @Override
    public void run() {
        // check 10 times a second for the QR code
        boolean run = true;
        while (run) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Result result = null;
            BufferedImage image = null;

            if (NotificationApp.webcam.isOpen()) {

                if ((image = NotificationApp.webcam.getImage()) == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    // fall thru, it means there is no QR code in image
                }
            }

            if (result != null) {
                System.out.println(result.getText());
                dispatchEvent(new java.awt.event.WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                run = false;
            }
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "example-runner");
        t.setDaemon(true);
        return t;
    }
}