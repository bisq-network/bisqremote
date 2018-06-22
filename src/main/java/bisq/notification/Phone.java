package bisq.notification;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Phone {
    private static final String PHONE_MAGIC_IOS = "BisqPhoneiOS";
    private static final String PHONE_FILENAME = "Phone.txt";

    public String key;
    public String apsToken;
    public Boolean isInitialized;
    private Logger logger = LoggerFactory.getLogger(getClass().getName());


    public Phone() {
        isInitialized = false;
        try {
            // TODO change to proper mechanism to store the BisqToken persistently
            String fromFile = new String(Files.readAllBytes(Paths.get(PHONE_FILENAME)));
            fromString(fromFile);
        } catch (IOException e) {
            key = "";
            apsToken = "";
        }
    }

    public void fromString(String s) {
        String[] a = s.split("@");
        try {
            if (a.length != 3) {
                throw new IOException("invalid " + PHONE_FILENAME + " format");
            }
            if (a[1].length() != 32) {
                throw new IOException("invalid " + PHONE_FILENAME + " format");
            }
            if (a[2].length() != 64) {
                throw new IOException("invalid " + PHONE_FILENAME + " format");
            }
            if (!a[0].equals(PHONE_MAGIC_IOS)) {
                throw new IOException("invalid " + PHONE_FILENAME + " format");
            }
            key = a[1];
            apsToken = a[2];
            isInitialized = true;
        }
        catch (IOException e) {
            key = "";
            apsToken = "";
            isInitialized = false;
            logger.error(e.getMessage());
        }
    }

    public String description() {
         return PHONE_MAGIC_IOS+"@"+key+"@"+apsToken;
    }

    public void save() {
        // TODO change to proper mechanism to store the BisqToken persistently
        try {
            PrintStream out = new PrintStream(new FileOutputStream(PHONE_FILENAME));
            out.print(description());
            out.close();
            isInitialized = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
