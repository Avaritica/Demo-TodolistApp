module com.avaritica.todolistapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.avaritica.todolistapp to javafx.fxml;
    exports com.avaritica.todolistapp;
    exports com.avaritica.todolistapp.datamodel;
}