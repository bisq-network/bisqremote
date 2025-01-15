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

import bisq.remote.notification.recipient.PairingToken;
import bisq.remote.notification.recipient.Recipient;
import bisq.remote.notification.recipient.RecipientFactory;
import bisq.remote.ui.views.scanqr.ScanQrViewController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.function.Consumer;

@FxmlView("AddRecipientDialog.fxml")
@Component
@Slf4j
public class AddRecipientDialogController {
    private Stage stage;
    private final FxControllerAndView<ScanQrViewController, BorderPane> scanQrDialog;
    private final RecipientFactory recipientFactory;
    private Consumer<Recipient> onOk;
    private final StringProperty pairingTokenProperty = new SimpleStringProperty();

    @FXML
    private VBox dialog;
    @FXML
    private TextField pairingTokenTextField;
    @FXML
    private Button scanQrButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    public AddRecipientDialogController(@NotNull final FxControllerAndView<ScanQrViewController, BorderPane> scanQrDialog,
                                        @NotNull final RecipientFactory recipientFactory) {
        this.scanQrDialog = scanQrDialog;
        this.recipientFactory = recipientFactory;
    }

    @FXML
    public void initialize() {
        this.stage = new Stage();
        stage.setScene(new Scene(dialog));
        stage.setTitle("Add Recipient");
        stage.centerOnScreen();
        stage.setResizable(false);

        pairingTokenTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> setPairingToken(newValue));

        scanQrButton.setOnAction(actionEvent -> scanQrDialog.getController().show(
                stage.getOwner(), this::setPairingToken));

        okButton.disableProperty().bind(pairingTokenProperty.isEmpty());
        okButton.setOnAction(actionEvent -> {
            try {
                Recipient recipient = recipientFactory.createFromPairingToken(
                        PairingToken.fromString(pairingTokenProperty.get()));
                onOk.accept(recipient);
                stage.close();
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage());
                pairingTokenTextField.styleProperty().set("-fx-background-color: #ee6664; -fx-text-fill: #ffffff;");
            }
        });

        cancelButton.setOnAction(actionEvent -> stage.close());
    }

    public void show(Window parentWindow, Consumer<Recipient> onOk) {
        this.onOk = onOk;
        if (stage.getOwner() == null) {
            stage.initOwner(parentWindow);
            stage.initModality(Modality.WINDOW_MODAL);
        }
        pairingTokenProperty.set("");
        pairingTokenTextField.textProperty().set("");
        pairingTokenTextField.styleProperty().set("");
        stage.show();
    }

    private void setPairingToken(@NotNull final String pairingToken) {
        try {
            recipientFactory.createFromPairingToken(
                    PairingToken.fromString(pairingToken));
            pairingTokenProperty.set(pairingToken);
            pairingTokenTextField.textProperty().set(pairingToken);
            pairingTokenTextField.styleProperty().set("");
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            pairingTokenTextField.styleProperty().set("-fx-background-color: #ee6664; -fx-text-fill: #ffffff;");
        }
    }
}
