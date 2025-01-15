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

package bisq.remote.notification.relay;

import bisq.remote.notification.message.MessageContainer;
import bisq.remote.notification.message.MessageEncryption;
import bisq.remote.notification.message.payload.MessagePayload;
import bisq.remote.notification.message.payload.MessagePayloadFactory;
import bisq.remote.notification.message.payload.PriceAlertMessagePayload;
import bisq.remote.notification.recipient.AndroidRecipient;
import bisq.remote.notification.recipient.IosRecipient;
import bisq.remote.notification.recipient.Recipient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.net.URLEncoder;

import static bisq.remote.notification.relay.RelayClient.APNS_URI;
import static bisq.remote.notification.relay.RelayClient.FCM_URI;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RelayClientTest {
    private WireMockServer wireMockServer;

    private RelayClient relayClient;

    private final MessageEncryption mockEncryption = Mockito.mock(MessageEncryption.class);

    private final MessagePayload messagePayload = new PriceAlertMessagePayload(
            new MessagePayloadFactory.MessagePayloadBuilder()
                    .title("Price alert for United States Dollar")
                    .message("Your price alert got triggered. The current" +
                            " United States Dollar price is 35351.08 BTC/USD"));
    private final MessageContainer messageContainer = new MessageContainer(
            "magic",
            messagePayload,
            mockEncryption);

    private final Recipient iOsRecipient = new IosRecipient(
            "iPad",
            "aximqjxdRhe4HSxyh28m/FiIPlv8HEgV",
            "d4HedtovQCyRdgPsxM0JbA:APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x6");
    private final Recipient androidRecipient = new AndroidRecipient(
            "Google sdk_gphone_x86",
            "aximqjxdRhe4HSxyh28m/FiIPlv8HEgV",
            "d4HedtovQCyRdgPsxM0JbA:APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6x" +
                    "kwIq8LZVPCyKVi1nh5NdG37TN2nGhpqchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6");

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        final String baseUrl = "http://localhost:" + wireMockServer.port();
        final WebClient webClient = new RelayClientConfig().webClient(baseUrl);
        relayClient = new RelayClient(webClient);
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    void testSendApnsNotification_Successful() throws JsonProcessingException {
        final RelayResult relayResult = new RelayResult(true, null, null, false);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonResponse = objectMapper.writeValueAsString(relayResult);

        wireMockServer.stubFor(post(urlPathTemplate(APNS_URI))
                .withPathParam("deviceToken",
                        equalTo(URLEncoder.encode(iOsRecipient.getPairingToken().notificationToken(), UTF_8)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                        .withStatus(200)));

        StepVerifier.create(relayClient.sendNotification(iOsRecipient, messageContainer))
                .expectNext(relayResult)
                .verifyComplete();

        wireMockServer.verify(postRequestedFor(urlPathTemplate(APNS_URI))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    void testSendFcmNotification_Successful() throws JsonProcessingException {
        final RelayResult relayResult = new RelayResult(true, null, null, false);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonResponse = objectMapper.writeValueAsString(relayResult);

        // Setup WireMock to respond to the expected request
        wireMockServer.stubFor(post(urlPathTemplate(FCM_URI))
                .withPathParam("deviceToken",
                        equalTo(URLEncoder.encode(androidRecipient.getPairingToken().notificationToken(), UTF_8)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                        .withStatus(200)));

        StepVerifier.create(relayClient.sendNotification(androidRecipient, messageContainer))
                .expectNext(relayResult)
                .verifyComplete();

        wireMockServer.verify(postRequestedFor(urlPathTemplate(FCM_URI))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    void testSendNotificationUnknownRecipientType() {
        final Recipient unknownRecipient = new AndroidRecipient(
                "Google sdk_gphone_x86",
                "aximqjxdRhe4HSxyh28m/FiIPlv8HEgV",
                "d4HedtovQCyRdgPsxM0JbA:APA91bFJIwRdBpO4SQpeSuA5rpEnu5N3Y3_c1T5x69gpedyKwGLUrApT6x" +
                        "kwIq8LZVPCyKVi1nh5NdG37TN2nGhpqchOUCysHweuL8V023WJYVwGgpUvdkk5mkYD9D3_QFj2c7f_2ul6"
        ) {
            @Override
            public String getClientMagic() {
                return "unknown";
            }
        };

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                relayClient.sendNotification(unknownRecipient, messageContainer));

        assertThat(thrown.getMessage()).contains("Unsupported recipient type: unknown");
    }

    @Test
    void testSendApnsNotification_BadRequest() throws JsonProcessingException {
        final RelayResult relayResult = new RelayResult(false, "400", "Bad Request", true);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String jsonResponse = objectMapper.writeValueAsString(relayResult);

        wireMockServer.stubFor(post(urlPathTemplate(APNS_URI))
                .withPathParam("deviceToken",
                        equalTo(URLEncoder.encode(iOsRecipient.getPairingToken().notificationToken(), UTF_8)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)
                        .withStatus(400)));

        StepVerifier.create(relayClient.sendNotification(iOsRecipient, messageContainer))
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException
                        && ((WebClientResponseException) throwable).getStatusCode().value() == 400
                        && ((WebClientResponseException) throwable).getResponseBodyAsString().equals(jsonResponse))
                .verify();

        wireMockServer.verify(postRequestedFor(urlPathTemplate(APNS_URI))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    void testSendApnsNotification_ServerError() {
        wireMockServer.stubFor(post(urlPathTemplate(APNS_URI))
                .withPathParam("deviceToken",
                        equalTo(URLEncoder.encode(iOsRecipient.getPairingToken().notificationToken(), UTF_8)))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        StepVerifier.create(relayClient.sendNotification(iOsRecipient, messageContainer))
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException
                        && ((WebClientResponseException) throwable).getStatusCode().value() == 500
                        && ((WebClientResponseException) throwable).getResponseBodyAsString().equals("Internal Server Error"))
                .verify();

        wireMockServer.verify(postRequestedFor(urlPathTemplate(APNS_URI))
                .withHeader("Content-Type", equalTo("application/json")));
    }
}
