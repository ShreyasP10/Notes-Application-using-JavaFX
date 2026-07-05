package com.shreyaspawar.ui.components;

import com.shreyaspawar.App;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
        content.setMaxWidth(420);
        overlay.getChildren().clear();
        overlay.getChildren().add(content);
        overlay.setVisible(true);
        overlay.setPickOnBounds(true);

        FadeTransition ft = new FadeTransition(Duration.millis(200), content);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        ScaleTransition st = new ScaleTransition(Duration.millis(200), content);
        st.setFromX(0.92);
        st.setFromY(0.92);
        st.setToX(1);
        st.setToY(1);
        st.play();

        overlay.setOnMouseClicked(e -> closeOverlay());
        content.setOnMouseClicked(e -> e.consume());
    }

    private static void closeOverlay() {
        StackPane overlay = App.getDialogOverlay();
        overlay.setVisible(false);
        overlay.setPickOnBounds(false);
        overlay.getChildren().clear();
    }

    private static VBox createInfoContent(String title, String message) {
        VBox box = new VBox(20);
        box.setPadding(new Insets(28, 28, 24, 28));
        box.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("dialog-title");
        Label msgLbl = new Label(message);
        msgLbl.setWrapText(true);
        msgLbl.setStyle("-fx-font-size: 13px; -fx-line-spacing: 4px;");
        HBox btnBox = new HBox();
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("dialog-ok-btn");
        okBtn.setOnAction(e -> closeOverlay());
        btnBox.getChildren().add(okBtn);
        box.getChildren().addAll(titleLbl, msgLbl, btnBox);
        return box;
    }

    private static VBox createConfirmContent(String title, String message, Runnable onConfirm) {
        VBox box = new VBox(20);
        box.setPadding(new Insets(28, 28, 24, 28));
        box.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("dialog-title");
        Label msgLbl = new Label(message);
        msgLbl.setWrapText(true);
        msgLbl.setStyle("-fx-font-size: 13px;");
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(4, 0, 0, 0));
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
        VBox box = new VBox(20);
        box.setPadding(new Insets(28, 28, 24, 28));
        box.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("dialog-title");
        TextField input = new TextField();
        input.setPromptText(prompt);
        input.setStyle("-fx-background-radius: 10; -fx-padding: 10 14; -fx-font-size: 13px;");
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(4, 0, 0, 0));
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
