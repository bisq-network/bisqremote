package bisq.notification;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class BisqKey {
    private static final String BISQ_KEY_FILENAME = "key.txt";
    public static final String BISQ_KEY_MAGIC = "BisqKey";
    public static final String BISQ_MESSAGE_MAGIC = "BisqEncrypted";

    private String key;
    private CryptoHelper cryptoHelper;

    public BisqKey() {
        readOrCreateKey();
        cryptoHelper = new CryptoHelper(key);
    }

    public String encryptBisqMessage(String message) {
        try {
            // generate 16 random characters for iv
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replace("-", "");
            String tempIV = uuid.substring(0, 16);

            StringBuffer padded = new StringBuffer(message);
            while (padded.length() % 16 != 0)
            {
                padded.append(" ");
            }
            message = padded.toString();

            String cipher = cryptoHelper.encrypt(message, tempIV);
            String combined =  BISQ_MESSAGE_MAGIC+" "+tempIV+" "+cipher;

            System.out.println("combined: "+combined);
            return combined;
        } catch (Exception e) {
            e.printStackTrace();
            return "error in encryptBisqMessage";
        }
    }


    public String key() {
        return key;
    }

    private void readOrCreateKey() {
        // TODO change to proper mechanism to store the BisqKey persistently
        String fromFile = null;
        try {
            fromFile = new String(Files.readAllBytes(Paths.get(BISQ_KEY_FILENAME)));
            String[] fromFileArray = fromFile.split(" ");
            if (fromFileArray.length != 2) {
                System.out.println("invalid "+BISQ_KEY_MAGIC+" format");
                throw new IOException("invalid "+BISQ_KEY_MAGIC+" format");
            }
            if (!fromFileArray[0].equals(BISQ_KEY_MAGIC)) {
                System.out.println("invalid "+BISQ_KEY_MAGIC+" format");
                throw new IOException("invalid "+BISQ_KEY_MAGIC+" format");
            }
            if (fromFileArray[1].length() != 32) {
                System.out.println("invalid "+BISQ_KEY_MAGIC+" format");
                throw new IOException("invalid "+BISQ_KEY_MAGIC+" format");
            }
            key = fromFileArray[1];
        } catch (IOException e) {
            newKey();
        }
    }


    private void saveKey() {
        // TODO change to proper mechanism to store the BisqKey persistently
        try {
            PrintStream out = new PrintStream(new FileOutputStream(BISQ_KEY_FILENAME));
            out.print(withMagic());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void newKey() {
        String uuid = UUID.randomUUID().toString();
        key = uuid.replace("-", "");
        System.out.println(key.length());
        saveKey();
    }

    public String withMagic() {
        return BISQ_KEY_MAGIC+" "+key;
    }

}
