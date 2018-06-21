package bisq.notification;

import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class BisqNotificationServer {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private ApnsClient apnsClientProduction;
    private ApnsClient apnsClientDevelopment;
    private ApnsPayloadBuilder payloadBuilder;
    public static final String IOS_BUNDLE_IDENTIFIER = "com.joachimneumann.bisqremotetest";

    BisqNotificationServer(Boolean production) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("push_certificate.production.p12");
            if (resource == null) {
                throw new IOException("push_certificate.production.p12 does not exist");
            }

            File p12File = new File(resource.getFile());
            logger.info("Using certification file {}.", p12File.getAbsolutePath());
            apnsClientProduction = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                    .setClientCredentials(p12File, "")
                    .build();
            apnsClientDevelopment = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setClientCredentials(p12File, "")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void overTor_____sendMessage(String apsTokenHex, String encryptedMessage, Boolean production) {
        SimpleApnsPushNotification pushNotification;
        payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setAlertBody("Bisq notifcation");
        payloadBuilder.addCustomProperty("encrypted", encryptedMessage);
        final String payload = payloadBuilder.buildWithDefaultMaximumLength();
        SimpleApnsPushNotification simpleApnsPushNotification = new SimpleApnsPushNotification(apsTokenHex, IOS_BUNDLE_IDENTIFIER, payload);

        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                sendNotificationFuture;
        if (production) {
            sendNotificationFuture = apnsClientProduction.sendNotification(simpleApnsPushNotification);
        } else {
            sendNotificationFuture = apnsClientDevelopment.sendNotification(simpleApnsPushNotification);
        }

        try {
            PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = sendNotificationFuture.get();
            if (pushNotificationResponse.isAccepted()) {
                logger.info("Push notification accepted by APNs gateway.");
            } else {
                logger.info("Notification rejected by the APNs gateway: " +
                        pushNotificationResponse.getRejectionReason());
                if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                    logger.info("\t…and the token is invalid as of " +
                            pushNotificationResponse.getTokenInvalidationTimestamp());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
