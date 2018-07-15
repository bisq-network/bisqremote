package bisq.notification;

public class BisqNotificationObject {
    long sentDate;
    String txId= "";
    String title = "";
    String message = "";
    String type = "";
    String actionRequired = "";
    int version = 1;

    public BisqNotificationObject() {
    }

    public BisqNotificationObject(BisqNotificationObject template) {
        // this constructor is used to strip variables from the subclass BisqNotifcation
        sentDate = template.sentDate;
        txId = template.txId;
        title = template.title;
        message = template.message;
        type = template.type;
        actionRequired = template.actionRequired;
        version = template.version;
        sentDate = 0;
    }
}
