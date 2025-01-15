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
 * Represents an informational message payload, defining the required structure for all
 * informational message payload types.
 */
public interface InformationalMessagePayload extends MessagePayload {
    /**
     * Retrieves the title of the message payload. The title is typically a short, descriptive
     * header meant to summarize the content or intent of the message.
     *
     * @return the title of the message payload as a {@link String}.
     */
    String getTitle();

    /**
     * Retrieves the main content or body of the message payload. This is the primary text
     * intended to be communicated to the recipient, containing detailed information.
     *
     * @return the message content as a {@link String}.
     */
    String getMessage();

    /**
     * Retrieves the transaction identifier associated with the message.
     *
     * @return the transaction ID as a {@link String}.
     */
    String getTxId();
}
