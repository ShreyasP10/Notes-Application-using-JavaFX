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
    private final double expandedWidth = 240;
    private final double collapsedWidth = 64;
    private final VBox itemsBox = new VBox(6);
    private final Button toggleBtn;
    private final List<HBox> itemContainers = new ArrayList<>();
    private final Label username;

    public interface PanelSwitchListener {
        void switchTo(String panelName);
    }
    private PanelSwitchListener listener;

    public Sidebar() {
        getStyleClass().add("sidebar");
        setPadding(new Insets(20, 12, 12, 12));
        setPrefWidth(expandedWidth);
        setSpacing(8);

        HBox profile = new HBox(12);
        profile.setAlignment(Pos.CENTER_LEFT);
        profile.setPadding(new Insets(0, 4, 0, 4));
        Region avatar = new Region();
        avatar.getStyleClass().add("avatar");
        username = new Label("Guest");
        username.getStyleClass().add("sidebar-username");
        profile.getChildren().addAll(avatar, username);

        Separator sep = new Separator();

        toggleBtn = new Button("\u2630");
        toggleBtn.getStyleClass().add("sidebar-toggle");
        toggleBtn.setMaxWidth(Double.MAX_VALUE);
        toggleBtn.setAlignment(Pos.CENTER_LEFT);
        toggleBtn.setPadding(new Insets(8, 8, 8, 8));
        toggleBtn.setOnAction(e -> toggleDrawer());

        itemsBox.setPadding(new Insets(4, 0, 0, 0));
        VBox.setVgrow(itemsBox, Priority.ALWAYS);

        getChildren().addAll(profile, sep, toggleBtn, itemsBox);

        addItem("My Day", "\uD83D\uDCCB");
        addItem("My Notes", "\uD83D\uDCDD");
        addItem("Important", "\u2B50");
        addItem("Settings", "\u2699");

        itemContainers.get(0).getStyleClass().add("sidebar-item-active");
    }

    public void setPanelSwitchListener(PanelSwitchListener listener) {
        this.listener = listener;
    }

    private void addItem(String text, String icon) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 14, 10, 14));
        item.getStyleClass().add("sidebar-item");
        if (!expanded) {
            item.setPadding(new Insets(10, 14, 10, 14));
        } else {
            item.setPadding(new Insets(10, 14, 10, 14));
        }
        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("sidebar-icon");
        iconLabel.setMinWidth(28);
        iconLabel.setAlignment(Pos.CENTER);
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

        if (expanded) {
            setPrefWidth(collapsedWidth);
            animateWidth(target);
            for (Node node : itemsBox.getChildren()) {
                if (node instanceof HBox item && item.getChildren().size() > 1) {
                    Node label = item.getChildren().get(1);
                    item.setPadding(new Insets(10, 14, 10, 14));
                    label.setManaged(true);
                    label.setVisible(true);
                    FadeTransition ft = new FadeTransition(Duration.millis(200), label);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                }
            }
        } else {
            for (Node node : itemsBox.getChildren()) {
                if (node instanceof HBox item && item.getChildren().size() > 1) {
                    Node label = item.getChildren().get(1);
                    FadeTransition ft = new FadeTransition(Duration.millis(150), label);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(e -> {
                        label.setManaged(false);
                        label.setVisible(false);
                    });
                    ft.play();
                }
            }
            animateWidth(target);
        }
    }

    private void animateWidth(double target) {
        Timeline t = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(prefWidthProperty(), getWidth())),
            new KeyFrame(Duration.millis(250),
                new KeyValue(prefWidthProperty(), target, Interpolator.EASE_BOTH))
        );
        t.play();
    }
}
