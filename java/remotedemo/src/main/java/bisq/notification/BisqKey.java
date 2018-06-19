package bisq.notification;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BisqKey {
    private static final String BISQ_KEY_FILENAME = "SecretKey.txt";
    private static final String BISQ_KEY_MAGIC = "BisqKey";
    private static final String  SYM_KEY_ALGO = "AES";
    private static final Integer SYM_KEY_BITS = 256;

    private static BisqKey instance;

    private String secretKeyBase58;
    private SecretKey secretKey;

    private BisqKey() {
        readBase58();
        secretKeyFromBase58();
    }

    private void readBase58() {
        // TODO change to proper mechanism to store the BisqKey persistently
        String fromFile = null;
        try {
            fromFile = new String(Files.readAllBytes(Paths.get(BISQ_KEY_FILENAME)));
            String[] fromFileArray = fromFile.split(" ");
            if (fromFileArray.length != 2) {
                throw new IOException("invalid "+BISQ_KEY_MAGIC+" format");
            }
            if (!fromFileArray[0].equals(BISQ_KEY_MAGIC)) {
                throw new IOException("invalid "+BISQ_KEY_MAGIC+" format");
            }
            secretKeyBase58 = fromFileArray[1];
        } catch (IOException e) {
            newKey();
        }
    }

    private void secretKeyFromBase58() {
        byte[] bytes = new byte[0];
        try {
            bytes = Base58.decode(secretKeyBase58);
            if (bytes.length != 32) {
                throw new AddressFormatException("key read has zero length");
            };
            secretKey = new SecretKeySpec(bytes, 0, bytes.length, SYM_KEY_ALGO);
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        // TODO change to proper mechanism to store the BisqKey persistently
        try {
            PrintStream out = new PrintStream(new FileOutputStream(BISQ_KEY_FILENAME));
            out.print(BISQ_KEY_MAGIC+" "+secretKeyBase58);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void fromString(String s) {
        String[] stringArray = s.split(" ");

        if (stringArray.length == 3 && stringArray[0].equals(BISQ_KEY_MAGIC)) {
            secretKeyBase58 = stringArray[1];
            secretKeyFromBase58();
        } else {
            System.out.println("wrong key: " + s);
            secretKeyBase58 = "";
            secretKey = null;
        }
        save();
    }

    public void newKey() {
        secretKey = generateSecretKey(SYM_KEY_BITS);
        try {
            secretKeyBase58 = Base58.encode(secretKey.getEncoded());
        } catch (Exception e) {
            secretKeyBase58 = "";
        }
        save();
    }

    public String asBase58() {
        return secretKeyBase58;
    }


    private SecretKey generateSecretKey(int bits) {
        try {
            KeyGenerator keyPairGenerator = KeyGenerator.getInstance(SYM_KEY_ALGO);
            keyPairGenerator.init(bits);
            return keyPairGenerator.generateKey();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized BisqKey getInstance(){
        if(instance == null){
            instance = new BisqKey();
        }
        return instance;
    }
}
