package com.shreyaspawar.ui;

import com.shreyaspawar.data.DataManager;
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
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
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
    private final Predicate<Task> baseFilter;

    public TaskPanel(String title) {
        this(title, null);
    }

    public TaskPanel(String title, Predicate<Task> baseFilter) {
        this.baseFilter = baseFilter;
        getStyleClass().add("task-panel");
        setPadding(new Insets(24, 32, 24, 32));
        setSpacing(18);

        filteredTasks = new FilteredList<>(masterTasks);

        emptyPlaceholder = createEmptyPlaceholder();

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox headerText = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("panel-title");
        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        dateLabel.getStyleClass().add("subtitle");
        headerText.getChildren().addAll(titleLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox filterBar = new HBox(8);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Search tasks...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(180);
        tagFilter = new ChoiceBox<>();
        tagFilter.setPrefWidth(110);
        tagFilter.setTooltip(new Tooltip("Filter by tag"));
        tagFilter.getItems().add("All");
        tagFilter.setValue("All");
        filterBar.getChildren().addAll(tagFilter, searchField);

        header.getChildren().addAll(headerText, spacer, filterBar);

        sortedTasks = new SortedList<>(filteredTasks, (t1, t2) -> {
            int p1 = priorityValue(t1.getPriority());
            int p2 = priorityValue(t2.getPriority());
            return Integer.compare(p2, p1);
        });

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
                    card.unbind();
                    setGraphic(null);
                } else {
                    card.unbind();
                    card.setTask(task);
                    setGraphic(card);
                }
            }
        });
        listView.getStyleClass().add("task-list-view");
        VBox.setVgrow(listView, Priority.ALWAYS);
        listView.setPlaceholder(emptyPlaceholder);

        HBox inputBox = new HBox(12);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(8, 0, 0, 0));
        inputField = new TextField();
        inputField.setPromptText("Add a new task... (use !high, @2024-12-25, #tag)");
        inputField.getStyleClass().add("floating-input");
        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("add-task-btn");
        addBtn.setOnAction(e -> addTask());
        inputField.setOnAction(e -> addTask());
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputBox.getChildren().addAll(inputField, addBtn);

        Label hint = new Label("Ctrl+N to focus input  \u00B7  !priority  @date  #tag");
        hint.setStyle("-fx-font-size: 11px; -fx-opacity: 0.5; -fx-padding: 0 0 0 4;");

        getChildren().addAll(header, listView, inputBox, hint);

        searchField.textProperty().addListener((obs, old, text) -> updateFilters());
        tagFilter.setOnAction(e -> updateFilters());
        updateFilters();

        masterTasks.addListener((javafx.collections.ListChangeListener<Task>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Task t : c.getAddedSubList()) {
                        attachTaskListeners(t);
                    }
                }
                if (c.wasRemoved()) {
                    for (Task t : c.getRemoved()) {
                        detachTaskListeners(t);
                    }
                }
            }
            saveData();
        });

        setOnKeyPressed(e -> {
            if (new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN).match(e)) {
                inputField.requestFocus();
                e.consume();
            }
        });
        setFocusTraversable(true);

        loadData();
    }

    private void addTask() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        Task task = new Task(parseTitle(text));
        parseMetadata(text, task);

        masterTasks.add(task);
        inputField.clear();
        updateTagFilter();
    }

    private String parseTitle(String text) {
        return text.replaceAll("![^\\s]*", "")
                   .replaceAll("@[^\\s]*", "")
                   .replaceAll("#[^\\s]*", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    private void parseMetadata(String text, Task task) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("!([^\\s]+)").matcher(text);
        if (m.find()) task.setPriority(m.group(1).toLowerCase());

        m = java.util.regex.Pattern.compile("@([^\\s]+)").matcher(text);
        if (m.find()) {
            try { task.setDueDate(LocalDate.parse(m.group(1))); } catch (Exception ignored) {}
        }

        m = java.util.regex.Pattern.compile("#([^\\s]+)").matcher(text);
        if (m.find()) task.setTag(m.group(1));
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
        if (selectedTag == null) selectedTag = "All";

        final String finalTag = selectedTag;
        filteredTasks.setPredicate(task -> {
            if (baseFilter != null && !baseFilter.test(task)) return false;
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
        Label label = new Label("\uD83C\uDF89 No tasks yet\nAdd your first task above!");
        label.getStyleClass().add("empty-state");
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(50));
        return label;
    }

    private final Map<Task, javafx.beans.value.ChangeListener<Object>> taskListeners = new WeakHashMap<>();

    private void attachTaskListeners(Task t) {
        javafx.beans.value.ChangeListener<Object> listener = (obs, old, val) -> saveData();
        t.completedProperty().addListener(listener);
        t.importantProperty().addListener(listener);
        t.titleProperty().addListener(listener);
        t.priorityProperty().addListener(listener);
        t.dueDateProperty().addListener(listener);
        t.tagProperty().addListener(listener);
        taskListeners.put(t, listener);
    }

    private void detachTaskListeners(Task t) {
        javafx.beans.value.ChangeListener<Object> listener = taskListeners.remove(t);
        if (listener != null) {
            t.completedProperty().removeListener(listener);
            t.importantProperty().removeListener(listener);
            t.titleProperty().removeListener(listener);
            t.priorityProperty().removeListener(listener);
            t.dueDateProperty().removeListener(listener);
            t.tagProperty().removeListener(listener);
        }
    }

    private void saveData() {
        DataManager.saveTasks(masterTasks);
    }

    private void loadData() {
        java.util.List<Task> saved = DataManager.loadTasks();
        if (!saved.isEmpty()) {
            masterTasks.setAll(saved);
            updateTagFilter();
        }
    }
}
