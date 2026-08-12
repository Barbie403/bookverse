module it.bookverse {
    requires javafx.controls;
    requires javafx.fxml;

    exports it.bookverse;
    exports it.bookverse.boundary;

    opens it.bookverse.boundary to javafx.fxml;
}