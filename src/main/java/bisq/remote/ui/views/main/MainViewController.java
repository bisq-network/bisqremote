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

import bisq.remote.notification.message.payload.MessagePayload;
import bisq.remote.notification.message.payload.MessagePayloadFactory;
import bisq.remote.notification.recipient.Recipient;
import bisq.remote.ui.views.main.panel.RecipientPanelController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@FxmlView("MainView.fxml")
@Component
public class MainViewController {
    private final MainViewModel mainViewModel;
    private final FxControllerAndView<AddRecipientDialogController, BorderPane> addRecipientDialog;
    private final RecipientPanelController recipientPanelController;

    @FXML
    private Button addRecipientButton;
    @FXML
    private Button removeRecipientButton;
    @FXML
    private ListView<Recipient> recipientListView;
    @FXML
    private AnchorPane recipientPanel;
    @FXML
    private ComboBox<String> payloadComboBox;
    @FXML
    private TextField payloadTextField;
    @FXML
    private Button sendMessageButton;
    @FXML
    private Label sendMessageResultText;

    public MainViewController(final MainViewModel mainViewModel,
                              final FxControllerAndView<AddRecipientDialogController, BorderPane> addRecipientDialog,
                              final RecipientPanelController recipientPanelController) {
        this.mainViewModel = mainViewModel;
        this.addRecipientDialog = addRecipientDialog;
        this.recipientPanelController = recipientPanelController;
    }

    @FXML
    public void initialize() {
        initializeRecipientsPanel();
        initializeMessagePanel();
    }

    private void initializeRecipientsPanel() {
        addRecipientButton.setOnAction(actionEvent ->
                addRecipientDialog.getController().show(((Node) actionEvent.getSource()).getScene().getWindow(),
                        mainViewModel::addRecipient));

        removeRecipientButton.disableProperty().bind(Bindings.isEmpty(
                recipientListView.getSelectionModel().getSelectedItems()));
        removeRecipientButton.setOnAction(actionEvent -> {
            final Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Are you sure you want to remove the selected recipient(s)?",
                    ButtonType.YES, ButtonType.NO);
            alert.setTitle("Remove Recipients");
            alert.setHeaderText(null);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                mainViewModel.removeRecipients(
                        List.copyOf(recipientListView.getSelectionModel().getSelectedItems()));
            }
        });

        recipientListView.setItems(mainViewModel.getRecipients());
        recipientListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                recipientPanelController.populate(recipientListView.getSelectionModel().getSelectedItem()));

        recipientPanel.visibleProperty().bind(Bindings.isNotEmpty(
                recipientListView.getSelectionModel().getSelectedItems()));
    }

    private void initializeMessagePanel() {
        payloadComboBox.setPromptText("Select Payload");
        payloadComboBox.setItems(FXCollections.observableArrayList(mainViewModel.getPayloadSuppliers().keySet()));
        payloadComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.isEmpty()) {
                        mainViewModel.setMessagePayload(newValue);
                    }
                });

        Bindings.bindBidirectional(payloadTextField.textProperty(), mainViewModel.getMessagePayloadProperty(),
                new StringConverter<>() {
                    @Override
                    public String toString(MessagePayload messagePayload) {
                        if (messagePayload == null) {
                            return "";
                        }
                        return messagePayload.asJsonString();
                    }

                    @Override
                    public MessagePayload fromString(String s) {
                        if (s == null) {
                            return null;
                        }
                        try {
                            return new MessagePayloadFactory().create(s);
                        } catch (ParseException | IllegalArgumentException e) {
                            return null;
                        }
                    }
                });

        sendMessageButton.disableProperty().bind(Bindings.or(
                Bindings.isEmpty(recipientListView.getSelectionModel().getSelectedItems()),
                Bindings.isNull(mainViewModel.getMessagePayloadProperty())));
        sendMessageButton.setOnAction(actionEvent -> {
            final Recipient recipient = recipientListView.getSelectionModel().getSelectedItem();
            mainViewModel.sendNotification(recipient);
        });

        sendMessageResultText.textProperty().bind(mainViewModel.getSendMessageResultMessage());
    }
}
