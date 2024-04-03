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

/**
 * Represents a message payload, defining the required structure for all message payload types.
 */
public interface MessagePayload {
    /**
     * Retrieves the type of the message payload.
     * The type is used to differentiate between the various payloads.
     *
     * @return the type of the payload as a {@link String}.
     */
    String getType();

    /**
     * Retrieves the version of the message payload.
     * This may be used to handle different versions of payload formats.
     *
     * @return the version number as an {@link Integer}, or null if versioning is not applicable.
     */
    Integer getVersion();

    /**
     * Retrieves the time that the message was sent, in milliseconds since the epoch.
     *
     * @return the timestamp as a {@link Long}, or null if the timestamp is not applicable.
     */
    Long getSentDate();

    /**
     * Converts the payload details into a JSON formatted {@link String}.
     *
     * @return a JSON formatted {@link String} representation of the payload.
     */
    String asJsonString();
}
