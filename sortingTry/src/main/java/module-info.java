module com.example.sortingtry {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.example.sortingtry to javafx.fxml;
    exports com.example.sortingtry;
}