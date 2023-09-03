package com.example.stationui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;


public class TankstellenUIController {

    private Label welcomeText;

    private static final String API_URL = "http://127.0.0.1:5151/api/v1/invoices/";

    @FXML
    private Label POSTLabel;

    @FXML
    private Label GETLabel;

    @FXML
    private TextField customerIDField;


    public void onButtonGenerateInvoiceClicked(ActionEvent actionEvent) {

        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/invoice"))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(customerIDField.getText())).build();
            var response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Failed to call REST api: \n" + e.toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void onButtonDownloadPDFClicked(ActionEvent event) {
        String customerID = customerIDField.getText();
        String fileName = "Invoice_" + customerID + ".pdf";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/invoices/" + customerID))
                    .build();

            HttpResponse<java.io.InputStream> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                String contentEncoding = response.headers().firstValue("Content-Encoding").orElse("");
                if (!"base64".equals(contentEncoding)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Unexpected Content-Encoding: " + contentEncoding);
                    alert.showAndWait();
                    return;
                }

                byte[] base64Data = response.body().readAllBytes();
                byte[] decodedBytes = Base64.getDecoder().decode(new String(base64Data));

                String userHome = System.getProperty("user.home");
                String downloadFolderPath = userHome + File.separator + "Downloads";
                String downloadFilePath = downloadFolderPath + File.separator + fileName;

                try (FileOutputStream fileOutputStream = new FileOutputStream(downloadFilePath)) {
                    fileOutputStream.write(decodedBytes);
                }

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("PDF file successfully downloaded.");
                successAlert.showAndWait();

            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText("Error downloading the PDF file. Status code: " + response.statusCode());
                errorAlert.showAndWait();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("An error occurred: \n" + e.toString());
            alert.showAndWait();
        }
    }





    public void onButtonCancelClicked(ActionEvent actionEvent) {
        System.exit(0);
    }

}