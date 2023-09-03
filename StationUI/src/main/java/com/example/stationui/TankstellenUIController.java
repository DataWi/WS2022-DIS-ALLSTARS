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

        String fileName = "Invoice " + customerID + ".pdf";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/invoices/" + customerID))
                    .build();

            HttpResponse<java.io.InputStream> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {

                String userHome = System.getProperty("user.home");

                String downloadFolderPath = userHome + File.separator + "Downloads";

                String downloadFilePath = downloadFolderPath + File.separator + fileName;

                try (BufferedInputStream inputStream = new BufferedInputStream(response.body());
                     FileOutputStream fileOutputStream = new FileOutputStream(downloadFilePath)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("PDF-Datei erfolgreich heruntergeladen.");
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText("Fehler beim Herunterladen der PDF-Datei, kein Kunde mit dieser ID gefunden.");
                errorAlert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Fehler beim Aufrufen der REST-API: \n" + e.toString());
            alert.showAndWait();
        }
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        System.exit(0);
    }

}