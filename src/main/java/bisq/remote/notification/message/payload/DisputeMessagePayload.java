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

package bisq.remote.notification.message.payload;

import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString(callSuper = true)
public class DisputeMessagePayload extends AbstractActionableMessagePayload {
    public static final String DISPUTE = "DISPUTE";

    public DisputeMessagePayload(@NotNull final MessagePayloadFactory.MessagePayloadBuilder builder) {
        super(builder);
    }

    @Override
    protected String getPayloadType() {
        return DISPUTE;
    }
}
