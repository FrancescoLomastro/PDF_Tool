module org.example {
    requires javafx.controls;
    requires org.apache.pdfbox;
    requires javafx.web;
    requires java.desktop;
    requires javafx.fxml;
    requires jdk.jsobject;
    requires javafx.graphics;

    opens org.example to javafx.fxml;
    exports org.example;
    exports org.example.GUI;
    opens org.example.GUI to javafx.fxml;
}