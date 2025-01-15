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
import bisq.remote.notification.recipient.Recipient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static bisq.remote.notification.recipient.AndroidRecipient.ANDROID;
import static bisq.remote.notification.recipient.IosDevRecipient.IOS_DEV;
import static bisq.remote.notification.recipient.IosRecipient.IOS;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class RelayClient {
    static final String APNS_URI = "/v1/apns/device/{deviceToken}";
    static final String FCM_URI = "/v1/fcm/device/{deviceToken}";

    private final WebClient webClient;

    RelayClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<RelayResult> sendNotification(
            @NotNull final Recipient recipient,
            @NotNull final MessageContainer messageContainer) {
        return switch (recipient.getClientMagic()) {
            case ANDROID -> sendFcmNotification(recipient.getPairingToken().notificationToken(), messageContainer);
            case IOS, IOS_DEV ->
                    sendApnsNotification(recipient.getPairingToken().notificationToken(), messageContainer);
            default ->
                    throw new IllegalArgumentException(String.format(
                            "Unsupported recipient type: %s", recipient.getClientMagic()));
        };
    }

    private Mono<RelayResult> sendApnsNotification(
            @NotNull final String deviceToken,
            @NotNull final MessageContainer messageContainer) {
        return sendNotification(APNS_URI, deviceToken, messageContainer);
    }

    private Mono<RelayResult> sendFcmNotification(
            @NotNull final String deviceToken,
            @NotNull final MessageContainer messageContainer) {
        return sendNotification(FCM_URI, deviceToken, messageContainer);
    }

    private Mono<RelayResult> sendNotification(
            @NotNull final String uri,
            @NotNull final String deviceToken,
            @NotNull final MessageContainer messageContainer) {

        return webClient.post()
                .uri(uri, deviceToken)
                .body(Mono.just(new RelayMessage(
                        messageContainer.encryptedContent(), true)), RelayMessage.class)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, response -> Mono.empty())
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            final HttpStatus status = (HttpStatus) response.statusCode();
                            return Mono.error(new WebClientResponseException(
                                    "Client error [" + response.statusCode() + "] - " + body,
                                    status.value(),
                                    status.getReasonPhrase(),
                                    response.headers().asHttpHeaders(),
                                    body.getBytes(UTF_8),
                                    UTF_8));
                        }))
                .onStatus(HttpStatusCode::is5xxServerError, response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            final HttpStatus status = (HttpStatus) response.statusCode();
                            return Mono.error(new WebClientResponseException(
                                    "Server error [" + response.statusCode() + "] - " + body,
                                    status.value(),
                                    status.getReasonPhrase(),
                                    response.headers().asHttpHeaders(),
                                    body.getBytes(UTF_8),
                                    UTF_8));
                        }))
                .bodyToMono(RelayResult.class);
    }
}
