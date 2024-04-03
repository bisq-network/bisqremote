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

package bisq.remote.notification.recipient;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecipientFactoryTest {
    final RecipientFactory recipientFactory = new RecipientFactory();

    @Test
    void creatingFromNullPairingToken_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> recipientFactory.createFromPairingToken(null));
    }

    @Test
    void creatingFromValidAndroidPairingToken_returnsAndroidRecipient() {
        final String pairingToken =
                "android|Google sdk_gphone_x86|aximqjxdRhe4HSxyh28m/FiIPlv8HEgV|d4HedtovQCyRdgPsx" +
                        "M0JbA:APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6xkwIq8LZVPCyKVi1nh5N" +
                        "dG37TN2nGhpqchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6";

        final Recipient recipient = recipientFactory.createFromPairingToken(PairingToken.fromString(pairingToken));

        assertThat(recipient).isInstanceOf(AndroidRecipient.class);
        assertThat(recipient.getClientMagic()).isEqualTo("android");
        assertThat(recipient.getPairingToken().asString()).isEqualTo(pairingToken);
    }

    @Test
    void creatingFromValidIosPairingToken_returnsIosRecipient() {
        final String pairingToken =
                "iOS|iPad|aximqjxdRhe4HSxyh28m/FiIPlv8HEgV|d4HedtovQCyRdgPsxM0JbA:" +
                        "APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x6";

        final Recipient recipient = recipientFactory.createFromPairingToken(PairingToken.fromString(pairingToken));

        assertThat(recipient).isInstanceOf(IosRecipient.class);
        assertThat(recipient.getClientMagic()).isEqualTo("iOS");
        assertThat(recipient.getPairingToken().asString()).isEqualTo(pairingToken);
    }

    @Test
    void creatingFromValidIosDevPairingToken_returnsIosDevRecipient() {
        final String pairingToken =
                "iOSDev|iPhone|aximqjxdRhe4HSxyh28m/FiIPlv8HEgV|d4HedtovQCyRdgPsxM0JbA:" +
                        "APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x6";

        final Recipient recipient = recipientFactory.createFromPairingToken(PairingToken.fromString(pairingToken));

        assertThat(recipient).isInstanceOf(IosDevRecipient.class);
        assertThat(recipient.getClientMagic()).isEqualTo("iOSDev");
        assertThat(recipient.getPairingToken().asString()).isEqualTo(pairingToken);
    }

    @Test
    void creatingWithUnknownMagicValue_throwsIllegalArgumentException() {
        final PairingToken pairingToken = PairingToken.fromString(
                "unknown|unknown|aximqjxdRhe4HSxyh28m/FiIPlv8HEgV|d4HedtovQCyRdgPsxM0JbA:APA91b" +
                        "FJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6xkwIq8LZVPCyKVi1nh5NdG37TN2nGhp" +
                        "qchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6");

        assertThrows(IllegalArgumentException.class, () -> recipientFactory.createFromPairingToken(pairingToken));
    }

    @Test
    void creatingWithInvalidNotificationToken_throwsIllegalArgumentException() {
        final PairingToken pairingToken = PairingToken.fromString(
                "android|Google sdk_gphone_x86|aximqjxdRhe4HSxyh28m/FiIPlv8HEgV|invalid");

        assertThrows(IllegalArgumentException.class, () -> recipientFactory.createFromPairingToken(pairingToken));
    }
}
