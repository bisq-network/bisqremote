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

import bisq.remote.notification.message.MessageEncryption;
import lombok.NonNull;

import java.util.Objects;
import java.util.regex.Pattern;

public record PairingToken(
        String magic,
        String descriptor,
        String key,
        String notificationToken
) {
    private static final String SEPARATOR = "|";

    public static PairingToken fromString(@NonNull final String pairingToken) {
        Objects.requireNonNull(pairingToken, "pairingToken must not be null");

        final String[] pairingTokenParts = pairingToken.split(Pattern.quote(SEPARATOR));
        if (pairingTokenParts.length != 4) {
            throw new IllegalArgumentException("Invalid pairingToken; must be four sections separated by " + SEPARATOR);
        }

        final String magic = pairingTokenParts[0];
        final String descriptor = pairingTokenParts[1];
        final String key = pairingTokenParts[2];
        final String notificationToken = pairingTokenParts[3];

        MessageEncryption.validateKey(key);

        return new PairingToken(magic, descriptor, key, notificationToken);
    }

    public String asString() {
        return magic + SEPARATOR + descriptor + SEPARATOR + key + SEPARATOR + notificationToken;
    }
}
