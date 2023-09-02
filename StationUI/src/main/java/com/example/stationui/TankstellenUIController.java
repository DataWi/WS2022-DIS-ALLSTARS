package com.example.stationui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    public void onButtonCancelClicked(ActionEvent actionEvent) {
        System.exit(0);
    }

}
