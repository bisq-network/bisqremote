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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A factory class for creating instances of {@link MessagePayload} from JSON data.
 * This class utilizes a registry of payload types and their respective construction logic
 * encapsulated within {@link Function} objects mapped to payload type identifiers.
 */
@Component
public class MessagePayloadFactory {
    private final Map<String, Function<JSONObject, MessagePayload>> payloadCreators;

    public MessagePayloadFactory() {
        payloadCreators = new HashMap<>();
        registerPayloadTypes();
    }

    private void registerPayloadTypes() {
        payloadCreators.put(DisputeMessagePayload.DISPUTE, json -> buildPayload(json, DisputeMessagePayload::new));
        payloadCreators.put(EraseMessagePayload.ERASE, json -> buildPayload(json, EraseMessagePayload::new));
        payloadCreators.put(MarketMessagePayload.MARKET, json -> buildPayload(json, MarketMessagePayload::new));
        payloadCreators.put(OfferMessagePayload.OFFER, json -> buildPayload(json, OfferMessagePayload::new));
        payloadCreators.put(PriceAlertMessagePayload.PRICE, json -> buildPayload(json, PriceAlertMessagePayload::new));
        payloadCreators.put(SetupConfirmationPayload.SETUP_CONFIRMATION, json -> buildPayload(json, SetupConfirmationPayload::new));
        payloadCreators.put(TradeMessagePayload.TRADE, json -> buildPayload(json, TradeMessagePayload::new));
    }

    /**
     * Creates a {@link MessagePayload} object from a JSON string based on the 'type' attribute within the JSON.
     *
     * @param payloadJson the JSON string containing the payload data.
     * @return the constructed {@link MessagePayload} object.
     * @throws ParseException if the JSON string cannot be parsed.
     * @throws IllegalArgumentException if the specified type is not recognized.
     */
    public MessagePayload create(@NotNull final String payloadJson) throws ParseException {
        final JSONObject json = (JSONObject) new JSONParser().parse(payloadJson);
        final String type = (String) json.get("type");

        if (!payloadCreators.containsKey(type)) {
            throw new IllegalArgumentException("Unknown type: " + type);
        }

        return payloadCreators.get(type).apply(json);
    }

    private <T extends MessagePayload> T buildPayload(
            @NotNull final JSONObject json,
            @NotNull final Function<MessagePayloadBuilder, T> constructor) {
        return new MessagePayloadBuilder().fromJson(json).build(constructor);
    }

    public static class MessagePayloadBuilder {
        public String type;
        public Integer version;
        public Long sentDate;
        public String title;
        public String message;
        public String txId;
        public String actionRequired;

        public MessagePayloadBuilder type(final String type) {
            this.type = type;
            return this;
        }

        public MessagePayloadBuilder version(final int version) {
            this.version = version;
            return this;
        }

        public MessagePayloadBuilder sentDate(final long sentDate) {
            this.sentDate = sentDate;
            return this;
        }

        public MessagePayloadBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public MessagePayloadBuilder message(final String message) {
            this.message = message;
            return this;
        }

        public MessagePayloadBuilder txId(final String txId) {
            this.txId = txId;
            return this;
        }

        public MessagePayloadBuilder actionRequired(final String actionRequired) {
            this.actionRequired = actionRequired;
            return this;
        }

        public MessagePayloadBuilder fromJson(@NotNull final JSONObject json) {
            this.type = (String) json.get("type");
            this.version = (Integer) json.get("version");
            this.sentDate = (Long) json.get("sentDate");
            this.title = (String) json.get("title");
            this.message = (String) json.get("message");
            this.txId = (String) json.get("txId");
            this.actionRequired = (String) json.get("actionRequired");
            return this;
        }

        public <T extends MessagePayload> T build(Function<MessagePayloadBuilder, T> constructor) {
            return constructor.apply(this);
        }
    }
}
