package bisq.notification;

public class BisqNotifcationObject {
    String timestampEvent = "2018-06-19 11:58:41";
    String transactionID = "234523423";
    String title = "example Title";
    String message = "example message";
    String notificationType = "TRADE_ACCEPTED";
    String actionRequired = "FROM JAVA! You need to make the bank transfer to receive your BTC";
    int version = 1;

    public BisqNotifcationObject() {
    }

    public BisqNotifcationObject(BisqNotifcationObject template){
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
