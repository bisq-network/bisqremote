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

import org.bouncycastle.crypto.CryptoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageEncryptionTest {
    private MessageEncryption encryption;

    private final String key = "12345678901234567890123456789012"; // 32-char key for AES
    private final String iv = "1234567890123456"; // 16-char IV for AES

    @BeforeEach
    void setUp() {
        encryption = new MessageEncryption();
        encryption.setKey(key);
    }

    @Test
    void testEncryptDecrypt() throws CryptoException, NoSuchPaddingException, NoSuchAlgorithmException {
        final String originalContent = "Hello, world!";
        final String paddedContent = "Hello, world!   ";

        final String encryptedContent = encryption.encrypt(originalContent, iv);
        assertThat(encryptedContent).isNotNull();

        final String decryptedContent = encryption.decrypt(encryptedContent, iv);
        assertThat(decryptedContent).isEqualTo(paddedContent);
    }

    @Test
    void testSetInvalidKeyLength() {
        assertThrows(IllegalArgumentException.class, () -> encryption.setKey("short"));
    }

    @Test
    void testEncryptWithInvalidIvLength() {
        assertThrows(IllegalArgumentException.class, () ->
                encryption.encrypt("unencryptedData", "shortIV"));
    }

    @Test
    void testDecryptWithInvalidIvLength() {
        assertThrows(IllegalArgumentException.class, () ->
                encryption.decrypt("encryptedData", "shortIV"));
    }

    @Test
    void testEncryptEmptyString() throws NoSuchPaddingException, NoSuchAlgorithmException, CryptoException {
        assertThat(encryption.encrypt("", iv)).isEqualTo("");
    }

    @Test
    void testDecryptEmptyString() throws NoSuchPaddingException, NoSuchAlgorithmException, CryptoException {
        assertThat(encryption.decrypt("", iv)).isEqualTo("");
    }

    @Test
    void testValidateKeyWithValidKey() {
        assertDoesNotThrow(() -> MessageEncryption.validateKey(key));
    }

    @Test
    void testValidateKeyWithInvalidKey() {
        final String invalidKey = "short";

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            MessageEncryption.validateKey(invalidKey));

        assertThat(thrown.getMessage()).contains("Key is not 32 characters");
    }

    @Test
    void testValidateIvWithValidIv() {
        assertDoesNotThrow(() -> MessageEncryption.validateIv(iv));
    }

    @Test
    void testValidateIvWithInvalidIv() {
        final String invalidIv = "short";

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            MessageEncryption.validateIv(invalidIv));

        assertThat(thrown.getMessage()).contains("Initialization vector is not 16 characters");
    }

    @Test
    void testPadContentToEncryptWithExactLength() {
        final String content = "1234567890123456"; // Already a multiple of 16

        final String paddedContent = MessageEncryption.padContentToEncrypt(content);

        assertThat(paddedContent)
                .describedAs("Content should not change if already the correct length")
                .isEqualTo(content);
    }

    @Test
    void testPadContentToEncryptWithShortContent() {
        final String content = "short";

        final String paddedContent = MessageEncryption.padContentToEncrypt(content);

        assertThat(paddedContent.length() % 16)
                .describedAs("Padded content length should be a multiple of 16")
                .isEqualTo(0);
        assertThat(paddedContent)
                .describedAs("Padded content should start with original content")
                .startsWith(content);
    }

    @Test
    void testPadContentToEncryptWithLongContent() {
        final String content = "123456789012345"; // 15 characters

        final String paddedContent = MessageEncryption.padContentToEncrypt(content);

        assertThat(paddedContent.length())
                .describedAs("Padded content should be padded to next multiple of 16")
                .isEqualTo(16);
        assertThat(paddedContent)
                .describedAs("Padded content should be padded with spaces")
                .endsWith(" ");
    }
}
