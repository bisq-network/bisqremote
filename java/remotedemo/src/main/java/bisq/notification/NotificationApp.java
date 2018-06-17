package bisq.notification;/*
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

import com.google.common.io.BaseEncoding;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class NotificationApp extends Application {
    private static final String SYM_KEY_ALGO = "AES";
    private static final String SYM_CIPHER = "AES";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Registration Form JavaFX Application");

        // Create the registration form grid pane
        GridPane gridPane = createFormPane();
        // Add UI controls to the registration form grid pane
        addUIControls(gridPane);
        // Create a scene with registration form grid pane as the root node
        Scene scene = new Scene(gridPane, 800, 500);
        // Set the scene in primary stage
        primaryStage.setScene(scene);

        primaryStage.show();
    }


    private GridPane createFormPane() {
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.CENTER);

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

        Label headerSetup1Label = new Label("Setup - create key and show QR code");
        headerSetup1Label.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(headerSetup1Label, 0, rowindex, 2, 1);
        GridPane.setHalignment(headerSetup1Label, HPos.CENTER);
        GridPane.setMargin(headerSetup1Label, new Insets(20, 0, 20, 0));

        rowindex++;
        Label keyTitleLabel = new Label("Symnmetric key: ");
        gridPane.add(keyTitleLabel, 0, rowindex);
        Label keyLabel = new Label("skjdhflksdjhflksdjh");
        gridPane.add(keyLabel, 1, rowindex);

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
        Label headlineLabel = new Label("Headline: ");
        gridPane.add(headlineLabel, 0, rowindex);

        TextField headlineField = new TextField("Test headline");
        headlineField.setPrefHeight(40);
        gridPane.add(headlineField, 1, rowindex);

        rowindex++;
        Label msgLabel = new Label("Message text: ");
        gridPane.add(msgLabel, 0, rowindex);

        TextField msgField = new TextField("Test msg");
        msgField.setPrefHeight(40);
        gridPane.add(msgField, 1, rowindex);

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
            showMsg(typeField.getText(), headlineField.getText(), msgField.getText());
        });
    }

    private void showMsg(String type, String headline, String msg) {
        try {
            // build json...
            String url = "https://www.apple.com";
            String appleKey = "key...";
            String json = getJson(type, headline, msg);
            String hexData = getEncryptedDataAsHex(json);
            String urlParameters = "hexData=" + URLEncoder.encode(hexData, "UTF-8") +
                    "&appleKey=" + URLEncoder.encode(appleKey, "UTF-8");
            String result = executePost(url, urlParameters);
            System.out.print("result = " + result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String getEncryptedDataAsHex(String json) {
        byte[] payload = json.getBytes(Charset.forName("UTF-8"));
        SecretKey secretKey = generateSecretKey(256);
        byte[] encryptedPayload = encrypt(payload, secretKey);
        //byte[] decryptedPayload = decrypt(encryptedPayload, secretKey);

        String hex = BaseEncoding.base16().lowerCase().encode(encryptedPayload);
        System.out.println("hex = " + hex);
        return hex;
    }

    private String getJson(String type, String headline, String msg) {
        JSONObject obj = new JSONObject();
        obj.put("type", type);
        obj.put("headline", headline);
        obj.put("msg", msg);
        String jsonString = obj.toJSONString();
        System.out.println("jsonString = " + jsonString);
        return jsonString;
    }

    private static String executePost(String targetURL, String urlParameters) {
        System.out.println("executePost targetURL = " + targetURL);
        System.out.println("executePost urlParameters = " + urlParameters);
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response  
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String result = response.toString();
            System.out.print("executePost result = " + result);
            return result;
        } catch (IOException e) {
            System.out.print("executePost error = " + e);

            return e.toString();
        } catch (Throwable e) {
            System.out.print("executePost error = " + e);
            e.printStackTrace();
            return e.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public SecretKey generateSecretKey(int bits) {
        try {
            KeyGenerator keyPairGenerator = KeyGenerator.getInstance(SYM_KEY_ALGO);
            keyPairGenerator.init(bits);
            return keyPairGenerator.generateKey();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(byte[] payload, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(SYM_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(payload);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(byte[] encryptedPayload, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(SYM_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedPayload);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}