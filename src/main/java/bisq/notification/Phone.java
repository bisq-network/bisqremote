package bisq.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Phone {
    private static final String PHONE_MAGIC_IOS     = "iOS";
    private static final String PHONE_MAGIC_IOS_DEV = "iOSDev";
    private static final String PHONE_MAGIC_ANDROID = "android";
    static final String PHONE_SEPARATOR_ESCAPED   = "\\|"; // see https://stackoverflow.com/questions/5675704/java-string-split-not-returning-the-right-values
    static final String PHONE_SEPARATOR_WRITING = "|";
    private static final String PHONE_FILENAME = "BisqPairingToken.txt";
    private CryptoHelper cryptoHelper;

    public enum OS {
        iOS, iOSDev, Android, undefined
    }

    public OS os = OS.undefined;
    public String descriptor;
    public String key;
    public String notificationToken;
    public Boolean isInitialized;
    private Logger logger = LoggerFactory.getLogger(getClass().getName());


    public String encrypt(String cipher, String iv) {
        try {
            return cryptoHelper.encrypt(cipher, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String cipher, String iv) {
        try {
            return cryptoHelper.decrypt(cipher, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Phone() {
        isInitialized = false;
        try {
            // TODO change to proper mechanism to store the BisqToken persistently
            String fromFile = new String(Files.readAllBytes(Paths.get(PHONE_FILENAME)));
            if (fromFile != null) {
                Boolean ignore = fromString(fromFile);
            }
        } catch (IOException e) {
            key = "";
            notificationToken = "";
            os = OS.undefined;
        }
    }

    // return value: confirmation notification required?
    public boolean fromString(String s) {
        if (phoneID() == s) return false; // nothing new - no confirmation notification action required
        logger.info(s);
        String[] a = s.split(PHONE_SEPARATOR_ESCAPED);
        try {
            if (a.length != 4) {
                throw new IOException("invalid Bisq Phone ID format: not three sections separated by "+PHONE_SEPARATOR_WRITING+" n="+a.length+" s="+s);
            }
            if (a[2].length() != 32) {
                throw new IOException("invalid Bisq Phone ID format: key not 32 bytes");
            }
            if (a[0].equals(PHONE_MAGIC_IOS)) {
                os = OS.iOS;
                if (a[3].length() != 64) {
                    throw new IOException("invalid Bisq Phone ID format: iOS token not 64 bytes");
                }
            } else if (a[0].equals(PHONE_MAGIC_IOS_DEV)) {
                os = OS.iOSDev;
                if (a[3].length() != 64) {
                    throw new IOException("invalid Bisq Phone ID format: iOS token not 64 bytes");
                }
            } else if (a[0].equals(PHONE_MAGIC_ANDROID)){
                os = OS.Android;
                if (a[3].length() < 32) {
                    throw new IOException("invalid Bisq Phone ID format: Android token too short (<32 bytes)");
                }
            } else {
                throw new IOException("invalid Bisq Phone ID format");
            }
            descriptor = a[1];
            key = a[2];
            notificationToken = a[3];
            isInitialized = true;
            cryptoHelper = new CryptoHelper(key);
            save();
            return true;
        }
        catch (IOException e) {
            key = "";
            descriptor = "";
            notificationToken = "";
            isInitialized = false;
            logger.error(e.getMessage());
            cryptoHelper = null;
        }
        return false;
    }

    public String phoneID() {
        if (os == OS.iOS) {
            return PHONE_MAGIC_IOS + PHONE_SEPARATOR_WRITING + key + PHONE_SEPARATOR_WRITING + notificationToken;
        } else if (os == OS.iOSDev) {
            return PHONE_MAGIC_IOS_DEV + PHONE_SEPARATOR_WRITING + key + PHONE_SEPARATOR_WRITING + notificationToken;
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
            out.print(phoneID());
            out.close();
            isInitialized = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
