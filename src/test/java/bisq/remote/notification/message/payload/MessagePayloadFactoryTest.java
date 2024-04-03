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

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessagePayloadFactoryTest {
    private final MessagePayloadFactory factory = new MessagePayloadFactory();

    @Test
    void testCreateDisputeMessagePayload() throws Exception {
        final String jsonInput =
                "{\"type\":\"DISPUTE\",\"txId\":\"dispute123\",\"title\":\"Dispute Title\"," +
                        "\"message\":\"Dispute Message\",\"actionRequired\":\"Yes\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(DisputeMessagePayload.class);
        final DisputeMessagePayload disputePayload = (DisputeMessagePayload) payload;
        assertThat(disputePayload.getTxId()).isEqualTo("dispute123");
        assertThat(disputePayload.getTitle()).isEqualTo("Dispute Title");
        assertThat(disputePayload.getMessage()).isEqualTo("Dispute Message");
        assertThat(disputePayload.getActionRequired()).isEqualTo("Yes");
    }

    @Test
    void testCreateEraseMessagePayload() throws Exception {
        final String jsonInput =
                "{\"type\":\"ERASE\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(EraseMessagePayload.class);
    }

    @Test
    void testCreateMarketMessagePayload() throws ParseException {
        final String jsonInput =
                "{\"type\":\"MARKET\",\"txId\":\"market123\",\"title\":\"Market Title\"," +
                        "\"message\":\"Market Message\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(MarketMessagePayload.class);
        final MarketMessagePayload offerPayload = (MarketMessagePayload) payload;
        assertThat(offerPayload.getTxId()).isEqualTo("market123");
        assertThat(offerPayload.getTitle()).isEqualTo("Market Title");
        assertThat(offerPayload.getMessage()).isEqualTo("Market Message");
    }

    @Test
    void testCreateOfferMessagePayload() throws ParseException {
        final String jsonInput =
                "{\"type\":\"OFFER\",\"txId\":\"offer123\",\"title\":\"Offer Title\"," +
                        "\"message\":\"Offer Message\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(OfferMessagePayload.class);
        final OfferMessagePayload offerPayload = (OfferMessagePayload) payload;
        assertThat(offerPayload.getTxId()).isEqualTo("offer123");
        assertThat(offerPayload.getTitle()).isEqualTo("Offer Title");
        assertThat(offerPayload.getMessage()).isEqualTo("Offer Message");
    }

    @Test
    void testCreatePriceAlertMessagePayload() throws ParseException {
        final String jsonInput =
                "{\"type\":\"PRICE\",\"txId\":\"price123\",\"title\":\"Price Title\"," +
                        "\"message\":\"Price Message\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(PriceAlertMessagePayload.class);
        final PriceAlertMessagePayload pricePayload = (PriceAlertMessagePayload) payload;
        assertThat(pricePayload.getTxId()).isEqualTo("price123");
        assertThat(pricePayload.getTitle()).isEqualTo("Price Title");
        assertThat(pricePayload.getMessage()).isEqualTo("Price Message");
    }

    @Test
    void testCreateSetupConfirmationMessagePayload() throws Exception {
        final String jsonInput =
                "{\"type\":\"SETUP_CONFIRMATION\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(SetupConfirmationPayload.class);
    }

    @Test
    void testCreateTradeMessagePayload() throws ParseException {
        final String jsonInput =
                "{\"type\":\"TRADE\",\"txId\":\"trade123\",\"title\":\"Trade Title\"," +
                        "\"message\":\"Trade Message\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(TradeMessagePayload.class);
        final TradeMessagePayload tradePayload = (TradeMessagePayload) payload;
        assertThat(tradePayload.getTxId()).isEqualTo("trade123");
        assertThat(tradePayload.getTitle()).isEqualTo("Trade Title");
        assertThat(tradePayload.getMessage()).isEqualTo("Trade Message");
    }

    @Test
    void testCreateWithUnknownType() {
        final String jsonInput = "{\"type\":\"UNKNOWN_TYPE\"}";

        final Exception exception = assertThrows(IllegalArgumentException.class, () ->
                factory.create(jsonInput));

        assertThat(exception.getMessage()).contains("Unknown type: UNKNOWN_TYPE");
    }

    @Test
    void testCreateWithMalformedJson() {
        // Missing closing brace in JSON
        final String jsonInput = "{\"type\":\"OFFER\", \"txId\":\"offer123\"";

        assertThrows(ParseException.class, () -> factory.create(jsonInput));
    }

    @Test
    void testCreateWithNullFields() throws ParseException {
        final String jsonInput =
                "{\"type\":\"OFFER\",\"txId\":null,\"title\":null,\"message\":null}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(OfferMessagePayload.class);
        final OfferMessagePayload offerPayload = (OfferMessagePayload) payload;
        assertThat(offerPayload.getTxId()).isEqualTo(null);
        assertThat(offerPayload.getTitle()).isEqualTo(null);
        assertThat(offerPayload.getMessage()).isEqualTo(null);
    }

    @Test
    void testCreateWithMissingFields() throws ParseException {
        final String jsonInput = "{\"type\":\"OFFER\"}";

        final MessagePayload payload = factory.create(jsonInput);

        assertThat(payload).isInstanceOf(OfferMessagePayload.class);
        final OfferMessagePayload offerPayload = (OfferMessagePayload) payload;
        assertThat(offerPayload.getTxId()).isEqualTo(null);
        assertThat(offerPayload.getTitle()).isEqualTo(null);
        assertThat(offerPayload.getMessage()).isEqualTo(null);
    }
}
