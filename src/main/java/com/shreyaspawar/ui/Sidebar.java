package com.shreyaspawar.ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.*;

public class Sidebar extends VBox {
    private boolean expanded = true;
    private final double expandedWidth = 230;
    private final double collapsedWidth = 60;
    private final VBox itemsBox = new VBox(8);
    private final Button toggleBtn;
    private final List<HBox> itemContainers = new ArrayList<>();

    public interface PanelSwitchListener {
        void switchTo(String panelName);
    }
    private PanelSwitchListener listener;

    public Sidebar() {
        getStyleClass().add("sidebar");
        setPadding(new Insets(15, 10, 10, 10));
        setPrefWidth(expandedWidth);

        // Profile
        HBox profile = new HBox(10);
        profile.setAlignment(Pos.CENTER_LEFT);
        Region avatar = new Region();
        avatar.getStyleClass().add("avatar");
        Label username = new Label("Guest");
        username.getStyleClass().add("sidebar-username");
        profile.getChildren().addAll(avatar, username);

        toggleBtn = new Button("☰");
        toggleBtn.getStyleClass().add("sidebar-toggle");
        toggleBtn.setMaxWidth(Double.MAX_VALUE);
        toggleBtn.setOnAction(e -> toggleDrawer());

        itemsBox.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(itemsBox, Priority.ALWAYS);

        getChildren().addAll(profile, new Separator(), toggleBtn, itemsBox);

        addItem("My Day", "📋");
        addItem("My Notes", "📝");
        addItem("Important", "⭐");
        addItem("Settings", "⚙");
    }

    public void setPanelSwitchListener(PanelSwitchListener listener) {
        this.listener = listener;
    }

    private void addItem(String text, String icon) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.getStyleClass().add("sidebar-item");
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("sidebar-icon");
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("sidebar-label");
        textLabel.setManaged(expanded);
        textLabel.setVisible(expanded);
        item.getChildren().addAll(iconLabel, textLabel);
        item.setOnMouseClicked(e -> {
            if (listener != null) listener.switchTo(text);
            itemContainers.forEach(i -> i.getStyleClass().remove("sidebar-item-active"));
            item.getStyleClass().add("sidebar-item-active");
        });
        itemsBox.getChildren().add(item);
        itemContainers.add(item);
    }

    private void toggleDrawer() {
        expanded = !expanded;
        double target = expanded ? expandedWidth : collapsedWidth;
        Timeline t = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(prefWidthProperty(), getWidth())),
            new KeyFrame(Duration.millis(250),
                new KeyValue(prefWidthProperty(), target, Interpolator.EASE_BOTH))
        );
        t.setOnFinished(e -> {
            for (Node node : itemsBox.getChildren()) {
                if (node instanceof HBox item && item.getChildren().size() > 1) {
                    Node label = item.getChildren().get(1);
                    label.setManaged(expanded);
                    label.setVisible(expanded);
                }
            }
        });
        t.play();
    }
}