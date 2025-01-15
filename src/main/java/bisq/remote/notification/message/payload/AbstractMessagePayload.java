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

import com.google.gson.Gson;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Provides a base for which all types of message payloads extend from.
 * <p>
 * Individual payload classes can define their unique payload type and additional properties as necessary.
 * Each subclass must implement the {@link #getPayloadType()} method to specify its unique type identifier.
 */
@Getter
@ToString
abstract class AbstractMessagePayload implements MessagePayload {

    private final String type = getPayloadType();
    private final Integer version;
    private final Long sentDate;

    /**
     * Constructs an instance of {@link AbstractMessagePayload} and initializes the
     * version to {@code null}, as it is desirable to omit that in its
     * JSON representation when not explicitly provided, and the sentDate to the current time.
     */
    protected AbstractMessagePayload() {
        this.version = null;
        this.sentDate = System.currentTimeMillis();
    }

    /**
     * Constructs an instance of {@link AbstractMessagePayload} and initializes the
     * payload with specific version and sent date values provided by the
     * {@link MessagePayloadFactory.MessagePayloadBuilder}.
     *
     * @param builder the {@link MessagePayloadFactory.MessagePayloadBuilder} to configure payload properties.
     */
    protected AbstractMessagePayload(@NotNull final MessagePayloadFactory.MessagePayloadBuilder builder) {
        this.version = builder.version;
        this.sentDate = Objects.requireNonNullElse(builder.sentDate, System.currentTimeMillis());
    }

    /**
     * Serializes the payload object to its JSON representation, facilitating easy data interchange and storage.
     *
     * @return a JSON formatted string representing this payload.
     */
    @Override
    public String asJsonString() {
        return new Gson().toJson(this);
    }

    /**
     * Returns the type of this payload. Each subclass should return a unique payload type identifier.
     *
     * @return the payload type as a {@link String}.
     */
    protected abstract String getPayloadType();
}
