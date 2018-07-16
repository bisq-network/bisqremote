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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationApp extends Application {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private Phone phone;
    private Stage primaryStage;
    private Button webcamButton;
    private Button eraseButton;
    private Button testButton;
    private QR qr;
    public static Webcam webcam;
    public TextField phoneTextField;
    private int testCounter = 1;
    private boolean listenToPhoneTextFieldChanges;
    private BisqNotificationServer bisqNotificationServer;
    private CheckBox soundCheckBox;

    public static void main(String[] args) {
        Webcam.getDiscoveryService().setEnabled(true);
        Webcam.getDiscoveryService().stop();
        webcam = Webcam.getDefault();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage_) {
        primaryStage = primaryStage_;
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

        webcamButton.setDisable(false);
        if (phone.isInitialized) {
            testButton.setDisable(false);
            eraseButton.setDisable(false);
        } else {
            testButton.setDisable(true);
            eraseButton.setDisable(true);
        }
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
        Label label;
        CheckBox checkbox;
        Integer rowindex = 0;

        label = new Label("Notification Setup");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(label, 0, rowindex, 2, 1);
        GridPane.setHalignment(label, HPos.LEFT);
        GridPane.setMargin(label, new Insets(5, 0, 0, 0));

        rowindex++;
        label = new Label("Use webcam:");
        gridPane.add(label, 0, rowindex, 1, 1);
        GridPane.setHalignment(label, HPos.RIGHT);
        webcamButton = new Button("SCAN QR Code");
        webcamButton.setOnAction((event) -> {
//            new ReadQRCode(this, this.phone);
            qr = new QR(this, primaryStage, webcam);
        });
        gridPane.add(webcamButton, 1, rowindex, 1, 1);
        GridPane.setHalignment(webcamButton, HPos.LEFT);

        rowindex++;
        label = new Label("Pairing token:");
        gridPane.add(label, 0, rowindex, 1, 1);
        GridPane.setHalignment(label, HPos.RIGHT);
        phoneTextField = new TextField();
        phoneTextField.setPromptText("(optional) paste pairing token from email");
        gridPane.add(phoneTextField, 1, rowindex, 1, 1);
        GridPane.setHalignment(phoneTextField, HPos.LEFT);
        if (phone.isInitialized) {
            phoneTextField.setText(phone.pairingToken());
        }
        // adding the listener *after* setting the text
        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (listenToPhoneTextFieldChanges) {
                Boolean ok = phone.fromString(phoneTextField.getText());
                if (ok) {
                    sendConfirmation();
                    testButton.setDisable(false);
                    eraseButton.setDisable(false);
                } else {
                    testButton.setDisable(true);
                    eraseButton.setDisable(true);
                }
            }
        });
        listenToPhoneTextFieldChanges = true;

        rowindex++;
        label = new Label("Play sound when in background:");
        gridPane.add(label, 0, rowindex);
        soundCheckBox = new  CheckBox("");
        gridPane.add(soundCheckBox, 1, rowindex);


        rowindex++;
        label = new Label("Send test Notification:");
        gridPane.add(label, 0, rowindex, 1, 1);
        GridPane.setHalignment(label, HPos.RIGHT);
        testButton = new Button("TEST");
        testButton.setOnAction((event) -> {
            if (phone.isInitialized) {
                BisqNotification n = new BisqNotification(phone);
                n.type = NotificationTypes.TRADE.name();
                n.title = "Bisq test notification "+testCounter;
                testCounter += 1;
                n.message = "message text";
                send(n, soundCheckBox.isSelected(), isContentAvailable());
            }
        });
        gridPane.add(testButton, 1, rowindex, 1, 1);
        GridPane.setHalignment(testButton, HPos.LEFT);

        rowindex++;
        rowindex++;
        label = new Label("Erase Phone:");
        gridPane.add(label, 0, rowindex, 1, 1);
        GridPane.setHalignment(label, HPos.RIGHT);
        eraseButton = new Button("ERASE");
        eraseButton.setStyle("-fx-background-color: #ee6664;-fx-text-fill: #ffffff;"); // color from airbnb logo
        eraseButton.setDisable(true);
        eraseButton.setOnAction((event) -> {
            if (phone.isInitialized) {
                BisqNotification n = new BisqNotification(phone);
                eraseButton.setDisable(true);
                testButton.setDisable(true);
                n.type = NotificationTypes.ERASE.name();
                send(n, false, isContentAvailable());
                listenToPhoneTextFieldChanges = false;
                phoneTextField.setText("");
                listenToPhoneTextFieldChanges = true;
            }
        });
        gridPane.add(eraseButton, 1, rowindex, 1, 1);
        GridPane.setHalignment(eraseButton, HPos.LEFT);



        rowindex++;
        label = new Label("Notification Settings");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(label, 0, rowindex, 2, 1);
        GridPane.setHalignment(label, HPos.LEFT);
        GridPane.setMargin(label, new Insets(35, 0, 0, 0));

        rowindex++;
        label = new Label("Trades: ");
        gridPane.add(label, 0, rowindex);
        checkbox = new  CheckBox("");
        gridPane.add(checkbox, 1, rowindex);

        rowindex++;
        label = new Label("Market alerts: ");
        gridPane.add(label, 0, rowindex);
        checkbox = new  CheckBox("");
        gridPane.add(checkbox, 1, rowindex);

        rowindex++;
        label = new Label("Price alerts: ");
        gridPane.add(label, 0, rowindex);
        checkbox = new  CheckBox("");
        gridPane.add(checkbox, 1, rowindex);

        updateGUI();
    }

    private boolean isContentAvailable() {
        // phone descriptors
        /*
        iPod Touch 5
        iPod Touch 6
        iPhone 4
        iPhone 4s
        iPhone 5
        iPhone 5c
        iPhone 5s
        iPhone 6
        iPhone 6 Plus
        iPhone 6s
        iPhone 6s Plus
        iPhone 7
        iPhone 7 Plus
        iPhone SE
        iPhone 8
        iPhone 8 Plus
        iPhone X
        iPad 2
        iPad 3
        iPad 4
        iPad Air
        iPad Air 2
        iPad 5
        iPad 6
        iPad Mini
        iPad Mini 2
        iPad Mini 3
        iPad Mini 4
        iPad Pro 9.7 Inch
        iPad Pro 12.9 Inch
        iPad Pro 12.9 Inch 2. Generation
        iPad Pro 10.5 Inch
        */
        if (phone.descriptor != null) {

            String[] tokens = phone.descriptor .split(" ");
            if (tokens.length >= 1) {
                String model = tokens[0];
                if ((model.equals("iPhone"))) {
                    String versionString = tokens[1];
                    versionString = versionString.substring(0, 1);
                    try {
                        int version = Integer.parseInt(versionString);
                        // iPhone 6 does not support isContentAvailable, iPhone 7 does.
                        // We don't know for other versions, but lets assume all below iPhone 7 are failing.
                        // SE we don't know as well
                        return version > 6;
                    } catch (Throwable ignore) {
                    }
                } else {
                    return (model.equals("iPad")) && tokens[1].equals("Pro");
                }
            }
        }
        return false;
    }

    public void sendConfirmation() {
        if (phone.isInitialized) {
            BisqNotification n = new BisqNotification(phone);
            n.type = NotificationTypes.SETUP_CONFIRMATION.name();
            send(n, true, isContentAvailable());
        }
    }

    private void send(BisqNotification n, Boolean sound, Boolean contentAvailable) {
        String payload = n.payload();
        if (phone.os == Phone.OS.iOS) {
            bisqNotificationServer.overTor_____sendiOSMessage(phone.notificationToken, payload, sound, true, contentAvailable);
        } else if (phone.os == Phone.OS.iOSDev) {
            bisqNotificationServer.overTor_____sendiOSMessage(phone.notificationToken, payload, sound, false, contentAvailable);
        } else if (phone.os == Phone.OS.Android) {
            bisqNotificationServer.overTor_____sendAndroidMessage(phone.notificationToken, payload, sound);
        }
    }
    public void qrString(String s) {
        qr.close();
        if (phone.fromString(s)) { sendConfirmation(); }
        updateGUI();
    }

    public void updateGUI() {
        switch (phone.os) {
            case iOS:
            case iOSDev:
            case Android:
                testButton.setDisable(false);
                eraseButton.setDisable(false);
                break;
            case undefined:
                testButton.setDisable(true);
                eraseButton.setDisable(true);
                break;
        }

        listenToPhoneTextFieldChanges = false;
        phoneTextField.setText(phone.pairingToken());
        listenToPhoneTextFieldChanges = true;
        this.webcamButton.setDisable(false);
        if (!phone.isInitialized) {
            this.testButton.setDisable(true);
            this.eraseButton.setDisable(true);
        }

    }

}