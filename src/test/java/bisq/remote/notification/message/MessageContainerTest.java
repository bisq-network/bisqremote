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
import bisq.remote.notification.message.payload.MessagePayloadFactory;
import bisq.remote.notification.message.payload.PriceAlertMessagePayload;
import org.bouncycastle.crypto.CryptoException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MessageContainerTest {
    private final MessageEncryption mockEncryption = Mockito.mock(MessageEncryption.class);

    private final MessagePayload messagePayload = new PriceAlertMessagePayload(
            new MessagePayloadFactory.MessagePayloadBuilder()
                    .title("Price alert for United States Dollar")
                    .message("Your price alert got triggered. The current" +
                            " United States Dollar price is 35351.08 BTC/USD"));
    final MessageContainer container = new MessageContainer("magic", messagePayload, mockEncryption);

    @Test
    void testEncryptedContent() throws CryptoException, NoSuchPaddingException, NoSuchAlgorithmException {
        when(mockEncryption.generateIv()).thenReturn("1234567890123456");
        when(mockEncryption.encrypt(Mockito.anyString(), Mockito.anyString()))
                .thenReturn("encryptedPayload");

        final String result = container.encryptedContent();

        assertThat(result).isNotNull();
        assertThat(result).matches("^magic\\|([0-9a-fA-F]{16})\\|encryptedPayload$");
    }

    @Test
    void testEncryptedContentWithException() throws CryptoException, NoSuchPaddingException, NoSuchAlgorithmException {
        when(mockEncryption.generateIv()).thenReturn("1234567890123456");
        when(mockEncryption.encrypt(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new CryptoException("Test exception"));

        assertThat(container.encryptedContent()).isNull();
    }
}
