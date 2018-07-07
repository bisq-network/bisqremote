package bisq.notification;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BisqNotification extends BisqNotificationObject {
    public static final String BISQ_MESSAGE_IOS_MAGIC      = "BisqMessageiOS";
    public static final String BISQ_CONFIRMATION_MESSAGE   = "confirmationNotification";
    public static final String BISQ_MESSAGE_ANDROID_MAGIC  = "BisqMessageAndroid";
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private Phone phone;
    private BisqNotificationServer bisqNotificationServer;

    public BisqNotification(Phone phone_) {
        super();
        phone = phone_;
        bisqNotificationServer = new BisqNotificationServer();
    }

    public void prepareToSend() {
        // reduce the notification to the fields in the superclass BisqNotificationObject
        // A JSON from BisqNotification would be far to large
        BisqNotificationObject mini = new BisqNotificationObject(this);

        Gson gson = new Gson();
        String json = gson.toJson(mini);
//        json = json.replace("\"", "'");
        byte[] ptext = json.getBytes(ISO_8859_1);
        json = new String(ptext, UTF_8);

        StringBuffer padded = new StringBuffer(json);
        while (padded.length() % 16 != 0) { padded.append(" "); }
        json = padded.toString();

        // generate 16 random characters for iv
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        String iv = uuid.substring(0, 16);

        String cipher = null;
        try {
            cipher = phone.encrypt(json, iv);
            logger.info("key = "+phone.key);
            logger.info("iv = "+iv);
            logger.info("encryptedJson = "+cipher);
        } catch (Exception e) {
            e.printStackTrace();
        }
        send(iv, cipher);
    }

    public void sendConfirmation() {
        send("not_encrypted", BISQ_CONFIRMATION_MESSAGE);
    }

    private void send(String iv, String cipher) {
        String combined;
        if (phone.os == Phone.OS.iOS) {
            combined =  BISQ_MESSAGE_IOS_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            bisqNotificationServer.overTor_____sendiOSMessage(phone.notificationToken, combined, true);
        } else if (phone.os == Phone.OS.iOSDev) {
            combined =  BISQ_MESSAGE_IOS_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            bisqNotificationServer.overTor_____sendiOSMessage(phone.notificationToken, combined, false);
        } else if (phone.os == Phone.OS.Android) {
            combined =  BISQ_MESSAGE_ANDROID_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            bisqNotificationServer.overTor_____sendAndroidMessage(phone.notificationToken, combined);
        }
    }
}
