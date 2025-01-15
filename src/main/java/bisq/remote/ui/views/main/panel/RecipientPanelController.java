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

package bisq.remote.ui.views.main.panel;

import bisq.remote.notification.message.MessageContainer;
import bisq.remote.notification.message.MessageEncryption;
import bisq.remote.notification.message.payload.EraseMessagePayload;
import bisq.remote.notification.message.payload.SetupConfirmationPayload;
import bisq.remote.notification.recipient.Recipient;
import bisq.remote.notification.relay.RelayClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@FxmlView("RecipientPanel.fxml")
@Component
@Slf4j
public class RecipientPanelController {
    private final MessageEncryption messageEncryption;
    private final RelayClient relayClient;
    private Recipient recipient;

    @FXML
    public Button pairButton;
    @FXML
    public Button eraseButton;
    private Window parentWindow;
    @FXML
    private Label typeLabel;
    @FXML
    private Label labelLabel;
    @FXML
    private TextField pairingTokenTextField;

    public RecipientPanelController(@NotNull final MessageEncryption messageEncryption,
                                    @NotNull final RelayClient relayClient) {
        this.messageEncryption = messageEncryption;
        this.relayClient = relayClient;
    }

    @FXML
    public void initialize() {
        pairButton.setOnAction(actionEvent -> {
            final MessageContainer messageContainer = new MessageContainer(
                    recipient.getMessageMagic(), new SetupConfirmationPayload(), messageEncryption);
            relayClient.sendNotification(recipient, messageContainer)
                    .doOnSubscribe(subscription -> log.info("Sending setup confirmation notification: {}", messageContainer))
                    .onErrorComplete(throwable -> {
                        log.error(throwable.getMessage());
                        return true;
                    }).block();
            recipient.setInitialized();
        });

        eraseButton.setOnAction(actionEvent -> {
            final MessageContainer messageContainer = new MessageContainer(
                    recipient.getMessageMagic(), new EraseMessagePayload(), messageEncryption);
            relayClient.sendNotification(recipient, messageContainer)
                    .doOnSubscribe(subscription -> log.info("Sending erase message notification: {}", messageContainer))
                    .onErrorComplete(throwable -> {
                        log.error(throwable.getMessage());
                        return true;
                    }).block();
            recipient.setUninitialized();
        });
    }

    public void setParentWindow(@NotNull final Window parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void populate(final Recipient recipient) {
        if (recipient == null) {
            return;
        }
        this.recipient = recipient;
        messageEncryption.setKey(recipient.getPairingToken().key());
        typeLabel.setText(recipient.getClientMagic());
        labelLabel.setText(recipient.getPairingToken().descriptor());
        pairingTokenTextField.setText(recipient.getPairingToken().asString());
    }
}
