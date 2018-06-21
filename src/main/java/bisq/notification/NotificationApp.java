package bisq.notification;

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


import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class NotificationApp extends Application {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private BisqToken bisqToken;
    private BisqKey bisqKey;
    private BisqNotification bisqNotification;
    public static Webcam webcam;
    public TextField bundleIdentifierTextField;
    public TextField tokenBase58TextField;

    public static void main(String[] args) {
        Webcam.getDiscoveryService().setEnabled(true);
        Webcam.getDiscoveryService().stop();
        webcam = Webcam.getDefault();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        bisqToken = new BisqToken();
        bisqKey = new BisqKey();
        bisqNotification = new BisqNotification(bisqToken, bisqKey);
        primaryStage.setTitle("Bisq Notification Reference Implementation");

        // Create the registration form grid pane
        GridPane gridPane = createFormPane();
        // Add UI controls to the registration form grid pane
        addUIControls(gridPane);
        // Create a scene with registration form grid pane as the root node
        Scene scene = new Scene(gridPane, 800, 800);
        // Set the scene in primary stage
        primaryStage.setScene(scene);

        primaryStage.show();
    }


    private GridPane createFormPane() {
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.TOP_CENTER);

        // Set a padding of 20px on each side
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        // Set the horizontal gap between columns
        gridPane.setHgap(10);

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // Add Column Constraints

        // columnOneConstraints will be applied to all the nodes placed in column one.
        ColumnConstraints columnOneConstraints = new ColumnConstraints(200, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        // columnTwoConstraints will be applied to all the nodes placed in column two.
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200, 200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        return gridPane;
    }

    public void updateToken() {
        bundleIdentifierTextField.setText(bisqToken.bundleidentifier);
        tokenBase58TextField.setText(bisqToken.asBase58());
    }

    private void addUIControls(GridPane gridPane) {

        Integer rowindex = 0;

        Label headerSetup1Label = new Label("Setup Step 1: The user needs the encryption key in his phone");
        headerSetup1Label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(headerSetup1Label, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSetup1Label, HPos.LEFT);
        GridPane.setMargin(headerSetup1Label, new Insets(5, 0, 0, 0));

        rowindex++;
        Label keyTitleLabel = new Label("Encryption key: " + bisqKey.key());
        gridPane.add(keyTitleLabel, 0, rowindex, 2, 1);
        GridPane.setHalignment(keyTitleLabel, HPos.LEFT);

        rowindex++;
        // QR code
        QR qr = new QR();
        AtomicReference<ImageView> iv = new AtomicReference<>(qr.imageView(
                bisqKey.withMagic(),
                300,
                300,
                Color.BLACK,
                new Color(244, 244, 244)));
        gridPane.add(iv.get(), 0, rowindex, 1, 1);
        GridPane.setHalignment(iv.get(), HPos.RIGHT);

        final Button newKeyButton = new Button("new key (only for new mobile phone)");
        newKeyButton.setOnAction((event) -> {
            bisqKey.newKey();
            keyTitleLabel.setText("Encryption key: " + bisqKey.key());
            iv.set(qr.imageView(
                    bisqKey.withMagic(),
                    300,
                    300,
                    Color.BLACK,
                    new Color(244, 244, 244)));
            gridPane.getChildren().remove(iv);
            gridPane.add(iv.get(), 0, 2, 1, 1);
            GridPane.setHalignment(iv.get(), HPos.RIGHT);
        });

        gridPane.add(newKeyButton, 1, rowindex, 1, 1);
        GridPane.setHalignment(newKeyButton, HPos.CENTER);

        rowindex++;
        Label headerSetup2Label = new Label("Setup Step 2: Bisq needs the Apple Notification Token from the phone");
        headerSetup2Label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(headerSetup2Label, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSetup2Label, HPos.LEFT);
        GridPane.setMargin(headerSetup2Label, new Insets(5, 0, 0, 0));

        // Webcam
        rowindex++;
        final Button webcamButton = new Button("Use the Webcam of your computer");
        webcamButton.setOnAction((event) -> {
            new ReadQRCode(this, bisqToken);
        });

        gridPane.add(webcamButton, 0, rowindex, 2, 1);
        GridPane.setHalignment(webcamButton, HPos.CENTER);

        rowindex++;
        Label bundleIdentifierTitleLabel = new Label("bundle identifier:");
        gridPane.add(bundleIdentifierTitleLabel, 0, rowindex, 1, 1);
        GridPane.setHalignment(bundleIdentifierTitleLabel, HPos.RIGHT);
        bundleIdentifierTextField = new TextField(bisqToken.bundleidentifier);
        bundleIdentifierTextField.setPromptText("Enter the bundle identifier");
        gridPane.add(bundleIdentifierTextField, 1, rowindex, 1, 1);
        GridPane.setHalignment(bundleIdentifierTextField, HPos.LEFT);
        bundleIdentifierTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            bisqToken.bundleidentifier = newValue;
            bisqToken.save();
        });

        rowindex++;
        Label tokenBase58TitleLabel = new Label("as Base58:");
        gridPane.add(tokenBase58TitleLabel, 0, rowindex, 1, 1);
        GridPane.setHalignment(tokenBase58TitleLabel, HPos.RIGHT);
        tokenBase58TextField = new TextField(bisqToken.asBase58());
        tokenBase58TextField.setPromptText("Enter Token in Base58 format");
        gridPane.add(tokenBase58TextField, 1, rowindex, 1, 1);
        GridPane.setHalignment(tokenBase58TextField, HPos.LEFT);
        tokenBase58TextField.textProperty().addListener((observable, oldValue, newValue) -> {
            bisqToken.apsTokenBase58 = newValue;
            bisqToken.save();
        });

        rowindex++;
        Label headerSendLabel = new Label("Usage: send message");
        headerSendLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(headerSendLabel, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSendLabel, HPos.LEFT);
        GridPane.setMargin(headerSendLabel, new Insets(5, 0, 0, 0));

        rowindex++;
        Label notificationTypeLabel = new Label("Message Type: ");
        gridPane.add(notificationTypeLabel, 0, rowindex);

        TextField notificationTypeField = new TextField("Test type");
        notificationTypeField.setPrefHeight(40);
        gridPane.add(notificationTypeField, 1, rowindex);

        rowindex++;
        Label titleLabel = new Label("Headline: ");
        gridPane.add(titleLabel, 0, rowindex);

        TextField titleField = new TextField("Test title");
        titleField.setPrefHeight(40);
        gridPane.add(titleField, 1, rowindex);

        rowindex++;
        Label messageLabel = new Label("Message text: ");
        gridPane.add(messageLabel, 0, rowindex);

        TextField messageField = new TextField("Test message");
        messageField.setPrefHeight(40);
        gridPane.add(messageField, 1, rowindex);

        rowindex++;
        Label actionRequiredLabel = new Label("Action Required: ");
        gridPane.add(actionRequiredLabel, 0, rowindex);

        TextField actionRequiredField = new TextField("Test action");
        actionRequiredField.setPrefHeight(40);
        gridPane.add(actionRequiredField, 1, rowindex);

        rowindex++;
        Button sendButton = new Button("Send Notification");
        sendButton.setDefaultButton(true);
        gridPane.add(sendButton, 0, rowindex, 2, 1);
        GridPane.setHalignment(sendButton, HPos.CENTER);
        GridPane.setMargin(sendButton, new Insets(20, 0, 20, 0));

        sendButton.setOnAction(event -> {
            // send to apple server
            bisqNotification.notificationType = notificationTypeField.getText();
            bisqNotification.title = titleField.getText();
            bisqNotification.message = messageField.getText();
            bisqNotification.actionRequired = actionRequiredField.getText();
            bisqNotification.prepareToSend(true);
        });

        rowindex++;
        Button sendEncryptedButtonDevelopment = new Button("Send to iOS App running from Xcode (Apple development APNS Server)");
        gridPane.add(sendEncryptedButtonDevelopment, 0, rowindex, 2, 1);
        GridPane.setHalignment(sendEncryptedButtonDevelopment, HPos.CENTER);
        GridPane.setMargin(sendEncryptedButtonDevelopment, new Insets(0, 0, 20, 0));

        sendEncryptedButtonDevelopment.setOnAction(event -> {
            // send to apple server
            bisqNotification.notificationType = notificationTypeField.getText();
            bisqNotification.title = titleField.getText();
            bisqNotification.message = messageField.getText();
            bisqNotification.actionRequired = actionRequiredField.getText();
            bisqNotification.prepareToSend(false);
        });
    }
}