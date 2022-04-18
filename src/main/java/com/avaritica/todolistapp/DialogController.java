package com.avaritica.todolistapp;

import com.avaritica.todolistapp.datamodel.TodoData;
import com.avaritica.todolistapp.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea details;
    @FXML
    private DatePicker deadlinePicker;

    public TodoItem processResults() {
        String description = shortDescription.getText().trim();
        String detail = details.getText().trim();
        LocalDate localDate = deadlinePicker.getValue();
        TodoItem item = new TodoItem(description,detail,localDate);
        TodoData.getInstance().addTodoItems(item);
        return item;
    }
}
