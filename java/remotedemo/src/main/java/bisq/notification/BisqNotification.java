package bisq.notification;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.turo.pushy.apns.ApnsClient;
import com.turo.pushy.apns.ApnsClientBuilder;
import com.turo.pushy.apns.PushNotificationResponse;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import com.turo.pushy.apns.util.SimpleApnsPushNotification;
import com.turo.pushy.apns.util.TokenUtil;
import com.turo.pushy.apns.util.concurrent.PushNotificationFuture;
import org.bitcoinj.core.Base58;
import org.json.simple.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class BisqNotification {
    private static final String  SYM_KEY_ALGO = "AES";
    private static final Integer SYM_KEY_BITS = 256;
    private static final String  SYM_CIPHER   = "AES";
    private String notificationToken;

    private SecretKey key;

    public BisqNotification() {
        key = generateSecretKey(SYM_KEY_BITS);

        try {
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse;
            {
                PushNotificationResponse<SimpleApnsPushNotification> temp;
                try {
                    temp = sendNotificationFuture.get();
                } catch (InterruptedException e) {
                    temp = null;
                    e.printStackTrace();
                }
                pushNotificationResponse = temp;
            }

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
        }

    }

    final ApnsClient apnsClient; {
        ApnsClient temp;
        try {
            temp = new ApnsClientBuilder()
                        .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                        .setClientCredentials(new File("/Users/joachim/SpiderOak Hive/keys/push_certificate.production.p12"), "")
                        .build();
        } catch (IOException e) {
            temp = null;
            e.printStackTrace();
        }
        apnsClient = temp;
    }


    final SimpleApnsPushNotification pushNotification; {
        final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
        payloadBuilder.setAlertBody("Bisq notifcation");

        BisqNotifcationObject bisqNotifcationObject = new BisqNotifcationObject();

        Gson gson = new Gson();
        String json = gson.toJson(bisqNotifcationObject);
        payloadBuilder.addCustomProperty("bisqNotification", json);

        final String payload = payloadBuilder.buildWithDefaultMaximumLength();
        final String token = TokenUtil.sanitizeTokenString("c0e7a47701ba2ebbb25c104796e65a0ac04ca77fc6c09d66ab9dede8ddebf3ca");

        pushNotification = new SimpleApnsPushNotification(token, "com.joachimneumann.bisqremotetest", payload);
    }

    final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
            sendNotificationFuture = apnsClient.sendNotification(pushNotification);


    public String key() {
        try {
            return Base58.encode(key.getEncoded()) +" (Base58)";
        } catch (Exception e) {
            return "bisq_key error";
        }
    }

    private SecretKey generateSecretKey(int bits) {
        try {
            KeyGenerator keyPairGenerator = KeyGenerator.getInstance(SYM_KEY_ALGO);
            keyPairGenerator.init(bits);
            return keyPairGenerator.generateKey();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private String getEncryptedDataAsHex(String json) {
        byte[] payload = json.getBytes(Charset.forName("UTF-8"));
        SecretKey secretKey = generateSecretKey(256);
        byte[] encryptedPayload = encrypt(payload, secretKey);
        //byte[] decryptedPayload = decrypt(encryptedPayload, secretKey);

        String hex = BaseEncoding.base16().lowerCase().encode(encryptedPayload);
        System.out.println("hex = " + hex);
        return hex;
    }

    private String getJson(String type, String headline, String msg) {
        JSONObject obj = new JSONObject();
        obj.put("type", type);
        obj.put("headline", headline);
        obj.put("msg", msg);
        String jsonString = obj.toJSONString();
        System.out.println("jsonString = " + jsonString);
        return jsonString;
    }

    private static String executePost(String targetURL, String urlParameters) {
        System.out.println("executePost targetURL = " + targetURL);
        System.out.println("executePost urlParameters = " + urlParameters);
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String result = response.toString();
            System.out.print("executePost result = " + result);
            return result;
        } catch (IOException e) {
            System.out.print("executePost error = " + e);

            return e.toString();
        } catch (Throwable e) {
            System.out.print("executePost error = " + e);
            e.printStackTrace();
            return e.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private byte[] encrypt(byte[] payload, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(SYM_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(payload);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decrypt(byte[] encryptedPayload, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(SYM_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedPayload);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
