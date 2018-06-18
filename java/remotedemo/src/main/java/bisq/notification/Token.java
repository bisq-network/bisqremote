package bisq.notification;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import java.nio.charset.StandardCharsets;

public class Token {

    private static Token instance;

    public String apsToken; // the token is encoded in Base58

    private Token(){
        apsToken = null;
    }

    public static synchronized Token getInstance(){
        if(instance == null){
            instance = new Token();
        }
        return instance;
    }

}