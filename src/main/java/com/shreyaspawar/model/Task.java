package com.shreyaspawar.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Task {
    private final StringProperty title = new SimpleStringProperty("");
    private final BooleanProperty completed = new SimpleBooleanProperty(false);
    private final BooleanProperty important = new SimpleBooleanProperty(false);
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>(null);
    private final StringProperty priority = new SimpleStringProperty(""); // low, medium, high
    private final StringProperty tag = new SimpleStringProperty("");

    public Task(String title) {
        this.title.set(title);
    }

    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }

    public boolean isCompleted() { return completed.get(); }
    public BooleanProperty completedProperty() { return completed; }
    public void setCompleted(boolean completed) { this.completed.set(completed); }

    public boolean isImportant() { return important.get(); }
    public BooleanProperty importantProperty() { return important; }
    public void setImportant(boolean important) { this.important.set(important); }

    public LocalDate getDueDate() { return dueDate.get(); }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate.set(dueDate); }

    public String getPriority() { return priority.get(); }
    public StringProperty priorityProperty() { return priority; }
    public void setPriority(String priority) { this.priority.set(priority); }

    public String getTag() { return tag.get(); }
    public StringProperty tagProperty() { return tag; }
    public void setTag(String tag) { this.tag.set(tag); }
}