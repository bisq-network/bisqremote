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

class PairingTokenTest {
    @Test
    void creatingFromNullPairingToken_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PairingToken.fromString(null));
    }

    @Test
    void creatingFromEmptyPairingTokenString_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PairingToken.fromString(""));
    }

    @Test
    void creatingFromPairingTokenStringWithInvalidKey_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> PairingToken.fromString(
                "android|Google sdk_gphone_x86|ximqjxdRhe4HSxyh28m/FiIPlv8HEgV|d4HedtovQCyRdgPsxM0JbA:" +
                        "APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6xkwIq8LZVPCyKVi1nh5NdG37TN2nGhp" +
                        "qchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6"));
    }

    @Test
    void creatingFromValidPairingTokenString_returnsPairingToken() {
        final PairingToken token = PairingToken.fromString(
                "android|Google sdk_gphone_x86|aximqjxdRhe4HSxyh28m/FiIPlv8HEgV|d4HedtovQCyRdgPsxM0JbA:" +
                        "APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6xkwIq8LZVPCyKVi1nh5NdG37TN2nGhpq" +
                        "chOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6");
        assertThat(token.magic()).isEqualTo("android");
        assertThat(token.descriptor()).isEqualTo("Google sdk_gphone_x86");
        assertThat(token.key()).isEqualTo("aximqjxdRhe4HSxyh28m/FiIPlv8HEgV");
        assertThat(token.notificationToken()).isEqualTo(
                "d4HedtovQCyRdgPsxM0JbA:APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6xkwIq8" +
                        "LZVPCyKVi1nh5NdG37TN2nGhpqchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6");
    }

    @Test
    void gettingAsString_returnsPairingTokenAsString() {
        final PairingToken token = new PairingToken(
                "android",
                "Google sdk_gphone_x86",
                "aximqjxdRhe4HSxyh28m/FiIPlv8HEgV",
                "d4HedtovQCyRdgPsxM0JbA:APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6" +
                        "xkwIq8LZVPCyKVi1nh5NdG37TN2nGhpqchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6");
        assertThat(token.asString()).isEqualTo(
                "android|Google sdk_gphone_x86|aximqjxdRhe4HSxyh28m/FiIPlv8HEgV|d4HedtovQCyRdgPsxM0JbA:" +
                        "APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6xkwIq8LZVPCyKVi1nh5NdG37TN2nGh" +
                        "pqchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6");
    }
}
