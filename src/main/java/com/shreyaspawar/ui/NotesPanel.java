package com.shreyaspawar.ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private String currentTool = "pencil";
    private Color currentColor = Color.BLACK;
    private final VBox layersPanel;
    private boolean pencilDrew = false;

    public NotesPanel() {
        getStyleClass().add("notes-panel");
        setSpacing(16);
        setPadding(new Insets(20));

        VBox editorArea = new VBox(12);
        editorArea.setPrefWidth(600);
        HBox.setHgrow(editorArea, Priority.ALWAYS);

        ToolBar toolbar = new ToolBar();
        toolbar.getStyleClass().add("notes-toolbar");
        String[][] tools = {
            {"\u270F", "pencil"},
            {"\u25AD", "rect"},
            {"\u25EF", "circle"},
            {"\u2192", "line"},
            {"A", "text"},
            {"\uD83D\uDCCC", "sticky"}
        };
        ToggleGroup toolGroup = new ToggleGroup();
        for (String[] t : tools) {
            ToggleButton btn = new ToggleButton(t[0]);
            btn.getStyleClass().add("tool-btn");
            btn.setTooltip(new Tooltip(t[1]));
            btn.setToggleGroup(toolGroup);
            btn.setUserData(t[1]);
            btn.setOnAction(e -> currentTool = (String) btn.getUserData());
            toolbar.getItems().add(btn);
        }
        toolGroup.getToggles().get(0).setSelected(true);

        toolbar.getItems().add(new Separator());
        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setPrefWidth(40);
        colorPicker.setOnAction(e -> currentColor = colorPicker.getValue());
        toolbar.getItems().add(colorPicker);

        toolbar.getItems().add(new Separator());
        Button undoBtn = new Button("\u21A9");
        undoBtn.getStyleClass().add("tool-btn");
        undoBtn.setTooltip(new Tooltip("Clear canvas"));
        undoBtn.setOnAction(e -> gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()));
        toolbar.getItems().add(undoBtn);

        Button exportBtn = new Button("Export PNG");
        exportBtn.getStyleClass().add("tool-btn");
        exportBtn.setTooltip(new Tooltip("Save as PNG"));
        exportBtn.setOnAction(e -> exportCanvas());
        toolbar.getItems().add(exportBtn);

        canvasContainer = new Pane();
        canvasContainer.getStyleClass().add("notes-canvas");
        canvas = new Canvas();
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());
        gc = canvas.getGraphicsContext2D();
        canvas.widthProperty().addListener((obs, old, w) -> clearCanvas());
        canvas.heightProperty().addListener((obs, old, h) -> clearCanvas());
        clearCanvas();
        canvasContainer.getChildren().add(canvas);
        VBox.setVgrow(canvasContainer, Priority.ALWAYS);

        canvasContainer.setOnMousePressed(e -> {
            if ("pencil".equals(currentTool)) {
                pencilDrew = false;
                gc.setStroke(currentColor);
                gc.setLineWidth(2);
                gc.beginPath();
                gc.moveTo(e.getX(), e.getY());
            }
        });
        canvasContainer.setOnMouseDragged(e -> {
            if ("pencil".equals(currentTool)) {
                pencilDrew = true;
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        });
        canvasContainer.setOnMouseReleased(e -> {
            if ("pencil".equals(currentTool) && !pencilDrew) {
                gc.beginPath();
            }
        });
        canvasContainer.setOnMouseClicked(e -> {
            if ("pencil".equals(currentTool)) return;
            double x = e.getX(), y = e.getY();
            if ("sticky".equals(currentTool)) {
                addStickyNote(x, y);
            } else {
                addShape(x, y);
            }
        });

        editorArea.getChildren().addAll(toolbar, canvasContainer);

        layersPanel = new VBox(10);
        layersPanel.getStyleClass().add("layers-panel");
        layersPanel.setPrefWidth(180);
        Label layersTitle = new Label("Layers");
        layersTitle.getStyleClass().add("panel-title");
        layersTitle.setStyle("-fx-font-size: 16px;");
        Label layersHint = new Label("Shapes & notes appear here");
        layersHint.setStyle("-fx-font-size: 11px; -fx-opacity: 0.5;");
        layersPanel.getChildren().addAll(layersTitle, layersHint);

        getChildren().addAll(editorArea, layersPanel);
    }

    private void clearCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void addStickyNote(double x, double y) {
        StackPane sticky = new StackPane();
        sticky.setLayoutX(x);
        sticky.setLayoutY(y);
        sticky.setPrefSize(150, 110);
        sticky.getStyleClass().add("sticky-note");
        TextArea text = new TextArea("Note...");
        text.setWrapText(true);
        text.setPrefSize(130, 90);
        text.getStyleClass().add("sticky-text");
        sticky.getChildren().add(text);
        canvasContainer.getChildren().add(sticky);
        makeDraggable(sticky);
        addLayer("Sticky Note");
    }

    private void addShape(double x, double y) {
        Node shape = null;
        String layerName = "";
        switch (currentTool) {
            case "rect" -> {
                shape = new Rectangle(x, y, 100, 60);
                layerName = "Rectangle";
            }
            case "circle" -> {
                shape = new Circle(x, y, 40);
                layerName = "Circle";
            }
            case "line" -> {
                Line l = new Line(x, y, x + 80, y);
                l.setStroke(currentColor);
                shape = l;
                layerName = "Line";
            }
            case "text" -> {
                Text t = new Text(x, y, "Edit me");
                t.setFill(currentColor);
                t.setStyle("-fx-font-size: 16px;");
                shape = t;
                layerName = "Text";
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
            addLayer(layerName);
        }
    }

    private void makeDraggable(Node node) {
        final double[] offset = new double[2];
        node.setOnMousePressed(e -> {
            offset[0] = e.getSceneX() - node.getLayoutX();
            offset[1] = e.getSceneY() - node.getLayoutY();
            node.toFront();
        });
        node.setOnMouseDragged(e -> {
            node.setLayoutX(e.getSceneX() - offset[0]);
            node.setLayoutY(e.getSceneY() - offset[1]);
        });
    }

    private void addLayer(String name) {
        HBox layerItem = new HBox(8);
        layerItem.setAlignment(Pos.CENTER_LEFT);
        layerItem.getStyleClass().add("layer-item");
        Label dot = new Label("\u25CF");
        dot.setStyle("-fx-font-size: 8px; -fx-text-fill: #4f6ef7;");
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 12px;");
        layerItem.getChildren().addAll(dot, nameLabel);
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
