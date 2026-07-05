package com.shreyaspawar;

import com.shreyaspawar.theme.ThemeManager;
import com.shreyaspawar.ui.*;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
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

        var iconStream = getClass().getResourceAsStream("/images/icon.png");
        if (iconStream != null) {
            stage.getIcons().add(new javafx.scene.image.Image(iconStream));
        }

        String systemTheme = SystemThemeDetector.getSystemTheme();
        if ("dark".equals(systemTheme)) {
            ThemeManager.setTheme(ThemeManager.Theme.DARK);
        } else {
            ThemeManager.setTheme(ThemeManager.Theme.LIGHT);
        }

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        TitleBar titleBar = new TitleBar(stage);
        root.setTop(titleBar);

        sidebar = new Sidebar();
        sidebar.setPanelSwitchListener(this::switchPanel);
        root.setLeft(sidebar);

        StackPane centerStack = new StackPane();
        mainContent = new StackPane();
        mainContent.getStyleClass().add("main-content");
        dialogOverlay = new StackPane();
        dialogOverlay.setVisible(false);
        dialogOverlay.setPickOnBounds(false);
        dialogOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
        centerStack.getChildren().addAll(mainContent, dialogOverlay);
        root.setCenter(centerStack);

        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(null);
        ThemeManager.registerScene(scene);
        stage.setScene(scene);
        stage.show();

        switchPanel("My Day");
    }

    private void switchPanel(String name) {
        Pane panel = panelCache.computeIfAbsent(name, this::createPanel);
        panel.setOpacity(0);
        mainContent.getChildren().clear();
        mainContent.getChildren().add(panel);

        FadeTransition ft = new FadeTransition(Duration.millis(200), panel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

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
        VBox settings = new VBox(24);
        settings.getStyleClass().add("settings-panel");
        settings.setPadding(new Insets(40, 48, 40, 48));

        VBox appearanceSection = new VBox(16);
        appearanceSection.getStyleClass().add("settings-section");
        Label sectionTitle = new Label("Appearance");
        sectionTitle.getStyleClass().add("settings-section-title");
        Label sectionDesc = new Label("Choose your preferred theme");
        sectionDesc.setStyle("-fx-font-size: 12px; -fx-opacity: 0.6;");

        HBox themeButtons = new HBox(12);
        Button darkBtn = new Button("\uD83C\uDF19 Dark Theme");
        darkBtn.getStyleClass().add("settings-btn");
        darkBtn.setOnAction(e -> ThemeManager.setTheme(ThemeManager.Theme.DARK));

        Button lightBtn = new Button("\u2600\uFE0F Light Theme");
        lightBtn.getStyleClass().add("settings-btn");
        lightBtn.setOnAction(e -> ThemeManager.setTheme(ThemeManager.Theme.LIGHT));

        Button systemBtn = new Button("\uD83D\uDCF1 System");
        systemBtn.getStyleClass().add("settings-btn");
        systemBtn.setOnAction(e -> {
            String systemTheme = SystemThemeDetector.getSystemTheme();
            if ("dark".equals(systemTheme)) {
                ThemeManager.setTheme(ThemeManager.Theme.DARK);
            } else {
                ThemeManager.setTheme(ThemeManager.Theme.LIGHT);
            }
        });

        themeButtons.getChildren().addAll(lightBtn, darkBtn, systemBtn);
        appearanceSection.getChildren().addAll(sectionTitle, sectionDesc, themeButtons);

        VBox aboutSection = new VBox(12);
        aboutSection.getStyleClass().add("settings-section");
        Label aboutTitle = new Label("About");
        aboutTitle.getStyleClass().add("settings-section-title");
        Label version = new Label("Notes App v1.0");
        version.setStyle("-fx-font-size: 12px; -fx-opacity: 0.6;");

        aboutSection.getChildren().addAll(aboutTitle, version);

        settings.getChildren().addAll(appearanceSection, new Separator(), aboutSection);
        return settings;
    }

    public static StackPane getDialogOverlay() {
        return dialogOverlay;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
