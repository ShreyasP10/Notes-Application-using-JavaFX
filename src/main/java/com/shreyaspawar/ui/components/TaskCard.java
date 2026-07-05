package com.shreyaspawar.ui.components;

import com.shreyaspawar.model.Task;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskCard extends HBox {
    private final ObjectProperty<Task> taskProperty = new SimpleObjectProperty<>();
    private final CheckBox checkBox;
    private final Label titleLabel;
    private final Label priorityBadge;
    private final Label dueChip;
    private final Button starBtn;
    private Runnable deleteAction;
    private Task previousTask = null;

    public TaskCard(Task task) {
        getStyleClass().add("task-card");
        setPadding(new Insets(14, 16, 14, 16));
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);

        checkBox = new CheckBox();
        VBox textContainer = new VBox(4);
        titleLabel = new Label();
        titleLabel.getStyleClass().add("task-title");
        textContainer.getChildren().add(titleLabel);

        HBox metaRow = new HBox(6);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        priorityBadge = new Label();
        priorityBadge.getStyleClass().addAll("badge");
        dueChip = new Label();
        dueChip.getStyleClass().add("due-chip");
        metaRow.getChildren().addAll(priorityBadge, dueChip);
        metaRow.setVisible(false);
        textContainer.getChildren().add(metaRow);

        starBtn = new Button();
        starBtn.getStyleClass().add("icon-btn");
        starBtn.setOnAction(e -> {
            Task t = taskProperty.get();
            if (t != null) {
                t.setImportant(!t.isImportant());
                updateImportant();
            }
        });

        Button deleteBtn = new Button("\uD83D\uDDD1");
        deleteBtn.getStyleClass().add("icon-btn");
        deleteBtn.setOpacity(0);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setOpacity(1));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setOpacity(0));
        deleteBtn.setOnAction(e -> {
            if (deleteAction != null) deleteAction.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(checkBox, textContainer, spacer, starBtn, deleteBtn);

        setOnMouseEntered(e -> deleteBtn.setOpacity(1));
        setOnMouseExited(e -> deleteBtn.setOpacity(0));

        taskProperty.addListener((obs, old, newTask) -> updateTask());
        metaRow.managedProperty().bind(metaRow.visibleProperty());
        setTask(task);
    }

    public void setTask(Task task) {
        taskProperty.set(task);
    }

    public void setDeleteAction(Runnable action) {
        this.deleteAction = action;
    }

    public Task getTask() {
        return taskProperty.get();
    }

    public void unbind() {
        if (previousTask != null) {
            checkBox.selectedProperty().unbindBidirectional(previousTask.completedProperty());
            previousTask = null;
        }
        titleLabel.getStyleClass().remove("completed");
    }

    private void updateTask() {
        Task t = taskProperty.get();
        if (previousTask != null && previousTask != t) {
            checkBox.selectedProperty().unbindBidirectional(previousTask.completedProperty());
        }
        previousTask = t;

        if (t == null) {
            titleLabel.setText("");
            checkBox.setSelected(false);
            priorityBadge.setVisible(false);
            dueChip.setVisible(false);
            return;
        }

        titleLabel.setText(t.getTitle());
        checkBox.selectedProperty().unbindBidirectional(t.completedProperty());
        checkBox.setSelected(t.isCompleted());
        checkBox.selectedProperty().bindBidirectional(t.completedProperty());

        titleLabel.getStyleClass().remove("completed");
        if (t.isCompleted()) titleLabel.getStyleClass().add("completed");

        String priority = t.getPriority();
        if (priority != null && !priority.isEmpty()) {
            priorityBadge.setText(priority.substring(0, 1).toUpperCase());
            priorityBadge.getStyleClass().removeAll("priority-high", "priority-medium", "priority-low");
            switch (priority) {
                case "high" -> priorityBadge.getStyleClass().add("priority-high");
                case "medium" -> priorityBadge.getStyleClass().add("priority-medium");
                case "low" -> priorityBadge.getStyleClass().add("priority-low");
            }
            priorityBadge.setVisible(true);
        } else {
            priorityBadge.setVisible(false);
        }

        LocalDate due = t.getDueDate();
        if (due != null) {
            dueChip.setText(due.format(DateTimeFormatter.ofPattern("MMM d")));
            dueChip.setVisible(true);
            dueChip.getStyleClass().remove("overdue");
            if (due.isBefore(LocalDate.now()) && !t.isCompleted()) {
                dueChip.getStyleClass().add("overdue");
            }
        } else {
            dueChip.setVisible(false);
        }

        updateImportant();

        HBox metaRow = (HBox) ((VBox) titleLabel.getParent()).getChildren().get(1);
        metaRow.setVisible(priorityBadge.isVisible() || dueChip.isVisible());
    }

    private void updateImportant() {
        Task t = taskProperty.get();
        if (t == null) return;
        starBtn.setText(t.isImportant() ? "\u2605" : "\u2606");
        if (t.isImportant()) {
            getStyleClass().add("important");
            starBtn.getStyleClass().add("star-btn-active");
        } else {
            getStyleClass().remove("important");
            starBtn.getStyleClass().remove("star-btn-active");
        }
    }
}
