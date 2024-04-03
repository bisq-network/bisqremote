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

package bisq.remote.ui.views.main;

import bisq.remote.AppData;
import bisq.remote.notification.message.MessageContainer;
import bisq.remote.notification.message.MessageEncryption;
import bisq.remote.notification.message.payload.*;
import bisq.remote.notification.recipient.Recipient;
import bisq.remote.notification.relay.RelayClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static bisq.remote.notification.message.payload.DisputeMessagePayload.DISPUTE;
import static bisq.remote.notification.message.payload.MarketMessagePayload.MARKET;
import static bisq.remote.notification.message.payload.OfferMessagePayload.OFFER;
import static bisq.remote.notification.message.payload.PriceAlertMessagePayload.PRICE;
import static bisq.remote.notification.message.payload.TradeMessagePayload.TRADE;

@Slf4j
@Component
public class MainViewModel {
    private final RelayClient relayClient;
    private final MessageEncryption messageEncryption;

    @Getter
    private final ObservableList<Recipient> recipients = FXCollections.observableArrayList();
    @Getter
    private final SimpleObjectProperty<MessagePayload> messagePayloadProperty = new SimpleObjectProperty<>();
    @Getter
    private final StringProperty sendMessageResultMessage = new SimpleStringProperty();
    @Getter
    private Map<String, Supplier<MessagePayload>> payloadSuppliers;

    public MainViewModel(RelayClient relayClient, MessageEncryption messageEncryption) {
        this.relayClient = relayClient;
        this.messageEncryption = messageEncryption;

        recipients.addAll(AppData.getInstance().getRecipients());

        initializePayloadSuppliers();
    }

    private void initializePayloadSuppliers() {
        final String txId = UUID.randomUUID().toString().split("-")[0];
        payloadSuppliers = Map.of(
                DISPUTE, () -> new DisputeMessagePayload(new MessagePayloadFactory.MessagePayloadBuilder()
                        .title("Dispute message")
                        .message("You received a dispute message for trade with ID " + txId)
                        .txId(txId)
                        .actionRequired("Please contact the arbitrator")),
                MARKET, () -> new MarketMessagePayload(new MessagePayloadFactory.MessagePayloadBuilder()
                        .title("New offer")
                        .message("A new offer with price 36000 USD (1% above market price)" +
                                " and payment method Zelle was published to" +
                                " the Bisq offerbook.\nThe offer ID is " + txId)
                        .txId(txId)),
                OFFER, () -> new OfferMessagePayload(new MessagePayloadFactory.MessagePayloadBuilder()
                        .title("Offer taken")
                        .message("Your offer with ID " + txId + " was taken")
                        .txId(txId)),
                PRICE, () -> new PriceAlertMessagePayload(new MessagePayloadFactory.MessagePayloadBuilder()
                        .title("Price alert for United States Dollar")
                        .message("Your price alert got triggered. The current" +
                                " United States Dollar price is 35351.08 BTC/USD")),
                TRADE, () -> new TradeMessagePayload(new MessagePayloadFactory.MessagePayloadBuilder()
                        .title("Trade confirmed")
                        .message("The trade with ID " + txId + " is confirmed.")
                        .txId(txId))
        );
    }

    void addRecipient(@NotNull final Recipient recipient) {
        Objects.requireNonNull(recipient, "Recipient must not be null");
        this.recipients.add(recipient);
        AppData.getInstance().addRecipient(recipient);
    }

    void removeRecipients(@NotNull final List<Recipient> recipients) {
        Objects.requireNonNull(recipients, "Recipients must not be null");
        this.recipients.removeAll(recipients);
        AppData.getInstance().removeRecipients(recipients);
    }

    void setMessagePayload(@NotNull final String payloadType) {
        final MessagePayload messagePayload = payloadSuppliers.get(payloadType).get();
        messagePayloadProperty.setValue(messagePayload);
        sendMessageResultMessage.setValue("");
    }

    void sendNotification(@NotNull final Recipient recipient) {
        sendMessageResultMessage.setValue("");
        messageEncryption.setKey(recipient.getPairingToken().key());

        MessageContainer messageContainer = new MessageContainer(
                recipient.getMessageMagic(),
                messagePayloadProperty.getValue(),
                messageEncryption
        );

        log.info("Sending message to {}: {}", recipient, messageContainer);

        try {
            relayClient.sendNotification(recipient, messageContainer)
                    .subscribe(
                            result -> Platform.runLater(() -> {
                                log.info("Message accepted by relay: {}", result);
                                sendMessageResultMessage.set("Message sent: " + result);
                            }),
                            throwable -> {
                                final String message = "Failed to send message: " + throwable.getMessage();
                                log.error(message);
                                Platform.runLater(() -> sendMessageResultMessage.set(message));
                            }
                    );
        } catch (Exception e) {
            final String message = "Unable to send message: " + e.getMessage();
            log.error(message);
            Platform.runLater(() -> sendMessageResultMessage.set(message));
        }
    }
}
