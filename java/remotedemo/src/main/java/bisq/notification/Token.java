package bisq.notification;

public class Token {

    private static Token instance;

    public String string;

    private Token(){
        string = "BisqNotificationToken unknown";
    }

    public static synchronized Token getInstance(){
        if(instance == null){
            instance = new Token();
        }
        return instance;
    }

}