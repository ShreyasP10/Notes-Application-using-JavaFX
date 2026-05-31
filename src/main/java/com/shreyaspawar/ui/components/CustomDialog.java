package com.shreyaspawar.ui.components;

import com.shreyaspawar.App;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class CustomDialog {

    public static void showInfo(String title, String message) {
        showOverlay(createInfoContent(title, message));
    }

    public static void showConfirm(String title, String message, Runnable onConfirm) {
        showOverlay(createConfirmContent(title, message, onConfirm));
    }

    public static void showTextInput(String title, String prompt, TextInputCallback callback) {
        showOverlay(createInputContent(title, prompt, callback));
    }

    public interface TextInputCallback {
        void onResult(String text);
    }

    private static void showOverlay(VBox content) {
        StackPane overlay = App.getDialogOverlay();
        content.getStyleClass().add("dialog-card");
        content.setMaxWidth(400);
        overlay.getChildren().clear();
        overlay.getChildren().add(content);
        overlay.setVisible(true);
        overlay.setPickOnBounds(true);

        FadeTransition ft = new FadeTransition(Duration.millis(200), content);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private static void closeOverlay() {
        StackPane overlay = App.getDialogOverlay();
        overlay.setVisible(false);
        overlay.setPickOnBounds(false);
        overlay.getChildren().clear();
    }

    private static VBox createInfoContent(String title, String message) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("dialog-title");
        Label msgLbl = new Label(message);
        msgLbl.setWrapText(true);
        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("dialog-ok-btn");
        okBtn.setOnAction(e -> closeOverlay());
        box.getChildren().addAll(titleLbl, msgLbl, okBtn);
        return box;
    }

    private static VBox createConfirmContent(String title, String message, Runnable onConfirm) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("dialog-title");
        Label msgLbl = new Label(message);
        msgLbl.setWrapText(true);
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("dialog-cancel-btn");
        cancelBtn.setOnAction(e -> closeOverlay());
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.setOnAction(e -> { closeOverlay(); onConfirm.run(); });
        buttons.getChildren().addAll(cancelBtn, deleteBtn);
        box.getChildren().addAll(titleLbl, msgLbl, buttons);
        return box;
    }

    private static VBox createInputContent(String title, String prompt, TextInputCallback callback) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("dialog-title");
        TextField input = new TextField();
        input.setPromptText(prompt);
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("dialog-cancel-btn");
        cancelBtn.setOnAction(e -> closeOverlay());
        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("dialog-ok-btn");
        okBtn.setOnAction(e -> { closeOverlay(); callback.onResult(input.getText()); });
        buttons.getChildren().addAll(cancelBtn, okBtn);
        box.getChildren().addAll(titleLbl, input, buttons);
        return box;
    }
}