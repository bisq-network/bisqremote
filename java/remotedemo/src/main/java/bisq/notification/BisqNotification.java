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
    }


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
