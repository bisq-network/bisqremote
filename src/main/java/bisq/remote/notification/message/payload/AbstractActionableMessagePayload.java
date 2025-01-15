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
 * Provides a base for which message payloads that require user action,
 * in addition to conveying information, to extend from.
 *
 * @see AbstractInformationalMessagePayload
 */
@Getter
@ToString(callSuper = true)
abstract class AbstractActionableMessagePayload extends AbstractInformationalMessagePayload
        implements ActionableMessagePayload {
    private final String actionRequired;

    /**
     * Constructs an instance of {@link AbstractActionableMessagePayload} and initializes
     * the payload with actionable properties from the provided builder.
     *
     * @param builder the {@link MessagePayloadFactory.MessagePayloadBuilder} to configure payload properties.
     */
    public AbstractActionableMessagePayload(@NotNull final MessagePayloadFactory.MessagePayloadBuilder builder) {
        super(builder);
        this.actionRequired = builder.actionRequired;
    }
}
