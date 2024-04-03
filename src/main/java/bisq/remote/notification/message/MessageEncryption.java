/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.remote.notification.message;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bouncycastle.crypto.CryptoException;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Provides functionality for encrypting and decrypting messages using applicable
 * algorithm and transformation type as expected by the client application.
 */
@Component
public class MessageEncryption {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/NOPadding";
    private static final int KEY_LENGTH = 32;
    private static final int IV_LENGTH = 16;

    private SecretKeySpec keySpec;

    /**
     * Sets the encryption key to be used for all cryptographic operations. The key must meet the
     * required length of {@value #KEY_LENGTH} characters.
     *
     * @param key the secret key for encryption
     * @throws IllegalArgumentException if the key does not meet the required length
     */
    public void setKey(@NotNull final String key) throws IllegalArgumentException {
        validateKey(key);
        keySpec = new SecretKeySpec(key.getBytes(UTF_8), KEY_ALGORITHM);
    }

    /**
     * Validates the length of the encryption key.
     *
     * @param key the encryption key
     * @throws IllegalArgumentException if the key does not meet the required length of {@value #KEY_LENGTH} characters
     */
    public static void validateKey(@NotNull final String key) throws IllegalArgumentException {
        if (key.length() != KEY_LENGTH) {
            throw new IllegalArgumentException(String.format("Key is not %s characters", KEY_LENGTH));
        }
    }

    /**
     * Validates the length of the initialization vector (IV).
     *
     * @param iv the initialization vector
     * @throws IllegalArgumentException if the IV does not meet the required length of {@value #IV_LENGTH} characters
     */
    public static void validateIv(@NotNull final String iv) throws IllegalArgumentException {
        if (iv.length() != IV_LENGTH) {
            throw new IllegalArgumentException(String.format(
                    "Initialization vector is not %s characters [%s]", IV_LENGTH, iv.length()));
        }
    }

    /**
     * Generates a random initialization vector (IV) for encryption; a string of {@value #IV_LENGTH} characters.
     *
     * @return a random initialization vector string
     */
    @NotNull
    public String generateIv() {
        try {
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = SecureRandom.getInstanceStrong();
            random.nextBytes(iv);
            return Base64.getEncoder().encodeToString(iv).substring(0, IV_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, IV_LENGTH);
        }
    }

    /**
     * Due to the use of CBC mode without padding, the content length must be a multiple of the
     * block size ({@value IV_LENGTH}), necessitating padding of the content if it is not already of the
     * appropriate length.
     *
     * @param contentToEncrypt the content   to encrypt
     * @return content padded to the correct length, so it can be encrypted
     */
    public static String padContentToEncrypt(@NotNull final String contentToEncrypt) {
        StringBuilder valueToEncryptBuilder = new StringBuilder(contentToEncrypt);
        while (valueToEncryptBuilder.length() % IV_LENGTH != 0) {
            valueToEncryptBuilder.append(" ");
        }
        return valueToEncryptBuilder.toString();
    }

    /**
     * Encrypts the provided content using the configured key and initialization vector (IV).
     * The content is first padded to ensure it is a multiple of the AES block size.
     *
     * @param contentToEncrypt the plaintext to encrypt
     * @param iv               the initialization vector to use for the encryption (must be {@link #IV_LENGTH} characters long)
     * @return the encrypted content as a base64-encoded string
     * @throws IllegalArgumentException if the content or IV is invalid
     * @throws CryptoException          if the encryption operation fails
     * @throws NoSuchAlgorithmException if the encryption algorithm is not found
     * @throws NoSuchPaddingException   if the padding scheme is not found
     */
    public String encrypt(@NotNull final String contentToEncrypt, @NotNull final String iv)
            throws IllegalArgumentException, CryptoException, NoSuchAlgorithmException, NoSuchPaddingException {
        validateIv(iv);

        final String paddedContent = padContentToEncrypt(contentToEncrypt);

        final IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(UTF_8));
        byte[] encryptedBytes = doEncrypt(paddedContent, ivSpec);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts the provided base64 encoded ciphertext using the configured key and initialization vector (IV).
     *
     * @param contentToDecrypt the base64 encoded ciphertext to decrypt
     * @param iv               the initialization vector that was used for the encryption (must be {@link #IV_LENGTH} characters long)
     * @return the decrypted plaintext
     * @throws IllegalArgumentException if the content or IV is invalid
     * @throws CryptoException          if the decryption operation fails
     * @throws NoSuchAlgorithmException if the decryption algorithm is not found
     * @throws NoSuchPaddingException   if the padding scheme is not found
     */
    public String decrypt(@NotNull final String contentToDecrypt, @NotNull final String iv)
            throws IllegalArgumentException, CryptoException, NoSuchAlgorithmException, NoSuchPaddingException {
        validateIv(iv);

        final IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(UTF_8));
        byte[] decryptedBytes = doDecrypt(contentToDecrypt, ivSpec);
        return new String(decryptedBytes);
    }

    private byte[] doEncrypt(@NotNull final String contentToEncrypt, @NotNull final IvParameterSpec ivSpec)
            throws IllegalArgumentException, CryptoException, NoSuchAlgorithmException, NoSuchPaddingException {
        if (contentToEncrypt.isEmpty()) {
            return "".getBytes();
        }

        try {
            final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(contentToEncrypt.getBytes(UTF_8));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new CryptoException("Failed to encrypt content. " + e.getMessage());
        }
    }

    private byte[] doDecrypt(@NotNull final String contentToDecrypt, @NotNull final IvParameterSpec ivSpec)
            throws IllegalArgumentException, CryptoException, NoSuchAlgorithmException, NoSuchPaddingException {
        if (contentToDecrypt.isEmpty()) {
            return "".getBytes();
        }

        try {
            final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(Base64.getDecoder().decode(contentToDecrypt));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new CryptoException("Failed to decrypt content. " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("algorithm", keySpec.getAlgorithm())
                .append("cipher", CIPHER_TRANSFORMATION)
                .append("format", keySpec.getFormat())
                .toString();
    }
}
