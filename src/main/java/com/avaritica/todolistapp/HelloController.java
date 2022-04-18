package com.avaritica.todolistapp;

import com.avaritica.todolistapp.datamodel.TodoData;
import com.avaritica.todolistapp.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public class HelloController {

    @FXML
    private ListView<TodoItem> toDoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadLineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private CheckBox filterCheckBox;

    private FilteredList<TodoItem> filteredList;
    private Predicate<TodoItem> todayItems;
    private Predicate<TodoItem> allItems;

    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            TodoItem item = toDoListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });
        listContextMenu.getItems().add(deleteMenuItem);
        toDoListView.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue) -> {
            if(newValue!=null) {
               TodoItem item = toDoListView.getSelectionModel().getSelectedItem();
               itemDetailsTextArea.setText(item.getDetails());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
               deadLineLabel.setText(df.format(item.getDeadline()));
            }
        });

        todayItems = (item) -> item.getDeadline().equals(LocalDate.now());
        allItems = (item) -> true;

        filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), allItems);

        SortedList<TodoItem> sortedList = new SortedList<>(filteredList,
                Comparator.comparing(TodoItem::getDeadline)
        );

        toDoListView.setItems(sortedList);
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();

        toDoListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem todoItem, boolean empty) {
                        super.updateItem(todoItem, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(todoItem.getShortDescription());
//                            setFont(Font.font("Times New Roman Italic",18));
                            if (todoItem.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (todoItem.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.BLUEVIOLET);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener((observableValue, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        cell.setContextMenu(null);
                    } else {
                        cell.setContextMenu(listContextMenu);
                    }
                });
                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new todo Item");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            TodoItem newitem = controller.processResults();
            toDoListView.getSelectionModel().select(newitem);
        }
    }

    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete todoItem");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("This will delete the selected item. Are you sure ?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && (result.get()==ButtonType.OK)) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent){
        TodoItem item = toDoListView.getSelectionModel().getSelectedItem();
        if(item!=null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(item);
            }
        }
    }

    @FXML
    public void handleFilterButton() {
        TodoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if(filterCheckBox.isSelected()) {
            filteredList.setPredicate(todayItems);
            if(filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadLineLabel.setText("");
            } else if(filteredList.contains(selectedItem)) {
                toDoListView.getSelectionModel().select(selectedItem);
            } else {
                toDoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(allItems);
            toDoListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }
}