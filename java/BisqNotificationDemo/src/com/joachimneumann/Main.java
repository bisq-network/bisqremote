package com.joachimneumann;

import java.security.AlgorithmParameters;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bitcoinj.core.Base58;

public class Main {

    private static final String  SYM_KEY_ALGO = "AES";
    private static final Integer SYM_KEY_BITS = 256;

    public static void main(String[] args) {


        String BisqNotification = "{ message: \"TRADE_ACCEPTED\"}";

        try {

            /* generate the key */
            KeyGenerator keyPairGenerator = KeyGenerator.getInstance(SYM_KEY_ALGO);
            keyPairGenerator.init(SYM_KEY_BITS);
            SecretKey secretKey =  keyPairGenerator.generateKey();
            String secretKeyBase58 = Base58.encode(secretKey.getEncoded());
            System.out.println("Secret key (Base58): "+secretKeyBase58);

            /* Encrypt the message. */
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(BisqNotification.getBytes("UTF-8"));
            String ciphertextBase58 = Base58.encode(ciphertext);
            System.out.println("Encrypted message (Base58): "+ciphertextBase58);

            /* This is that iOS has to do */
            byte[] secretKeyDecoded = Base58.decode(secretKeyBase58);
            SecretKey secretKeyReceived = new SecretKeySpec(secretKeyDecoded, 0, secretKeyDecoded.length, "AES");

            byte[] ciphertextDecoded = Base58.decode(ciphertextBase58);
            cipher.init(Cipher.DECRYPT_MODE, secretKeyReceived, new IvParameterSpec(iv));
            String plaintext = new String(cipher.doFinal(ciphertextDecoded), "UTF-8");

            /* Did it work? */
            System.out.println("iOS should decrypt to: "+plaintext + ", equal: "+(plaintext.equals(BisqNotification)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
