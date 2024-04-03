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

package bisq.remote.ui.views.scanqr;

import bisq.remote.image.capture.ImageCaptureService;
import bisq.remote.image.processor.QrCodeImageProcessor;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@FxmlView("ScanQrView.fxml")
@Component
@Slf4j
public class ScanQrViewController {
    private final ScanQrViewModel viewModel;
    private final ImageCaptureService imageCaptureService;
    private final QrCodeImageProcessor qrCodeImageProcessor;

    private Stage stage;
    private Consumer<String> onScanned;
    private boolean isClosing;

    @FXML private BorderPane root;

    @FXML private ComboBox<WebcamListItem> webcamComboBox;

    @FXML private FlowPane webcamPane;

    @FXML private Label webcamStatusLabel;

    @FXML private ImageView webcamImageView;

    @FXML public FlowPane bottomPane;

    @FXML public Button closeButton;

    public ScanQrViewController(ScanQrViewModel viewModel, ImageCaptureService imageCaptureService, QrCodeImageProcessor qrCodeImageProcessor) {
        this.viewModel = viewModel;
        this.imageCaptureService = imageCaptureService;
        this.qrCodeImageProcessor = qrCodeImageProcessor;
    }

    @FXML
    public void initialize() {
        initializeStage();

        initializeWebcamComboBox();

        webcamImageView.imageProperty().bind(viewModel.getWebcamImage());

        initializeWebcamStatusLabel();

        imageCaptureService
                .valueProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (isClosing || newValue == null) {
                                return;
                            }
                            qrCodeImageProcessor.process(newValue).ifPresent(result -> {
                                close();
                                onScanned.accept(result);
                            });
                        });

        closeButton.setOnAction(actionEvent -> close());
    }

    private void initializeStage() {
        this.stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Scan QR Code");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(event -> cleanup());
        stage.widthProperty()
                .addListener(
                        (observable, oldValue, newValue) -> Platform.runLater(this::updateView));
        stage.heightProperty()
                .addListener(
                        (observable, oldValue, newValue) -> Platform.runLater(this::updateView));
    }

    private void initializeWebcamComboBox() {
        webcamComboBox.setPromptText("Choose Camera");
        webcamComboBox
                .disableProperty()
                .bind(
                        Bindings.createBooleanBinding(
                                () -> viewModel.getAvailableWebcams().isEmpty(),
                                viewModel.getAvailableWebcams()));
        webcamComboBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                viewModel.clearWebcamImage();
                                startWebcam(newValue);
                            }
                        });
    }

    private void initializeWebcamStatusLabel() {
        imageCaptureService
                .stateProperty()
                .addListener(
                        (observable, oldState, newState) -> {
                            switch (newState) {
                                case SCHEDULED -> webcamStatusLabel.setText("Waiting...");
                                case FAILED -> {
                                    String exception;
                                    if (imageCaptureService.getException().getCause() != null) {
                                        exception =
                                                String.valueOf(
                                                        imageCaptureService.getException().getCause());
                                    } else {
                                        exception = String.valueOf(imageCaptureService.getException());
                                    }
                                    log.error(exception);
                                    if (exception.contains("Failed to get image")) {
                                        webcamStatusLabel.setText(
                                                "Failed to get image from webcam. Is it"
                                                        + " disconnected?");
                                    } else {
                                        webcamStatusLabel.setText("Failed to load webcam");
                                    }
                                }
                                case CANCELLED -> webcamStatusLabel.setText("Stopped");
                                default -> webcamStatusLabel.setText("");
                            }
                            Platform.runLater(this::updateView);
                        });
    }

    public void show(Window parentWindow, Consumer<String> onScanned) {
        this.onScanned = onScanned;
        isClosing = false;
        try {
            stage.initOwner(parentWindow);
        } catch (IllegalStateException ignored) {
            // Cannot set owner once stage has been set visible
        }
        viewModel.discoverAvailableWebcams();
        webcamComboBox.setItems(viewModel.getAvailableWebcams());
        if (viewModel.getSelectedWebcam().getValue() != null) {
            webcamComboBox.getSelectionModel().select(viewModel.getSelectedWebcam().getValue());
        }
        stage.show();

        // Must set the minimum width and height of the stage after showing it,
        // otherwise it will be ignored
        stage.setMinHeight(400);
        stage.setMinWidth(400);
    }

    public void cleanup() {
        isClosing = true;
        Platform.runLater(
                () -> {
                    imageCaptureService.cancel();
                    imageCaptureService.reset();
                });
    }

    public void close() {
        cleanup();
        stage.close();
    }

    private void updateView() {
        double width = webcamPane.getWidth();
        double height = webcamPane.getHeight();
        if (imageCaptureService.isRunning()) {
            // The height is offset by the bottom pane height in order to ensure the bottom pane is
            // always visible.
            // This may be a hack, but I have yet to find another working solution.
            webcamImageView.setFitWidth(width);
            webcamImageView.setFitHeight(height - bottomPane.getHeight());
            webcamImageView.prefWidth(height);
            webcamImageView.prefHeight(width - bottomPane.getHeight());
        } else {
            webcamImageView.setFitWidth(0);
            webcamImageView.setFitHeight(0);
            webcamImageView.prefWidth(0);
            webcamImageView.prefHeight(0);
        }
    }

    private void startWebcam(WebcamListItem newValue) {
        Platform.runLater(
                () -> {
                    imageCaptureService.cancel();
                    imageCaptureService.setWebcam(newValue.webcam());
                    viewModel.setSelectedWebcam(newValue);
                    imageCaptureService.restart();
                    updateView();
                });
    }
}
