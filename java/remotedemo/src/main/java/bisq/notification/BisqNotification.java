package bisq.notification;

import com.google.gson.Gson;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import javax.crypto.SecretKey;
import java.io.*;
import java.util.concurrent.ExecutionException;

public class BisqNotification extends BisqNotifcationObject {

    private static final String  SYM_CIPHER   = "AES";

    private SecretKey secretKey;
    private BisqToken bisqToken;
    public BisqNotification(BisqToken t) {
        super();
        bisqToken = t;
    }


    public void send() {
        try {
            ApnsClient apnsClient;
            apnsClient = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setClientCredentials(new File("/Users/joachim/SpiderOak Hive/keys/push_certificate.production.p12"), "")
                    .build();

            PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse = null;
            SimpleApnsPushNotification pushNotification;

            ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
            payloadBuilder.setAlertBody("Bisq notifcation");

            Gson gson = new Gson();
            String json = gson.toJson((BisqNotifcationObject) this);
            payloadBuilder.addCustomProperty("bisqNotification", json);
//            payloadBuilder.addCustomProperty("encrypted", "sldkfsldk");

            final String payload = payloadBuilder.buildWithDefaultMaximumLength();
            final String token = bisqToken.asHex();

            pushNotification = new SimpleApnsPushNotification(token, "com.joachimneumann.bisqremotetest", payload);

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

//
//    private String getEncryptedDataAsHex(String json) {
//        byte[] payload = json.getBytes(Charset.forName("UTF-8"));
//        SecretKey secretKey = generateSecretKey(SYM_KEY_BITS);
//        byte[] encryptedPayload = encrypt(payload, secretKey);
//        //byte[] decryptedPayload = decrypt(encryptedPayload, secretKey);
//
//        String hex = BaseEncoding.base16().lowerCase().encode(encryptedPayload);
//        System.out.println("hex = " + hex);
//        return hex;
//    }
//
//    private byte[] encrypt(byte[] payload, SecretKey secretKey) {
//        try {
//            Cipher cipher = Cipher.getInstance(SYM_CIPHER);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            return cipher.doFinal(payload);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private byte[] decrypt(byte[] encryptedPayload, SecretKey secretKey) {
//        try {
//            Cipher cipher = Cipher.getInstance(SYM_CIPHER);
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//            return cipher.doFinal(encryptedPayload);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }
}
