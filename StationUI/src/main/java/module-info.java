module com.example.stationui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.stationui to javafx.fxml;
    exports com.example.stationui;
}