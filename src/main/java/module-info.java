module org.example.demofx {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.swing;
    exports remote to java.rmi;
    requires com.dlsc.formsfx;
    requires java.rmi;
    requires java.desktop;

    opens client to javafx.fxml;
    exports client;
}