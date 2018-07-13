package bisq.notification;

public class BisqNotificationObject {
    String timestampEvent = "2018-06-19 11:58:41";
    String transactionID = "234523423";
    String title = "";
    String message = "";
    String notificationType = "";
    String actionRequired = "";
    int version = 1;

    public BisqNotificationObject() {
    }

    public BisqNotificationObject(BisqNotificationObject template) {
        // this constructor is used to strip variables from the subclass BisqNotifcation
        timestampEvent=template.timestampEvent;
        transactionID=template.transactionID;
        title=template.title;
        message=template.message;
        notificationType=template.notificationType;
        actionRequired=template.actionRequired;
        version=template.version;
    }
}
