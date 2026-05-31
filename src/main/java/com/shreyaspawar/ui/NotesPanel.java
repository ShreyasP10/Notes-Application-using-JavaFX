package com.shreyaspawar.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class NotesPanel extends HBox {
    private Pane canvasContainer;
    private Canvas canvas;
    private GraphicsContext gc;
    private String currentTool = "pointer";
    private Color currentColor = Color.BLACK;
    private final VBox layersPanel;

    public NotesPanel() {
        getStyleClass().add("notes-panel");
        setSpacing(15);
        setPadding(new Insets(15));

        // Toolbar
        ToolBar toolbar = new ToolBar();
        toolbar.getStyleClass().add("notes-toolbar");
        String[] tools = {"🖱 pointer", "✏ pencil", "▭ rect", "◯ circle", "→ line", "A text", "📌 sticky"};
        for (String t : tools) {
            Button btn = new Button(t);
            btn.getStyleClass().add("tool-btn");
            btn.setOnAction(e -> currentTool = t.split(" ")[1]);
            toolbar.getItems().add(btn);
        }
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setOnAction(e -> currentColor = colorPicker.getValue());
        toolbar.getItems().add(colorPicker);

        Button exportBtn = new Button("Export PNG");
        exportBtn.getStyleClass().add("tool-btn");
        exportBtn.setOnAction(e -> exportCanvas());
        toolbar.getItems().add(exportBtn);

        // Canvas container
        canvasContainer = new Pane();
        canvas = new Canvas(700, 500);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvasContainer.getChildren().add(canvas);

        canvasContainer.setOnMousePressed(e -> {
            if ("pencil".equals(currentTool)) {
                gc.setStroke(currentColor);
                gc.setLineWidth(2);
                gc.beginPath();
                gc.moveTo(e.getX(), e.getY());
            }
        });
        canvasContainer.setOnMouseDragged(e -> {
            if ("pencil".equals(currentTool)) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        });
        canvasContainer.setOnMouseClicked(e -> {
            if ("pointer".equals(currentTool) || "pencil".equals(currentTool)) return;
            double x = e.getX(), y = e.getY();
            if ("sticky".equals(currentTool)) {
                addStickyNote(x, y);
            } else {
                addShape(x, y);
            }
        });

        // Layers panel
        layersPanel = new VBox(10);
        layersPanel.getStyleClass().add("layers-panel");
        layersPanel.setPrefWidth(160);
        Label layersTitle = new Label("Layers");
        layersTitle.getStyleClass().add("panel-title");
        layersPanel.getChildren().add(layersTitle);

        getChildren().addAll(new VBox(10, toolbar, canvasContainer), layersPanel);
    }

    private void addStickyNote(double x, double y) {
        StackPane sticky = new StackPane();
        sticky.setLayoutX(x);
        sticky.setLayoutY(y);
        sticky.setPrefSize(140, 100);
        sticky.getStyleClass().add("sticky-note");
        TextArea text = new TextArea("Note...");
        text.setWrapText(true);
        text.setPrefSize(120, 80);
        text.getStyleClass().add("sticky-text");
        sticky.getChildren().add(text);
        canvasContainer.getChildren().add(sticky);
        makeDraggable(sticky);
        addLayer("Sticky Note");
    }

    private void addShape(double x, double y) {
        Node shape = null;
        switch (currentTool) {
            case "rect" -> shape = new Rectangle(x, y, 100, 60);
            case "circle" -> shape = new Circle(x, y, 40);
            case "line" -> {
                Line l = new Line(x, y, x + 80, y);
                l.setStroke(currentColor);
                shape = l;
            }
            case "text" -> {
                Text t = new Text(x, y, "Edit me");
                t.setFill(currentColor);
                shape = t;
            }
        }
        if (shape != null) {
            if (shape instanceof Shape s) {
                s.setFill(null);
                s.setStroke(currentColor);
                s.setStrokeWidth(2);
            }
            canvasContainer.getChildren().add(shape);
            makeDraggable(shape);
            addLayer(currentTool.substring(0, 1).toUpperCase() + currentTool.substring(1));
        }
    }

    private void makeDraggable(Node node) {
        final double[] offset = new double[2];
        node.setOnMousePressed(e -> {
            offset[0] = e.getSceneX() - node.getLayoutX();
            offset[1] = e.getSceneY() - node.getLayoutY();
        });
        node.setOnMouseDragged(e -> {
            node.setLayoutX(e.getSceneX() - offset[0]);
            node.setLayoutY(e.getSceneY() - offset[1]);
        });
    }

    private void addLayer(String name) {
        Label layerItem = new Label(name);
        layerItem.getStyleClass().add("layer-item");
        layersPanel.getChildren().add(layerItem);
    }

    private void exportCanvas() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            WritableImage image = canvasContainer.snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}