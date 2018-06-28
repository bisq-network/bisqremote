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
    private static final String PHONE_MAGIC_IOS     = "BisqPhoneiOS";
    private static final String PHONE_MAGIC_ANDROID = "BisqPhoneAndroid";
    static final String PHONE_SEPARATOR_SPLIT   = "\\|"; // see https://stackoverflow.com/questions/5675704/java-string-split-not-returning-the-right-values
    static final String PHONE_SEPARATOR_WRITING = "|";
    private static final String PHONE_FILENAME = "BisqPhoneID.txt";

    public enum OS {
        iOS, Android, undefined
    }

    public OS os = OS.undefined;
    public String key;
    public String notificationToken;
    public Boolean isInitialized;
    private Logger logger = LoggerFactory.getLogger(getClass().getName());


    public Phone() {
        isInitialized = false;
        try {
            // TODO change to proper mechanism to store the BisqToken persistently
            String fromFile = new String(Files.readAllBytes(Paths.get(PHONE_FILENAME)));
            if (fromFile != null) {
                fromString(fromFile);
            }
        } catch (IOException e) {
            key = "";
            notificationToken = "";
            os = OS.undefined;
        }
    }

    public void fromString(String s) {
        String[] a = s.split(PHONE_SEPARATOR_SPLIT);
        try {
            if (a.length != 3) {
                throw new IOException("invalid Bisq Phone ID format: not three sections separated by _");
            }
            if (a[1].length() != 32) {
                throw new IOException("invalid Bisq Phone ID format: key not 32 bytes");
            }
            if (a[0].equals(PHONE_MAGIC_IOS)) {
                os = OS.iOS;
                if (a[2].length() != 64) {
                    throw new IOException("invalid Bisq Phone ID format: iOS token not 64 bytes");
                }
            } else if (a[0].equals(PHONE_MAGIC_ANDROID)){
                os = OS.Android;
                if (a[2].length() < 32) {
                    throw new IOException("invalid Bisq Phone ID format: Android token too short (<32 bytes)");
                }
            } else {
                throw new IOException("invalid Bisq Phone ID format");
            }
            key = a[1];
            notificationToken = a[2];
            isInitialized = true;
        }
        catch (IOException e) {
            key = "";
            notificationToken = "";
            isInitialized = false;
            logger.error(e.getMessage());
        }
    }

    public String description() {
        if (os == OS.iOS) {
            return PHONE_MAGIC_IOS + PHONE_SEPARATOR_WRITING + key + PHONE_SEPARATOR_WRITING + notificationToken;
        } else if (os == OS.Android) {
            return PHONE_MAGIC_ANDROID + PHONE_SEPARATOR_WRITING + key + PHONE_SEPARATOR_WRITING + notificationToken;
        } else {
            return null; // invalid Bisq phone ID
        }
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
