package bisq.notification;

import com.google.gson.Gson;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BisqNotification extends BisqNotificationObject {
    private BisqToken bisqToken;
    private BisqKey bisqKey;

    public BisqNotification(BisqToken t, BisqKey k) {
        super();
        bisqToken = t;
        bisqKey = k;
    }


    public void send(Boolean encrypt) {
        try {
            ApnsClient apnsClient;
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("push_certificate.production.p12");
            if (resource == null) {
                System.out.println("Error: push_certificate.production.p12 does not exist ");
                return;
            }

            File p12File = new File(resource.getFile());
            apnsClient = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setClientCredentials(p12File, "")
                    .build();

            //.setClientCredentials(new File("/Users/joachim/SpiderOak Hive/keys/push_certificate.production.p12"), "")

            PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = null;
            SimpleApnsPushNotification pushNotification;

            ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
            payloadBuilder.setAlertBody("Bisq notifcation");
            Gson gson = new Gson();
            BisqNotificationObject mini = new BisqNotificationObject(this);
            String json = gson.toJson(mini);
            byte[] ptext = json.getBytes(ISO_8859_1);
            json = new String(ptext, UTF_8);

            if (encrypt) {
                payloadBuilder.addCustomProperty("encrypted", bisqKey.encryptBisqMessage(json));
            } else {
                payloadBuilder.addCustomProperty("bisqNotification", json);
            }

            final String payload = payloadBuilder.buildWithDefaultMaximumLength();
            final String token = bisqToken.asHex();

            pushNotification = new SimpleApnsPushNotification(token, bisqToken.bundleidentifier, payload);

            PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                    sendNotificationFuture = apnsClient.sendNotification(pushNotification);

            pushNotificationResponse = sendNotificationFuture.get();
            if (pushNotificationResponse.isAccepted()) {
                System.out.println("Push notification accepted by APNs gateway.");
            } else {
                System.out.println("Notification rejected by the APNs gateway: " +
                        pushNotificationResponse.getRejectionReason());

                if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                    System.out.println("\t…and the token is invalid as of " +
                            pushNotificationResponse.getTokenInvalidationTimestamp());
                }
            }
        } catch (final ExecutionException e) {
            System.err.println("Failed to send push notification.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
