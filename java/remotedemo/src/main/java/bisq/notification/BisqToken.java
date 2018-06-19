package bisq.notification;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BisqToken {

    private static final String APS_TOKEN_MAGIC = "BisqToken";
    private static final String APS_TOKEN_FILENAME = "apsToken.txt";

    private static BisqToken instance;

    private String apsTokenBase58;
    public String bundleidentifier;

    private BisqToken() {
        try {
            // TODO change to proper mechanism to store the BisqToken persistently
            String fromFile = new String(Files.readAllBytes(Paths.get(APS_TOKEN_FILENAME)));
            String[] fromFileArray = fromFile.split(" ");
            if (fromFileArray.length != 3) {
                throw new IOException("invalid aps token format");
            }
            if (!fromFileArray[0].equals(APS_TOKEN_MAGIC)) {
                throw new IOException("invalid aps token format");
            }
            bundleidentifier = fromFileArray[1];
            apsTokenBase58 = fromFileArray[2];
        } catch (IOException e) {
            bundleidentifier = "";
            apsTokenBase58 = "";
        }
    }

    public void fromString(String s) {
        String[] stringArray = s.split(" ");

        if (stringArray.length == 3 && stringArray[0].equals(APS_TOKEN_MAGIC)) {
            bundleidentifier = stringArray[1];
            apsTokenBase58 = stringArray[2];
        } else {
            System.out.println("wrong token: " + s);
            bundleidentifier = "";
            apsTokenBase58 = "";
        }
        save();
    }

    private void save() {
        // TODO change to proper mechanism to store the BisqToken persistently
        try {
            PrintStream out = new PrintStream(new FileOutputStream(APS_TOKEN_FILENAME));
            out.print(APS_TOKEN_MAGIC+" "+bundleidentifier+" "+apsTokenBase58);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public String asBase58() {
        return apsTokenBase58;
    }

    public String asHex() {
        byte[] binary;
        try {
            binary = Base58.decode(apsTokenBase58);
        } catch (AddressFormatException e) {
            return "error decoding apsToken";
        }

        String hex = BaseEncoding.base16().lowerCase().encode(binary);
        System.out.println("hex = " + hex);
        return hex;
    }

    public static synchronized BisqToken getInstance(){
        if(instance == null){
            instance = new BisqToken();
        }
        return instance;
    }

}