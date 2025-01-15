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

import bisq.remote.notification.message.payload.MessagePayload;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.CryptoException;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * A container for encapsulating message details along with the encryption strategy to securely
 * transmit messages.
 *
 * <p>The {@link MessageContainer} includes:
 * <ul>
 *     <li>{@code messageMagic} - An identifier used to represent the type of recipient.</li>
 *     <li>{@code payload} - The actual message content that needs to be secured.</li>
 *     <li>{@code encryption} - The encryption strategy to secure the message content.</li>
 * </ul>
 * </p>
 */
@Slf4j
public record MessageContainer(
        String messageMagic,
        MessagePayload payload,
        MessageEncryption encryption) {

    private static final String SEPARATOR = "|";

    /**
     * Constructs the final encrypted message format by encrypting the payload using the provided
     * {@link MessageEncryption} strategy.
     *
     * @return a {@link String} that represents the concatenated value of the message magic,
     *         an initialization vector (IV), and the encrypted payload; separated by {@value SEPARATOR}.
     */
    public String encryptedContent() {
        final String iv = encryption.generateIv();
        try {
            final String encryptedPayload = encryption.encrypt(
                    MessageEncryption.padContentToEncrypt(payload.asJsonString()), iv);
            return messageMagic + SEPARATOR + iv + SEPARATOR + encryptedPayload;
        } catch (IllegalArgumentException | CryptoException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
