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

import lombok.ToString;

@ToString(callSuper = true)
public class IosDevRecipient extends AbstractRecipient {
    public static final String IOS_DEV = "iOSDev";
    public static final String BISQ_MESSAGE_IOS = "BisqMessageiOS";
    public static final int NOTIFICATION_TOKEN_LENGTH = 64;

    public IosDevRecipient(String descriptor, String key, String notificationToken) {
        super(descriptor, key, notificationToken);
    }

    @Override
    public String getClientMagic() {
        return IOS_DEV;
    }

    @Override
    public String getMessageMagic() {
        return BISQ_MESSAGE_IOS;
    }

    @Override
    public int getNotificationTokenLength() {
        return NOTIFICATION_TOKEN_LENGTH;
    }
}
