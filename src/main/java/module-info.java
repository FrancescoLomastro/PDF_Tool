module org.example {
    requires javafx.controls;
    requires org.apache.pdfbox;
    requires javafx.web;
    requires java.desktop;
    requires javafx.fxml;
    requires jdk.jsobject;
    requires javafx.graphics;


    exports org.example.GUI;
    opens org.example.GUI to javafx.fxml;
    exports org.example.Exceptions;
    opens org.example.Exceptions to javafx.fxml;
    exports org.example.Model;
    opens org.example.Model to javafx.fxml;
    exports org.example.Utility;
    opens org.example.Utility to javafx.fxml;
}