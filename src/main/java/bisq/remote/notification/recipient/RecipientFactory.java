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

import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static bisq.remote.notification.recipient.AndroidRecipient.ANDROID;
import static bisq.remote.notification.recipient.IosDevRecipient.IOS_DEV;
import static bisq.remote.notification.recipient.IosRecipient.IOS;

@Component
public class RecipientFactory {
    public Recipient createFromPairingToken(@NonNull final PairingToken pairingToken) {
        Objects.requireNonNull(pairingToken, "pairingToken must not be null");

        final Recipient recipient;
        if (pairingToken.magic().equalsIgnoreCase(ANDROID)) {
            recipient = new AndroidRecipient(pairingToken.descriptor(), pairingToken.key(), pairingToken.notificationToken());
        } else if (pairingToken.magic().equalsIgnoreCase(IOS)) {
            recipient = new IosRecipient(pairingToken.descriptor(), pairingToken.key(), pairingToken.notificationToken());
        } else if (pairingToken.magic().equalsIgnoreCase(IOS_DEV)) {
            recipient = new IosDevRecipient(pairingToken.descriptor(), pairingToken.key(), pairingToken.notificationToken());
        } else {
            throw new IllegalArgumentException("Unknown magic value: " + pairingToken.magic());
        }

        if (pairingToken.notificationToken().length() != recipient.getNotificationTokenLength()) {
            throw new IllegalArgumentException(String.format(
                    "Invalid notification token; length is %s characters, must be %s characters",
                    pairingToken.notificationToken().length(), recipient.getNotificationTokenLength()));
        }

        return recipient;
    }
}
