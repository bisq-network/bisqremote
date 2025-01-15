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
 * Represents an actionable message payload, defining the required structure for all
 * actionable message payload types.
 */
public interface ActionableMessagePayload extends InformationalMessagePayload {
    /**
     * Retrieves the action to be taken. The action contains a concise description
     * of the action expected to be taken by the recipient.
     *
     * @return the required action as a {@link String}.
     */
    String getActionRequired();
}
