package bisq.notification;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BisqNotification extends BisqNotificationObject {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private BisqToken bisqToken;
    private BisqKey bisqKey;
    private BisqNotificationServer bisqNotificationServer;

    public BisqNotification(BisqToken t, BisqKey k) {
        super();
        bisqNotificationServer = new BisqNotificationServer(false);
        bisqToken = t;
        bisqKey = k;
    }


    public void prepareToSend(Boolean production) {
        // reduce the notification to the fields in the superclass BisqNotificationObject
        // A JSON from BisqNotification would be far to large
        BisqNotificationObject mini = new BisqNotificationObject(this);

        Gson gson = new Gson();
        String json = gson.toJson(mini);

        byte[] ptext = json.getBytes(ISO_8859_1);
        json = new String(ptext, UTF_8);

        String encryptedNotification = bisqKey.encryptBisqMessage(json);

        bisqNotificationServer.overTor_____sendMessage(bisqToken.asHex(), encryptedNotification, production);
    }
}
