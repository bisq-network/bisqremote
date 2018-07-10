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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Phone phone;
    private Button sendButton;
    private Button webcamButton;
    private Button deleteButton;
    public static Webcam webcam;
    public TextField phoneTextField;
    private boolean listenToPhoneTextFieldChanges;
    private BisqNotificationServer bisqNotificationServer;

    public static void main(String[] args) {
        Webcam.getDiscoveryService().setEnabled(true);
        Webcam.getDiscoveryService().stop();
        webcam = Webcam.getDefault();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        phone = new Phone();
        bisqNotificationServer = new BisqNotificationServer();

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

    private void addUIControls(GridPane gridPane) {

        Integer rowindex = 0;

        Label headerSetupLabel = new Label("SETUP");
        headerSetupLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(headerSetupLabel, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSetupLabel, HPos.LEFT);
        GridPane.setMargin(headerSetupLabel, new Insets(5, 0, 0, 0));

        rowindex++;
        webcamButton = new Button("WEBCAM");
        webcamButton.setDefaultButton(true);
        webcamButton.setOnAction((event) -> {
            this.webcamButton.setDisable(true);
            new ReadQRCode(this, this.phone);
        });
        gridPane.add(webcamButton, 0, rowindex, 2, 1);
        GridPane.setHalignment(webcamButton, HPos.CENTER);

        rowindex++;
        Label phoneTitleLabel = new Label("Bisq Phone ID:");
        gridPane.add(phoneTitleLabel, 0, rowindex, 1, 1);
        GridPane.setHalignment(phoneTitleLabel, HPos.RIGHT);
        phoneTextField = new TextField();
        phoneTextField.setPromptText("copy the string from the email");
        gridPane.add(phoneTextField, 1, rowindex, 1, 1);
        GridPane.setHalignment(phoneTextField, HPos.LEFT);
        if (phone.isInitialized) {
            phoneTextField.setText(phone.phoneID());
        }
        // adding the listener *after* setting the text
        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (listenToPhoneTextFieldChanges) {
                sendConfirmation();
            }
        });
        listenToPhoneTextFieldChanges = true;

        rowindex++;
        webcamButton = new Button("FACTORY RESET");
        webcamButton.setOnAction((event) -> {
            BisqNotification n = new BisqNotification(phone);
            n.notificationType = NotificationTypes.ERASE.name();
            send(n, false);
        });
        gridPane.add(webcamButton, 0, rowindex, 2, 1);
        GridPane.setHalignment(webcamButton, HPos.CENTER);


        rowindex++;
        Label headerSendLabel = new Label("NOTIFY");
        headerSendLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(headerSendLabel, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSendLabel, HPos.LEFT);
        GridPane.setMargin(headerSendLabel, new Insets(35, 0, 0, 0));

        rowindex++;
        Label notificationTypeLabel = new Label("Message Type: ");
        gridPane.add(notificationTypeLabel, 0, rowindex);

        TextField notificationTypeField = new TextField("Test type");
        notificationTypeField.setPrefHeight(40);
        gridPane.add(notificationTypeField, 1, rowindex);

        rowindex++;
        Label titleLabel = new Label("Headline: ");
        gridPane.add(titleLabel, 0, rowindex);

        TextField titleField = new TextField("Notification Title");
        titleField.setPrefHeight(40);
        gridPane.add(titleField, 1, rowindex);

        rowindex++;
        Label messageLabel = new Label("Message text: ");
        gridPane.add(messageLabel, 0, rowindex);

        TextField messageField = new TextField("Notification message");
        messageField.setPrefHeight(40);
        gridPane.add(messageField, 1, rowindex);

        rowindex++;
        Label actionRequiredLabel = new Label("Action Required: ");
        gridPane.add(actionRequiredLabel, 0, rowindex);

        TextField actionRequiredField = new TextField("You need to do somthing!");
        actionRequiredField.setPrefHeight(40);
        gridPane.add(actionRequiredField, 1, rowindex);

        rowindex++;
        javafx.scene.control.CheckBox sound = new  javafx.scene.control.CheckBox("Play sound when in background");
        gridPane.add(sound, 1, rowindex);

        rowindex++;
        sendButton = new Button("Send Notification");
        sendButton.setDefaultButton(true);
        gridPane.add(sendButton, 0, rowindex, 2, 1);
        GridPane.setHalignment(sendButton, HPos.CENTER);
        GridPane.setMargin(sendButton, new Insets(20, 0, 20, 0));

        sendButton.setOnAction(event -> {
            BisqNotification n = new BisqNotification(phone);
            n.notificationType = NotificationTypes.TRADE.name();
            n.title = titleField.getText();
            n.message = messageField.getText();
            n.actionRequired = actionRequiredField.getText();
            send(n, sound.isSelected());
        });
        updateGUI();
    }


    public void sendConfirmation() {

        // check syntax
        boolean sendConfirmationNotification = this.phone.fromString(phoneTextField.getText());
        if (phone.isInitialized) {
            BisqNotification n = new BisqNotification(phone);
            n.notificationType = NotificationTypes.SETUP_CONFIRMATION.name();
            send(n, true);
        }
    }

    private void send(BisqNotification n, Boolean sound) {
        String payload = n.payload();
        if (phone.os == Phone.OS.iOS) {
            bisqNotificationServer.overTor_____sendiOSMessage(phone.notificationToken, payload, sound, true);
        } else if (phone.os == Phone.OS.iOSDev) {
            bisqNotificationServer.overTor_____sendiOSMessage(phone.notificationToken, payload, sound, false);
        } else if (phone.os == Phone.OS.Android) {
            bisqNotificationServer.overTor_____sendAndroidMessage(phone.notificationToken, payload, sound);
        }
    }


    public void updateGUI() {
        switch (phone.os) {
            case iOS:
                sendButton.setDisable(false);
                sendButton.setText("SEND (iPhone)");
                break;
            case iOSDev:
                sendButton.setDisable(false);
                sendButton.setText("SEND (iPhone dev)");
                break;
            case Android:
                sendButton.setDisable(false);
                sendButton.setText("SEND (Android");
                break;
            case undefined:
                sendButton.setText("SEND");
                sendButton.setDisable(true);
                break;
        }

        listenToPhoneTextFieldChanges = false;
        phoneTextField.setText(phone.phoneID());
        listenToPhoneTextFieldChanges = true;
        this.webcamButton.setDisable(false);
    }

}