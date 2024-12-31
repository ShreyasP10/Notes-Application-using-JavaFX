package com.shreyaspawar;

import javafx.application.Application;

import javafx.geometry.Pos;

import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Interpolator;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class App extends Application {

    private Stage splashStage;// variable for Splash Screen

    private BorderPane root;
    private VBox sidebar;
    private VBox groupsContainer; // Properly initialized
    private StackPane mainPanel;
    private HashMap<String, VBox> groups = new HashMap<>();

    private boolean isDrawerExpanded = true;
    // Task ID Generator
    AtomicInteger taskIdGenerator = new AtomicInteger(1);

    private Color currentColor = Color.BLACK;
    private Pane workspace; // The central pane

    Font sharpMaterialFont;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        showMainApp(primaryStage);
    }

    private void addSidebarOption(VBox sidebar, String text, String icon) {
        HBox sidebarOption = new HBox(10);
        sidebarOption.setAlignment(Pos.CENTER_LEFT);
        sidebarOption.setPadding(new Insets(5, 10, 5, 10));
        sidebarOption.setStyle("-fx-background-color: rgb(45, 45, 45); -fx-background-radius: 10;");

        // Create the icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(new Font("Arial", 18));
        iconLabel.setStyle("-fx-text-fill: white;");

        // Create the text
        Label textLabel = new Label(text);
        textLabel.setFont(new Font("Arial", 16));
        textLabel.setStyle("-fx-text-fill: white;");

        // Initially show text based on isDrawerExpanded
        if (isDrawerExpanded) {
            sidebarOption.getChildren().addAll(iconLabel, textLabel);
        } else {
            sidebarOption.getChildren().add(iconLabel);
        }

        // Add hover animations
        sidebarOption.setOnMouseEntered(e -> {
            sidebarOption.setStyle("-fx-background-color: #555555; -fx-background-radius: 10;");
            animateHover(sidebarOption, 1.05); // Scale up
        });
        sidebarOption.setOnMouseExited(e -> {
            sidebarOption.setStyle("-fx-background-color: rgb(45, 45, 45); -fx-background-radius: 10;");
            animateHover(sidebarOption, 1.0); // Scale back
        });

        // Add click event
        sidebarOption.setOnMouseClicked(e -> switchToPanel(text));

        sidebar.getChildren().add(sidebarOption);
    }

    private void animateHover(HBox node, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    private void toggleDrawerSize(VBox sidebar, Button toggleButton) {
        isDrawerExpanded = !isDrawerExpanded;

        double targetWidth = isDrawerExpanded ? 200 : 60;
        double initialWidth = sidebar.getWidth();

        // Animate the sidebar width
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(sidebar.prefWidthProperty(), initialWidth)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(sidebar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH)));

        timeline.setOnFinished(e -> {
            toggleButton.setText(isDrawerExpanded ? "M" : "O");

            // Adjust sidebar content
            for (Node node : sidebar.getChildren()) {
                if (node instanceof HBox) {
                    HBox sidebarOption = (HBox) node;

                    // Retrieve icon and text
                    Label iconLabel = (Label) sidebarOption.getChildren().get(0); // Always present
                    Label textLabel = sidebarOption.getChildren().size() > 1
                            ? (Label) sidebarOption.getChildren().get(1)
                            : null;

                    if (isDrawerExpanded) {
                        // Add textLabel back if not already present
                        if (textLabel == null) {
                            textLabel = new Label(" Text"); // Adjust text dynamically if needed
                            textLabel.setFont(new Font("Arial", 16));
                            textLabel.setStyle("-fx-text-fill: white;");
                            sidebarOption.getChildren().add(textLabel);
                        }
                    } else {
                        // Remove textLabel if present
                        if (textLabel != null) {
                            sidebarOption.getChildren().remove(textLabel);
                        }
                    }
                }
            }
        });

        timeline.play();
    }

    // private void toggleGroupBtnSize(VBox sidebar, Button addGroup) {
    // isDrawerExpanded = !isDrawerExpanded;

    // if (isDrawerExpanded) {
    // sidebar.setPrefWidth(200);
    // addGroup.setText("M");
    // } else {
    // sidebar.setPrefWidth(60);
    // addGroup.setText("O");
    // } if (isDrawerExpanded) {
    // sidebarOption.getChildren().addAll(textLabel);
    // } else {
    // sidebarOption.getChildren().add(iconLabel);
    // }

    // for (Node node : sidebar.getChildren()) {
    // if (node instanceof HBox) {
    // HBox sidebarOption = (HBox) node;
    // Label textLabel = sidebarOption.getChildren().size() > 1 ? (Label)
    // sidebarOption.getChildren().get(1) : null;

    // if (isDrawerExpanded) {
    // if (textLabel != null && !sidebarOption.getChildren().contains(textLabel)) {
    // sidebarOption.getChildren().add(textLabel);
    // }
    // } else {
    // if (textLabel != null) {
    // sidebarOption.getChildren().remove(textLabel);
    // }
    // }
    // }
    // }
    // }

    private void showMainApp(Stage primaryStage) {
        primaryStage.setTitle("To-Do List App");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);


        // Detect system theme
        String theme = SystemThemeDetector.getSystemTheme();
        String cssFile = theme.equals("dark") ? "dark-theme.css" : "light-theme.css";

        root = new BorderPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/"+cssFile).toExternalForm());
        // Toggle button for drawer size
        // Sidebar
        sidebar = new VBox(10);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #2D2D2D;");

        Button toggleButton = new Button("M");
        toggleButton.setStyle(
                "-fx-background-color: #444444; -fx-font-size:15; -fx-text-fill: white;  -fx-border-radius: 20px; -fx-background-radius: 20px;");
        toggleButton.setOnAction(e -> toggleDrawerSize(sidebar, toggleButton));
        VBox.setMargin(toggleButton, new Insets(5, 2, 5, 2));
        sidebar.getChildren().add(toggleButton);

        // Add sidebar options
        addSidebarOption(sidebar, "My Day", "🌞");
        addSidebarOption(sidebar, "My Notes", "📝");
        addSidebarOption(sidebar, "Important", "⭐");

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #444;");
        separator.setPrefHeight(1);
        sidebar.getChildren().add(separator);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // Button addGroup=new Button("+");
        // addGroup.setStyle("-fx-background-color: #444444; -fx-font-size:15;
        // -fx-text-fill: white; -fx-border-radius: 20px; -fx-background-radius:
        // 20px;");
        // VBox.setMargin(toggleButton, new Insets(5, 2, 5, 2));

        // addGroup.setOnAction(e -> toggleGroupBtnSize(sidebar, addGroup));
        // sidebar.getChildren().add(addGroup);
        addSidebarOption(sidebar, "Add Group", "+");

        // Main Panel
        mainPanel = new StackPane();
        mainPanel.setStyle("-fx-background-color: #232323;");
        root.setCenter(mainPanel);
        root.setLeft(sidebar);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Default to the "My Day" panel
        switchToPanel("My Day");
    }

    private void switchToPanel(String panelName) {
        mainPanel.getChildren().clear();

        if (panelName.equals("My Day")) {
            mainPanel.getChildren().add(createMyDayPanel());
        } else if (panelName.equals("My Notes")) {
            mainPanel.getChildren().add(createMyNotes());
        } else if (panelName.equals("Important")) {
            // mainPanel.getChildren().add(createImportantPanel());
        } else {
            Label errorLabel = new Label("Panel not found.");
            errorLabel.setFont(new Font("Arial", 24));
            errorLabel.setStyle("-fx-text-fill: white;");
            mainPanel.getChildren().add(errorLabel);
        }
    }
    // private VBox createPanel(String message, String icon) {
    // VBox panel = new VBox(10);
    // panel.setAlignment(Pos.CENTER);
    // panel.setStyle("-fx-background-color: #393939; -fx-border-radius: 10;
    // -fx-padding: 20;");

    // Label iconLabel = new Label(icon);
    // iconLabel.setFont(new Font("Arial", 48));
    // iconLabel.setStyle("-fx-text-fill: white;");

    // Label messageLabel = new Label(message);
    // messageLabel.setFont(new Font("Arial", 24));
    // messageLabel.setStyle("-fx-text-fill: white;");

    // panel.getChildren().addAll(iconLabel, messageLabel);
    // return panel;
    // }

    private Button createRoundedButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #414141; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 20px; -fx-background-radius: 20px;");
        button.setMinWidth(180);
        return button;
    }

    private VBox createMyDayPanel() {

        VBox myDayPanel = new VBox(10);
        myDayPanel.setPadding(new Insets(10));
        myDayPanel.setStyle("-fx-background-color: #232323;"); // Dark background for the panel
        myDayPanel.setAlignment(Pos.TOP_CENTER);

        // Navbar for label and Setting Btn
        HBox navbar = new HBox(10);
        navbar.setPadding(new Insets(10));
        // Label at the top
        Label label = new Label("My Day");
        label.getStyleClass().add("myDayLabel");
        label.setFont(new Font("Arial", 24));
        label.setTextFill(Color.WHITE);

        // For Moving Button to right side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Button for Setting
        Button settingsButton = createRoundedButton("$");
        settingsButton.setStyle(
                "-fx-background-color: #444444; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +

                        "-fx-background-radius: 50%; " +
                        "-fx-border-radius: 15px;" +
                        "-fx-border-width: 1px;");

        // "-fx-border-color: white; " +
        settingsButton.setMinSize(40, 40);
        settingsButton.setMaxSize(40, 40);

        // Adding Label and Setting Button
        navbar.getChildren().addAll(label, spacer, settingsButton);

        // ScrollPane for task list
        VBox taskList = new VBox(10);
        taskList.setStyle(
                "-fx-background-color: #383838; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Creating the ScrollPane with custom CSS styles
        ScrollPane taskScroll = new ScrollPane(taskList);
        taskScroll.setFitToWidth(true);
        taskScroll.setStyle(
                "-fx-background: #383838; -fx-border-color: transparent;  -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-viewport-border-radius: 15px; ");

        // Set the scrollbar styles
        taskScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Hide horizontal scrollbar
        taskScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hide vertical scrollbar
        // taskScroll.setVbarPolicy(ScrollBarPolicy.ALWAYS); // Always show vertical
        // scrollbar if needed
        // taskScroll.setHbarPolicy(ScrollBarPolicy.NEVER); // Disable horizontal
        // scrolling

        // Apply the custom scrollbar CSS class
        // taskScroll.getStyleClass().add("scroll-pane");

        // TextField and Button for adding tasks
        TextField taskInput = new TextField();
        taskInput.setPromptText("Add a new task");
        taskInput.setStyle("-fx-background-color: #505050; -fx-text-fill: white;");

        Button addTaskButton = createRoundedButton("Add Task");

        addTaskButton.setOnAction(e -> {
            String task = taskInput.getText().trim();
            if (!task.isEmpty()) {
                // Create a task label
                Label taskLabel = new Label(task);
                taskLabel.setFont(new Font("Arial", 16));
                taskLabel.setTextFill(Color.WHITE);

                // Create icons for rename, remove, and duplicate
                Button renameButton = createIconButton("Rename", "R");

                Button starButton = createIconButton("Marked As Star", "S");

                Button deleteButton = createIconButton("Remove", "D");

                // Arrange the label and buttons in an HBox
                HBox taskLabelContainer = new HBox(10, taskLabel, renameButton, starButton, deleteButton);
                taskLabelContainer.setAlignment(Pos.CENTER_LEFT);
                taskLabelContainer.setStyle(
                        "-fx-background-color: #444444; -fx-padding: 10; -fx-border-radius: 5; -fx-border-radius: 15;   -fx-background-radius: 15;"); // Task

                // Action for Rename Task
                renameButton.setOnAction(ev -> renameTask(taskLabel));

                // Action for Remove Task
                deleteButton.setOnAction(ev -> deleteTask(taskList, taskLabelContainer));

                // Action for Duplicate Task
                starButton.setOnAction(ev -> markTaskAsImportant(taskLabel.getText(), starButton));

                // Add the task label container to the task list
                taskList.getChildren().add(taskLabelContainer);

                // Clear the task input field
                taskInput.clear();
            }
        });

        // Task input area (TextField and Button combined)
        HBox inputArea = createMergedTaskInputArea(taskList);

        VBox.setVgrow(taskScroll, Priority.ALWAYS); // Task list grows to fill space
        myDayPanel.getChildren().addAll(navbar, taskScroll, inputArea);

        return myDayPanel;
    }

    private void markTaskAsImportant(String taskLabel, Button starButton) {
        if (starButton.getText() == "ST") {
            starButton.setText("S"); // Change the button text back to "S"
        } else {
            starButton.setText("ST"); // Change the button text to "ST"
        }
    }

    private Button createIconButton(String tooltipText, String iconText) {
        Button button = new Button(iconText);
        // button.setGraphic(fontStar); // Fix: Set the FontAwesome icon as the button's
        // graphic
        button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-font-size: 14px; " + // Font size for the icon
                        "-fx-text-fill: #FFFFFF; " + // Default text color
                        "-fx-border-radius: 15; " + // Rounded corners
                        "-fx-padding: 5; " // Padding for click area
        );

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #444444; " + // Slightly darker background
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 5;"));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-border-radius: 15; " +
                        "-fx-padding: 5;"));

        // Add tooltip
        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(button, tooltip);

        return button;
    }

    private HBox createMergedTaskInputArea(VBox taskList) {
        // Styled TextField
        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter Tasks");
        taskInput.setStyle(
                "-fx-background-color: #1E1E1E; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 15px; " +
                        "-fx-prompt-text-fill: #AAAAAA; " +
                        "-fx-background-radius: 20 0 0 20; " + // Rounded left corners
                        "-fx-border-radius: 20 0 0 20; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-color: #444444; " +
                        "-fx-border-width: 1;");

        // Styled Button
        Button addTaskButton = new Button("Add Task");
        addTaskButton.setStyle(
                "-fx-background-color: #2E2E2E; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 15px; " +
                        "-fx-background-radius: 0 20 20 0; " + // Rounded right corners
                        "-fx-border-radius: 0 20 20 0; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-color: #444444; " +
                        "-fx-border-width: 1;");

        addTaskButton.setOnAction(e -> addTaskToList(taskList, taskInput));
        taskInput.setOnAction(e -> addTaskToList(taskList, taskInput));

        // Layout: Merge the TextField and Button
        HBox inputArea = new HBox(taskInput, addTaskButton);
        inputArea.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        inputArea.setAlignment(Pos.CENTER); // Center-align the elements
        return inputArea;
    }

    // Unique ID generator for tasks
    private void addTaskToList(VBox taskList, TextField taskInput) {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            // Check for duplicates
            boolean isDuplicate = taskList.getChildren().stream()
                    .filter(node -> node instanceof HBox)
                    .map(node -> (HBox) node)
                    .filter(hbox -> !hbox.getChildren().isEmpty())
                    .map(hbox -> hbox.getChildren().get(0))
                    .filter(child -> child instanceof Label)
                    .map(child -> (Label) child)
                    .anyMatch(label -> label.getText().equals(task));

            if (isDuplicate) {
                taskInput.clear();
                showError("Duplicate task is not allowed!");
                return;
            }

            // Create the task label
            Label taskLabel = new Label(task);
            taskLabel.setFont(new Font("Arial", 16));
            taskLabel.setTextFill(Color.WHITE);

            // Create buttons
            Button renameButton = createRoundIconButton("Rename", "R");
            Button deleteButton = createRoundIconButton("Remove", "D");
            Button starButton = createRoundIconButton("Mark As Important", "S");

            // Create a drag handle button
            Button dragButton = createIconButton("Drag", "☰");
            dragButton.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: white;");
            dragButton.setTooltip(new Tooltip("Press and drag to reorder"));

            // Task label container
            HBox taskLabelContainer = new HBox(10, dragButton, taskLabel);
            taskLabelContainer.setAlignment(Pos.CENTER_LEFT);
            taskLabelContainer.setStyle(
                    "-fx-background-color: #1E1E1E; " +
                            "-fx-padding: 10; " +
                            "-fx-border-radius: 8;");

            // Button container
            HBox buttonContainer = new HBox(10, renameButton, starButton, deleteButton);
            buttonContainer.setAlignment(Pos.CENTER_RIGHT);

            // Full task container
            HBox fullTaskContainer = new HBox();
            HBox.setHgrow(taskLabelContainer, Priority.ALWAYS);
            fullTaskContainer.getChildren().addAll(taskLabelContainer, buttonContainer);
            fullTaskContainer.setAlignment(Pos.CENTER_LEFT);
            fullTaskContainer.setStyle(
                    "-fx-background-color: #1E1E1E; " +
                            "-fx-padding: 10; " +
                            "-fx-border-radius: 8; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.2, 0, 2);");
            fullTaskContainer.setOnMouseEntered(event -> fullTaskContainer.setStyle(
                    "-fx-background-color: #383838; " +
                            "-fx-padding: 10; " +
                            "-fx-border-radius: 8; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 8, 0.3, 0, 4);"));
            fullTaskContainer.setOnMouseExited(event -> fullTaskContainer.setStyle(
                    "-fx-background-color: #1E1E1E; " +
                            "-fx-padding: 10; " +
                            "-fx-border-radius: 8; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0.2, 0, 2);"));

            // Add drag-and-drop event handlers
            addAnimatedDragAndDropHandlers(dragButton, fullTaskContainer, taskList);

            // Add button actions
            renameButton.setOnAction(ev -> renameTask(taskLabel));
            deleteButton.setOnAction(ev -> deleteTask(taskList, fullTaskContainer));
            starButton.setOnAction(ev -> markTaskAsImportant(taskLabel.getText(), starButton));

            // Add the full task container to the task list
            taskList.getChildren().add(fullTaskContainer);
            taskInput.clear();
        }
    }

    private Button createRoundIconButton(String text, String tooltip) {
        Button button = new Button(tooltip);
        button.setTooltip(new Tooltip(text));
        button.setStyle(
                "-fx-background-color: #555555; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 5 10;");
        button.setOnMouseEntered(event -> button.setStyle(
                "-fx-background-color: #777777; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 5 10; " +
                        "-fx-transition: all 0.3s ease;"));
        button.setOnMouseExited(event -> button.setStyle(
                "-fx-background-color: #555555; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 5 10;"));
        return button;
    }

    private void addAnimatedDragAndDropHandlers(Button dragButton, HBox fullTaskContainer, VBox taskList) {
        dragButton.setOnMousePressed(event -> onDragStart(event, fullTaskContainer, dragButton));
        dragButton.setOnMouseDragged(event -> onDragMove(event, fullTaskContainer));
        dragButton.setOnMouseReleased(event -> onDragEnd(event, fullTaskContainer, taskList, dragButton));
    }

    private void onDragStart(MouseEvent event, HBox fullTaskContainer, Button dragButton) {
        dragButton.getScene().setCursor(Cursor.MOVE);
        // fullTaskContainer.setStyle("-fx-background-color: #666666; -fx-border-style:
        // dashed; -fx-border-color: white;");
        fullTaskContainer.toFront();
    }

    private void onDragMove(MouseEvent event, HBox fullTaskContainer) {
        fullTaskContainer.setTranslateY(event.getSceneY() - fullTaskContainer.getLayoutY());
    }

    private void onDragEnd(MouseEvent event, HBox fullTaskContainer, VBox taskList, Button dragButton) {
        dragButton.getScene().setCursor(Cursor.DEFAULT);

        // Determine the drop location
        double mouseY = event.getSceneY();
        int dropIndex = 0;

        for (int i = 0; i < taskList.getChildren().size(); i++) {
            Node node = taskList.getChildren().get(i);
            if (node.getBoundsInParent().getMaxY() > mouseY) {
                dropIndex = i;
                break;
            }
        }

        // Reorder the task in the list
        taskList.getChildren().remove(fullTaskContainer);
        taskList.getChildren().add(dropIndex, fullTaskContainer);

        // Reset styling
        // fullTaskContainer.setStyle("-fx-background-color: #444444; -fx-padding: 10;
        // -fx-border-radius: 5;");
        fullTaskContainer.setTranslateY(0);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void renameTask(Label taskLabel) {
        TextInputDialog dialog = new TextInputDialog(taskLabel.getText());
        dialog.setTitle("Rename Task");
        dialog.setHeaderText("Enter a new name for the task.");
        dialog.showAndWait().ifPresent(newName -> taskLabel.setText(newName));
    }

    private void deleteTask(VBox taskList, HBox taskLabelContainer) {
        taskList.getChildren().remove(taskLabelContainer);
    }

    // JavaFX.Merged(){}
    private void createNewGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Group");
        dialog.setHeaderText("Create a New Group");
        dialog.setContentText("Group Name:");
        // dialog.getIcons().add(new
        // Image(getClass().getResourceAsStream("/images/icon.png")));
        dialog.showAndWait().ifPresent(groupName -> {
            if (!groupName.isEmpty() && !groups.containsKey(groupName)) {
                Button groupButton = createRoundedButton(groupName);
                groupButton.setOnAction(e -> switchToPanel(groupName));
                groupsContainer.getChildren().add(groupButton);

                VBox groupPanel = new VBox(10);
                groupPanel.setPadding(new Insets(10));
                groupPanel.setStyle("-fx-background-color: #232323;");
                groups.put(groupName, groupPanel);
            }
        });
    }

    private VBox createMyNotes() {

        VBox myNotesPanel = new VBox(10);
        myNotesPanel.setPadding(new Insets(10));
        myNotesPanel.setStyle("-fx-background-color: #232323;"); // Dark background for the panel
        myNotesPanel.setAlignment(Pos.TOP_CENTER);

        // Toolbar
        ToolBar toolBar = createToolbar();

        // Workspace
        workspace = new Pane();
        workspace.setStyle("-fx-background-color: #1E1E1E;"); // Dark background color
        workspace.setPrefSize(800, 600);

        // Color picker
        ColorPicker standardColorPicker = new ColorPicker(Color.BLACK);
        standardColorPicker.setStyle("-fx-color-label-visible: false;");

        // Custom Color Creation Panel
        VBox customColorPanel = new VBox(10);
        customColorPanel.setAlignment(Pos.CENTER);

        Text customColorLabel = new Text("Custom Color Creator");
        customColorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Sliders for RGB values
        Slider redSlider = createColorSlider("Red", 255);
        Slider greenSlider = createColorSlider("Green", 255);
        Slider blueSlider = createColorSlider("Blue", 255);

        // Preview Rectangle
        Rectangle colorPreview = new Rectangle(100, 100, Color.BLACK);
        colorPreview.setStroke(Color.GRAY);
        colorPreview.setStrokeWidth(1);

        // Update preview when sliders change
        redSlider.valueProperty().addListener(
                (obs, oldVal, newVal) -> updatePreviewColor(colorPreview, redSlider, greenSlider, blueSlider));
        greenSlider.valueProperty().addListener(
                (obs, oldVal, newVal) -> updatePreviewColor(colorPreview, redSlider, greenSlider, blueSlider));
        blueSlider.valueProperty().addListener(
                (obs, oldVal, newVal) -> updatePreviewColor(colorPreview, redSlider, greenSlider, blueSlider));

        // Apply Button
        Button applyButton = new Button("Apply Custom Color");
        applyButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white; -fx-padding: 5 10;");
        applyButton.setOnAction(e -> standardColorPicker.setValue((Color) colorPreview.getFill()));

        // Add components to the custom color panel
        customColorPanel.getChildren().addAll(customColorLabel, redSlider, greenSlider, blueSlider, colorPreview,
                applyButton);

        // Combine Standard and Custom Pickers
        HBox pickersContainer = new HBox(30, standardColorPicker, customColorPanel);
        pickersContainer.setAlignment(Pos.CENTER);

        myNotesPanel.getChildren().addAll(toolBar, pickersContainer, workspace);
        return myNotesPanel;

    }

    private ToolBar createToolbar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setStyle("-fx-background-color: #2D2D2D;");

        // Buttons for toolbar (tools)
        Button selectTool = createToolbarButton("Pointer", "Select Tool");
        Button rectangleTool = createToolbarButton("▭", "Draw Rectangle");
        Button circleTool = createToolbarButton("◯", "Draw Circle");
        Button lineTool = createToolbarButton("→", "Draw Line");
        Button textTool = createToolbarButton("A", "Add Text");

        // Add action handlers for tools
        selectTool.setOnAction(e -> enablePointerTool());
        rectangleTool.setOnAction(e -> enableShapeTool("rectangle"));
        circleTool.setOnAction(e -> enableShapeTool("circle"));
        lineTool.setOnAction(e -> enableShapeTool("line"));
        textTool.setOnAction(e -> enableTextTool());

        toolBar.getItems().addAll(selectTool, rectangleTool, circleTool, lineTool, textTool);
        return toolBar;
    }

    private Button createToolbarButton(String label, String tooltip) {
        Button button = new Button(label);
        button.setStyle("-fx-background-radius: 10; -fx-background-color: #444444; -fx-text-fill: white;");
        button.setPrefSize(35, 35); // Fixed size
        button.setFocusTraversable(false);
        return button;
    }

    private void enablePointerTool() {
        // Logic for selecting/moving items (placeholder)
        System.out.println("Pointer tool selected.");
    }

    private void enableShapeTool(String shape) {
        workspace.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Shape newShape = null;

                if (shape.equals("rectangle")) {
                    newShape = new Rectangle(event.getX(), event.getY(), event.getX(), event.getY());
                } else if (shape.equals("circle")) {
                    newShape = new Circle(event.getX(), event.getY(), 30);
                } else if (shape.equals("line")) {
                    Line line = new Line(event.getX(), event.getY(), event.getX() + 50, event.getY());
                    line.setStrokeWidth(2);
                    newShape = line;
                }

                if (newShape != null) {
                    newShape.setFill(null);
                    newShape.setStroke(currentColor);
                    workspace.getChildren().add(newShape);
                }
            }
        });
    }

    private void enableTextTool() {
        workspace.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Text newText = new Text(event.getX(), event.getY(), "Edit Me");
                newText.setFill(currentColor);
                workspace.getChildren().add(newText);

                // Allow text to be draggable
                newText.setOnMouseDragged(dragEvent -> {
                    newText.setX(dragEvent.getX());
                    newText.setY(dragEvent.getY());
                });
            }
        });
    }

    private Slider createColorSlider(String label, int maxValue) {
        Slider slider = new Slider(0, maxValue, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(64);
        slider.setMinorTickCount(4);
        // slider.setStyle("-fx-control-inner-background: #" + label.toLowerCase());
        return slider;
    }

    // Helper method to update preview color
    private void updatePreviewColor(Rectangle preview, Slider red, Slider green, Slider blue) {
        Color color = Color.rgb((int) red.getValue(), (int) green.getValue(), (int) blue.getValue());
        preview.setFill(color);
    }

}
