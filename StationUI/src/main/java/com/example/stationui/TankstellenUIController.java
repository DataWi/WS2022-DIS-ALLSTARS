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
                     .uri(URI.create("http://localhost:8080/api/v1/invoices"))
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
        // Holen Sie sich den Kunden-ID-Wert aus dem Textfeld
        String customerID = customerIDField.getText();

        // Hier wird der Dateiname basierend auf der Kunden-ID erstellt
        String fileName = "Invoice_" + customerID + ".pdf";  // Beispiel-Dateiname

        try {
            // Erstellen Sie eine HTTP-Anfrage, um die PDF-Datei herunterzuladen
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + customerID + "/download"))
                    .build();

            // Senden Sie die Anfrage und erhalten Sie die Antwort
            HttpResponse<java.io.InputStream> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Überprüfen Sie, ob die Anfrage erfolgreich war (Statuscode 200)
            if (response.statusCode() == 200) {
                // Pfad, in dem die heruntergeladene PDF-Datei gespeichert wird
                // Holen Sie sich den Benutzerverzeichnis-Pfad
                String userHome = System.getProperty("user.home");

            // Pfad zum "Downloads"-Ordner hinzufügen
                String downloadFolderPath = userHome + File.separator + "Downloads";

            // Erstellen Sie den vollständigen Pfad zum Speichern der heruntergeladenen PDF-Datei
                String downloadFilePath = downloadFolderPath + File.separator + fileName;

                // Schreiben Sie die heruntergeladene PDF-Datei in das Dateisystem
                try (BufferedInputStream inputStream = new BufferedInputStream(response.body());
                     FileOutputStream fileOutputStream = new FileOutputStream(downloadFilePath)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }

                // Zeigen Sie eine Erfolgsmeldung an
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("PDF-Datei erfolgreich heruntergeladen.");
                successAlert.showAndWait();
            } else {
                // Zeigen Sie eine Fehlermeldung an, wenn die Anfrage nicht erfolgreich war
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText("Fehler beim Herunterladen der PDF-Datei.");
                errorAlert.showAndWait();
            }
        } catch (Exception e) {
            // Zeigen Sie eine Fehlermeldung an, wenn ein Fehler auftritt
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Fehler beim Aufrufen der REST-API: \n" + e.toString());
            alert.showAndWait();
        }
    }

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        System.exit(0);
    }

}
