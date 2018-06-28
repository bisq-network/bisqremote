package bisq.notification;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BisqNotification extends BisqNotificationObject {
    public static final String BISQ_MESSAGE_IOS_MAGIC     = "BisqMessageiOS";
    public static final String BISQ_MESSAGE_ANDROID_MAGIC = "BisqMessageAndroid";
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private Phone phone;
    private BisqNotificationServer bisqNotificationServer;
    private CryptoHelper cryptoHelper;

    public BisqNotification(Phone phone_) {
        super();
        phone = phone_;
        bisqNotificationServer = new BisqNotificationServer();
        cryptoHelper = new CryptoHelper(phone.key);
    }

    public void prepareToSend(Boolean production) {
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
            cipher = cryptoHelper.encrypt(json, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String combined;
        if (phone.os == Phone.OS.iOS) {
            combined =  BISQ_MESSAGE_IOS_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            bisqNotificationServer.overTor_____sendiOSMessage(phone.notificationToken, combined, production);
        } else if (phone.os == Phone.OS.Android) {
            combined =  BISQ_MESSAGE_ANDROID_MAGIC+Phone.PHONE_SEPARATOR_WRITING+iv+Phone.PHONE_SEPARATOR_WRITING+cipher;
            bisqNotificationServer.overTor_____sendAndroidMessage(phone.notificationToken, combined);
        } else {
            combined = null;
        }

        if (combined != null) {
            System.out.println("combined = "+combined);
            try {
                String decipher = cryptoHelper.decrypt(cipher, iv);
                System.out.println("decipher = |"+decipher+"|");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
