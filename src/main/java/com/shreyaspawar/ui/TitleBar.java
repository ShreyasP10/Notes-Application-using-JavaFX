package com.shreyaspawar.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TitleBar extends HBox {
    private double xOffset = 0, yOffset = 0;

    public TitleBar(Stage stage) {
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("title-bar");
        setPrefHeight(44);

        ImageView icon = new ImageView();
        var iconStream = getClass().getResourceAsStream("/images/icon.png");
        if (iconStream != null) {
            icon.setImage(new Image(iconStream));
        }
        icon.setFitWidth(18);
        icon.setFitHeight(18);
        Label appName = new Label("Notes App");
        appName.getStyleClass().add("title-bar-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimizeBtn = new Button("\u2014");
        minimizeBtn.getStyleClass().add("window-button");
        minimizeBtn.setOnAction(e -> stage.setIconified(true));

        Button maximizeBtn = new Button("\u25A1");
        maximizeBtn.getStyleClass().add("window-button");
        maximizeBtn.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));

        Button closeBtn = new Button("\u2715");
        closeBtn.getStyleClass().addAll("window-button", "close-button");
        closeBtn.setOnAction(e -> stage.close());

        getChildren().addAll(icon, appName, spacer, minimizeBtn, maximizeBtn, closeBtn);

        setOnMousePressed(e -> { xOffset = e.getSceneX(); yOffset = e.getSceneY(); });
        setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }
}
