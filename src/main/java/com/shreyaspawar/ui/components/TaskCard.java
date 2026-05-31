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
    private Task previousTask = null;  // track the last bound task

    public TaskCard(Task task) {
        getStyleClass().add("task-card");
        setPadding(new Insets(12));
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);

        Button dragHandle = new Button("☰");
        dragHandle.getStyleClass().add("drag-handle");

        checkBox = new CheckBox();
        titleLabel = new Label();
        titleLabel.getStyleClass().add("task-title");

        priorityBadge = new Label();
        priorityBadge.getStyleClass().addAll("badge", "priority-badge");
        dueChip = new Label();
        dueChip.getStyleClass().add("due-chip");

        starBtn = new Button();
        starBtn.getStyleClass().add("icon-btn");
        starBtn.setOnAction(e -> {
            Task t = taskProperty.get();
            if (t != null) {
                t.setImportant(!t.isImportant());
                updateImportant();
            }
        });

        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("icon-btn");
        deleteBtn.setOnAction(e -> {
            if (deleteAction != null) deleteAction.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(dragHandle, checkBox, titleLabel, priorityBadge, dueChip, starBtn, deleteBtn);

        taskProperty.addListener((obs, old, newTask) -> updateTask());
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

    /** Clean up bindings before reuse */
    public void unbind() {
        if (previousTask != null) {
            checkBox.selectedProperty().unbindBidirectional(previousTask.completedProperty());
            previousTask = null;
        }
        checkBox.selectedProperty().unbind();   // safety
        titleLabel.getStyleClass().remove("completed");
    }

    private void updateTask() {
        Task t = taskProperty.get();
        // Unbind previous task
        if (previousTask != null) {
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
        // Bind checkbox
        checkBox.selectedProperty().unbind();   // ensure clean
        checkBox.setSelected(t.isCompleted());
        checkBox.selectedProperty().bindBidirectional(t.completedProperty());

        titleLabel.getStyleClass().remove("completed");
        if (t.isCompleted()) titleLabel.getStyleClass().add("completed");

        // Priority badge
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

        // Due date chip
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
    }

    private void updateImportant() {
        Task t = taskProperty.get();
        if (t == null) return;
        starBtn.setText(t.isImportant() ? "★" : "☆");
        if (t.isImportant()) {
            getStyleClass().add("important");
        } else {
            getStyleClass().remove("important");
        }
    }
}