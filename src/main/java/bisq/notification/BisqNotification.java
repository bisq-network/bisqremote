package bisq.notification;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

enum NotificationTypes {
    SETUP_CONFIRMATION, ERASE, TRADE, DISPUTE, FINANCIAL
}

public class BisqNotification extends BisqNotificationObject {
    public static final String BISQ_MESSAGE_IOS_MAGIC      = "BisqMessageiOS";
    public static final String BISQ_CONFIRMATION_MESSAGE   = "confirmationNotification";
    public static final String BISQ_FACTORY_RESET_MESSAGE  = "factoryResetNotification";
    public static final String BISQ_MESSAGE_ANDROID_MAGIC  = "BisqMessageAndroid";
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private Phone phone;

    public BisqNotification(Phone phone_) {
        super();
        phone = phone_;
    }

    public String payload() {
        // reduce the notification to the fields in the superclass BisqNotificationObject
        // A JSON from BisqNotification would be far to large
        BisqNotificationObject mini = new BisqNotificationObject(this);

        Gson gson = new Gson();
        String json = gson.toJson(mini);
        byte[] ptext = json.getBytes(ISO_8859_1);
        json = new String(ptext, UTF_8);

        StringBuffer padded = new StringBuffer(json);
        while (padded.length() % 16 != 0) { padded.append(" "); }
        json = padded.toString();

        // generate 16 random characters for iv
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        String iv = uuid.substring(0, 16);
//        // For testing: Code to provoke a decryption error
//        String uuid2 = UUID.randomUUID().toString();
//        uuid2 = uuid2.replace("-", "");
//        String iv2 = uuid2.substring(0, 16);
//        iv = iv2


        String cipher = null;
        String payload = null;
        try {
            cipher = phone.encrypt(json, iv);
            logger.info("key = "+phone.key);
            logger.info("iv = "+iv);
            logger.info("encryptedJson = "+cipher);
            if (phone.os == Phone.OS.iOS) {
                payload =  BISQ_MESSAGE_IOS_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            } else if (phone.os == Phone.OS.iOSDev) {
                payload =  BISQ_MESSAGE_IOS_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            } else if (phone.os == Phone.OS.Android) {
                payload =  BISQ_MESSAGE_ANDROID_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payload;
    }

}
