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

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Provides a base for which message payloads that carry informational content extend from.
 * Informational messages provide information without requiring user action.
 *
 * @see AbstractMessagePayload
 */
@Getter
@ToString(callSuper = true)
abstract class AbstractInformationalMessagePayload extends AbstractMessagePayload
        implements InformationalMessagePayload {
    private final String title;
    private final String message;
    private final String txId;

    /**
     * Constructs an instance of {@link AbstractInformationalMessagePayload} and initializes
     * the payload with informational properties from the provided builder.
     *
     * @param builder the {@link MessagePayloadFactory.MessagePayloadBuilder} to configure payload properties.
     */
    protected AbstractInformationalMessagePayload(@NotNull final MessagePayloadFactory.MessagePayloadBuilder builder) {
        super(builder);
        this.title = builder.title;
        this.message = builder.message;
        this.txId = builder.txId;
    }
}
