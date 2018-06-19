package bisq.notification;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import java.nio.charset.StandardCharsets;

public class Token {

    private static Token instance;

    public String apsToken; // the token is encoded in Base58

    private Token(){
        apsToken = null;
    }

    public String asHex() {
        byte[] binary;
        try {
            binary = Base58.decode(apsToken);
        } catch (AddressFormatException e) {
            return "error decoding apsToken";
        }

        String hex = BaseEncoding.base16().lowerCase().encode(binary);
        System.out.println("hex = " + hex);
        return hex;
    }

    public static synchronized Token getInstance(){
        if(instance == null){
            instance = new Token();
        }
        return instance;
    }

}