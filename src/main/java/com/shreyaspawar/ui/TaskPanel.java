package com.shreyaspawar.ui;

import com.shreyaspawar.model.Task;
import com.shreyaspawar.ui.components.CustomDialog;
import com.shreyaspawar.ui.components.TaskCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TaskPanel extends VBox {
    private final ObservableList<Task> masterTasks = FXCollections.observableArrayList();
    private final FilteredList<Task> filteredTasks;
    private final SortedList<Task> sortedTasks;
    private final ListView<Task> listView;
    private final TextField inputField;
    private final TextField searchField;
    private final ChoiceBox<String> tagFilter;
    private final Label emptyPlaceholder;

    public TaskPanel(String title) {
        this(title, null); // no extra filter
    }

    public TaskPanel(String title, Predicate<Task> baseFilter) {
        getStyleClass().add("task-panel");
        setPadding(new Insets(20));
        setSpacing(15);

        // Apply base filter
        filteredTasks = new FilteredList<>(masterTasks);
        if (baseFilter != null) {
            filteredTasks.setPredicate(baseFilter);
        }

        emptyPlaceholder = createEmptyPlaceholder();

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("panel-title");
        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        dateLabel.getStyleClass().add("subtitle");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("search-field");
        tagFilter = new ChoiceBox<>();
        tagFilter.setPrefWidth(100);
        tagFilter.setTooltip(new Tooltip("Filter by tag"));
        tagFilter.getItems().add("All");
        tagFilter.setValue("All");

        header.getChildren().addAll(titleLabel, dateLabel, spacer, tagFilter, searchField);

        // Priority sorting
        sortedTasks = new SortedList<>(filteredTasks, (t1, t2) -> {
            int p1 = priorityValue(t1.getPriority());
            int p2 = priorityValue(t2.getPriority());
            return Integer.compare(p2, p1);
        });

        // Virtualized task list
        listView = new ListView<>(sortedTasks);
        listView.setCellFactory(param -> new ListCell<>() {
            private final TaskCard card = new TaskCard(null);
            {
                card.setDeleteAction(() -> {
                    Task t = card.getTask();
                    if (t != null) deleteTask(t);
                });
            }
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    card.unbind();   // clean up old bindings
                    setGraphic(null);
                } else {
                    card.unbind();   // unbind previous
                    card.setTask(task);
                    setGraphic(card);
                }
            }
        });
        listView.getStyleClass().add("task-list-view");
        VBox.setVgrow(listView, Priority.ALWAYS);
        listView.setPlaceholder(emptyPlaceholder);

        // Input area (improved)
        inputField = new TextField();
        inputField.setPromptText("Add a new task...");
        inputField.getStyleClass().add("floating-input");
        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("add-task-btn");
        addBtn.setOnAction(e -> addTask());
        inputField.setOnAction(e -> addTask());
        HBox inputBox = new HBox(10, inputField, addBtn);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(15, 0, 0, 0));
        HBox.setHgrow(inputField, Priority.ALWAYS);

        getChildren().addAll(header, listView, inputBox);

        // Search & filter logic (default tag to "All" if null)
        searchField.textProperty().addListener((obs, old, text) -> updateFilters());
        tagFilter.setOnAction(e -> updateFilters());
        updateFilters();

        // Keyboard shortcut
        setOnKeyPressed(e -> {
            if (new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN).match(e)) {
                inputField.requestFocus();
                e.consume();
            }
        });
        setFocusTraversable(true);
    }

    private void addTask() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        Task task = new Task(text);
        // Quick parsing
        if (text.contains("!")) {
            int start = text.indexOf("!") + 1;
            int end = text.indexOf(" ", start);
            if (end == -1) end = text.length();
            task.setPriority(text.substring(start, end).toLowerCase());
        }
        if (text.contains("@")) {
            int start = text.indexOf("@") + 1;
            int end = text.indexOf(" ", start);
            if (end == -1) end = text.length();
            try { task.setDueDate(LocalDate.parse(text.substring(start, end))); } catch (Exception ignored) {}
        }
        if (text.contains("#")) {
            int start = text.indexOf("#") + 1;
            int end = text.indexOf(" ", start);
            if (end == -1) end = text.length();
            task.setTag(text.substring(start, end));
        }

        masterTasks.add(task);
        inputField.clear();
        updateTagFilter();
    }

    private void deleteTask(Task task) {
        CustomDialog.showConfirm("Delete Task", "This task will be permanently deleted.", () -> {
            masterTasks.remove(task);
            updateTagFilter();
        });
    }

    private void updateFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedTag = tagFilter.getValue();
        if (selectedTag == null) selectedTag = "All";   // prevents NPE

        final String finalTag = selectedTag;
        filteredTasks.setPredicate(task -> {
            boolean tagOk = finalTag.equals("All") || finalTag.equals(task.getTag());
            boolean searchOk = searchText.isEmpty() || task.getTitle().toLowerCase().contains(searchText);
            return tagOk && searchOk;
        });
    }

    private void updateTagFilter() {
        Set<String> tags = new HashSet<>();
        for (Task t : masterTasks) {
            String tag = t.getTag();
            if (tag != null && !tag.isEmpty()) tags.add(tag);
        }
        String selected = tagFilter.getValue();
        if (selected == null) selected = "All";
        tagFilter.getItems().setAll("All");
        tagFilter.getItems().addAll(tags);
        if (tags.contains(selected)) {
            tagFilter.setValue(selected);
        } else {
            tagFilter.setValue("All");
        }
    }

    private int priorityValue(String p) {
        return switch (p) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };
    }

    private Label createEmptyPlaceholder() {
        Label label = new Label("🎉 No tasks found\nAdd something productive.");
        label.getStyleClass().add("empty-state");
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(40));
        return label;
    }
}