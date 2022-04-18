package com.avaritica.todolistapp.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;

public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "todoListItems.txt";

    private ObservableList<TodoItem> todoItems;
    private DateTimeFormatter formatter;

    public static TodoData getInstance() {
        return instance;
    }

    private TodoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.ENGLISH);
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }


    public void loadTodoItems() throws IOException {
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        String input;

        try(BufferedReader reader = Files.newBufferedReader(path)) {
            while((input = reader.readLine())!=null) {
                String[] itemPieces = input.split("\t");
                String shortDesc = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate localDate = LocalDate.parse(dateString,formatter);
                TodoItem todoItem = new TodoItem(shortDesc,details,localDate);
                todoItems.add(todoItem);
            }
        }
    }

    public void storeTodoItems() throws IOException {
        Path path = Paths.get(filename);
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            Iterator<TodoItem> iter = todoItems.iterator();
            while(iter.hasNext()) {
                TodoItem item = iter.next();
                writer.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter)));
                writer.newLine();
            }
        }
    }

    public void deleteTodoItem(TodoItem item) {
        todoItems.remove(item);
    }

    public void addTodoItems(TodoItem item) {
        todoItems.add(item);
    }
}
