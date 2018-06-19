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


import com.github.sarxos.webcam.Webcam;

import java.awt.Color;

public class NotificationApp extends Application {

    private BisqNotification bisqNotification = new BisqNotification();
    public static Webcam webcam;
    public Label tokenBase58Label;
    public Label tokenHexLabel;

    public static void main(String[] args) {
        Webcam.getDiscoveryService().setEnabled(true);
        Webcam.getDiscoveryService().stop();
        webcam = Webcam.getDefault();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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

        Label headerSetup1Label = new Label("Setup Step 1: The user needs to scan the QR code with his phone");
        headerSetup1Label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(headerSetup1Label, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSetup1Label, HPos.LEFT);
        GridPane.setMargin(headerSetup1Label, new Insets(5, 0, 0, 0));

        rowindex++;
        Label keyTitleLabel = new Label("Symmetric key: "+bisqNotification.key());
        System.out.println("Symmetric key: "+bisqNotification.key());
        gridPane.add(keyTitleLabel, 0, rowindex, 2, 1);
        GridPane.setHalignment(keyTitleLabel, HPos.LEFT);

        rowindex++;
        // QR code
        QR qr = new QR();
        ImageView iv = qr.imageView(
                bisqNotification.key(),
                300,
                300,
                Color.BLACK,
                new Color(244, 244, 244));
        gridPane.add(iv, 0, rowindex, 2, 1);
        GridPane.setHalignment(iv, HPos.CENTER);

        rowindex++;
        Label headerSetup2Label = new Label("Setup Step 2: Bisq needs to read the Notification token from the phone");
        headerSetup2Label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gridPane.add(headerSetup2Label, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSetup2Label, HPos.LEFT);
        GridPane.setMargin(headerSetup2Label, new Insets(5, 0, 0, 0));

        // Webcam
        rowindex++;
        final Button webcamButton = new Button("Open Webcam");

        webcamButton.setOnAction((event) -> {
            System.out.println("WebCam button");
            new WebcamQRCodeExample();
        });

        gridPane.add(webcamButton, 0, rowindex, 1, 1);
        GridPane.setHalignment(webcamButton, HPos.CENTER);

        final Button refreshButton = new Button("refresh");
        refreshButton.setOnAction((event) -> {
            tokenBase58Label.setText(Token.getInstance().apsToken);
            tokenHexLabel.setText(Token.getInstance().asHex());
        });
        gridPane.add(refreshButton, 1, rowindex, 1, 1);
        GridPane.setHalignment(refreshButton, HPos.LEFT);

        rowindex++;
        Label tokenBase58TitleLabel = new Label("as Base58:");
        gridPane.add(tokenBase58TitleLabel, 0, rowindex, 1, 1);
        GridPane.setHalignment(tokenBase58TitleLabel, HPos.RIGHT);
        tokenBase58Label = new Label();
        gridPane.add(tokenBase58Label, 1, rowindex, 1, 1);
        GridPane.setHalignment(tokenBase58Label, HPos.LEFT);

        rowindex++;
        Label tokenHexTitleLabel = new Label("as Hex:");
        gridPane.add(tokenHexTitleLabel, 0, rowindex, 1, 1);
        GridPane.setHalignment(tokenHexTitleLabel, HPos.RIGHT);
        tokenHexLabel = new Label();
        gridPane.add(tokenHexLabel, 1, rowindex, 1, 1);
        GridPane.setHalignment(tokenHexLabel, HPos.LEFT);

        rowindex++;
        Label headerSendLabel = new Label("Send message");
        headerSendLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(headerSendLabel, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSendLabel, HPos.CENTER);
        GridPane.setMargin(headerSendLabel, new Insets(20, 0, 20, 0));

        rowindex++;
        Label typeLabel = new Label("Message Type: ");
        gridPane.add(typeLabel, 0, rowindex);

        TextField typeField = new TextField("Test type");
        typeField.setPrefHeight(40);
        gridPane.add(typeField, 1, rowindex);

        rowindex++;
        Label titleLabel = new Label("Headline: ");
        gridPane.add(titleLabel, 0, rowindex);

        TextField titleField = new TextField("Test headline");
        titleField.setPrefHeight(40);
        gridPane.add(titleField, 1, rowindex);

        rowindex++;
        Label messageLabel = new Label("Message text: ");
        gridPane.add(messageLabel, 0, rowindex);

        TextField messageField = new TextField("Test msg");
        messageField.setPrefHeight(40);
        gridPane.add(messageField, 1, rowindex);

        rowindex++;
        Button sendButton = new Button("Send");
        sendButton.setPrefHeight(40);
        sendButton.setDefaultButton(true);
        sendButton.setPrefWidth(100);
        gridPane.add(sendButton, 0, rowindex, 2, 1);
        GridPane.setHalignment(sendButton, HPos.CENTER);
        GridPane.setMargin(sendButton, new Insets(20, 0, 20, 0));

        sendButton.setOnAction(event -> {
            // send to apple server
            sendNotification(typeField.getText(), titleField.getText(), messageField.getText());
        });
    }

    private void sendNotification(String type, String title, String message) {
        try {
            BisqNotifcationObject o = new BisqNotifcationObject();
            o.notificationType = type;
            o.title = title;
            o.message = message;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}