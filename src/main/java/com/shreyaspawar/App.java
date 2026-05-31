package com.shreyaspawar;

import com.shreyaspawar.theme.ThemeManager;
import com.shreyaspawar.ui.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;
import java.util.Map;

public class App extends Application {
    private StackPane mainContent;
    private Sidebar sidebar;
    private final Map<String, Pane> panelCache = new HashMap<>();
    private static StackPane dialogOverlay;

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/icon.png")));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        TitleBar titleBar = new TitleBar(stage);
        root.setTop(titleBar);

        sidebar = new Sidebar();
        sidebar.setPanelSwitchListener(this::switchPanel);
        root.setLeft(sidebar);

        // Main content + overlay for dialogs
        StackPane centerStack = new StackPane();
        mainContent = new StackPane();
        mainContent.getStyleClass().add("main-content");
        dialogOverlay = new StackPane();
        dialogOverlay.setVisible(false);
        dialogOverlay.setPickOnBounds(false);
        dialogOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        centerStack.getChildren().addAll(mainContent, dialogOverlay);
        root.setCenter(centerStack);

        Scene scene = new Scene(root, 1100, 750);
        scene.setFill(null);
        ThemeManager.registerScene(scene);
        stage.setScene(scene);
        stage.show();

        switchPanel("My Day");
    }

    private void switchPanel(String name) {
        mainContent.getChildren().clear();
        Pane panel = panelCache.computeIfAbsent(name, this::createPanel);
        mainContent.getChildren().add(panel);
        dialogOverlay.toFront();
    }

    private Pane createPanel(String name) {
        return switch (name) {
            case "My Day" -> new TaskPanel("My Day");
            case "My Notes" -> new NotesPanel();
            case "Important" -> new TaskPanel("Important", task -> task.isImportant());
            case "Settings" -> createSettingsPanel();
            default -> new StackPane(new Label("Panel not found"));
        };
    }

    private VBox createSettingsPanel() {
        VBox settings = new VBox(20);
        settings.getStyleClass().add("settings-panel");
        settings.setPadding(new Insets(30));
        Label title = new Label("Settings");
        title.getStyleClass().add("panel-title");

        Button darkBtn = new Button("Dark Theme");
        darkBtn.getStyleClass().add("settings-btn");
        darkBtn.setOnAction(e -> ThemeManager.setTheme(ThemeManager.Theme.DARK));

        Button lightBtn = new Button("Light Theme");
        lightBtn.getStyleClass().add("settings-btn");
        lightBtn.setOnAction(e -> ThemeManager.setTheme(ThemeManager.Theme.LIGHT));

        settings.getChildren().addAll(title, darkBtn, lightBtn);
        return settings;
    }

    public static StackPane getDialogOverlay() {
        return dialogOverlay;
    }

    public static void main(String[] args) {
        launch(args);
    }
}