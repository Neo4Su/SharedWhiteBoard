package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import remote.WhiteBoardService;

import java.awt.image.BufferedImage;
import java.rmi.RemoteException;


public class WhiteBoardController {

    private WhiteBoardService whiteBoardService;
    private double SMALL = 2;
    private double MEDIUM = 4;
    private double LARGE = 6;

    private double size=SMALL;
    // tools for drawing on the canvas

    //private DrawCommand drawCommand = new DrawCommand();

    @FXML
    private ChoiceBox<String> sizeChoiceBox;

    @FXML
    private Button pencilButton;

    @FXML
    public Canvas canvas;
    public GraphicsContext gc;
    private ToolType currentTool;
    private double startX, startY, endX, endY; // coordinates for drawing shapes
    double eraserSize=SMALL;
    public WritableImage snapshot;


    @FXML
    private ColorPicker colorPicker;
    private Color color;

    @FXML
    private TextField canvasTextInput;

    @FXML
    private void initialize() {
        // set initial color to black
        colorPicker.setValue(Color.BLACK);
        color = colorPicker.getValue();
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(color);
        currentTool = ToolType.PENCIL;

        // set canvas background to white
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        snapshot = canvas.snapshot(null, null);

        // 初始化 ChoiceBox 项
        ObservableList<String> options = FXCollections.observableArrayList(
                "Option 1", "Option 2", "Option 3", "Option 4"  // 示例选项
        );
        sizeChoiceBox.setItems(options);

        // 设置默认选项
        sizeChoiceBox.setValue("Option 1");
    }

    @FXML
    private void canvasMousePressed(MouseEvent event) {


        startX = event.getX();
        startY = event.getY();


//        drawCommand.startX = startX;
//        drawCommand.startY = startY;


        gc.beginPath();
        gc.moveTo(startX, startY);

    }

    @FXML
    private void canvasMouseDragged(MouseEvent event) throws RemoteException {
        endX = event.getX();
        endY = event.getY();



        double shapeX = Math.min(startX, endX), shapeY = Math.min(startY, endY);
        double shapeWidth = Math.abs(endX - startX), shapeHeight = Math.abs(endY - startY);

        // clear the canvas and redraw the snapshot
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(snapshot, 0, 0, canvas.getWidth(), canvas.getHeight());

        // update the canvas
        switch (currentTool) {
            case PENCIL:
                gc.lineTo(endX, endY);
                gc.stroke();

                break;
            case LINE:
                gc.strokeLine(startX, startY, endX, endY);
                break;
            case RECT:
                gc.strokeRect(shapeX, shapeY, shapeWidth, shapeHeight);
                break;
            case OVAL:
                gc.strokeOval(shapeX, shapeY, shapeWidth, shapeHeight);
                break;
            case CIRCLE:
                double radius = Math.max(shapeWidth, shapeHeight) / 2;
                gc.strokeOval(shapeX, shapeY, 2 * radius, 2 * radius);
                break;
//            case ERASER:
//                gc.clearRect(event.getX() - eraserSize / 2,
//                        event.getY() - eraserSize / 2, eraserSize, eraserSize);
//                break;
        }
    }

    @FXML
    private void canvasMouseReleased(MouseEvent event) {
//        if (currentTool == ToolType.TEXT){
//            canvasTextInput.setText(null);
//            canvasTextInput.setLayoutX(event.getX());
//            canvasTextInput.setLayoutY(event.getY());
//            canvasTextInput.setVisible(true);
//        }


        snapshot = canvas.snapshot(null, null);



        whiteBoardService.drawOnCanvas();
    }




    // set the color of shapes
    @FXML
    private void colorPicked(ActionEvent event) {
        color = colorPicker.getValue();
        gc.setStroke(color);
    }


    @FXML
    private void PencilButtonClicked(ActionEvent event) {
        currentTool = ToolType.PENCIL;
    }

    @FXML
    private void CircleButtonClicked(ActionEvent event) {
        currentTool = ToolType.CIRCLE;
    }

    @FXML
    private void LineButtonClicked(ActionEvent event) {
        currentTool = ToolType.LINE;
    }

    @FXML
    private void OvalButtonClicked(ActionEvent event) {
        currentTool = ToolType.OVAL;
    }

    @FXML
    private void RectButtonClicked(ActionEvent event) {
        currentTool = ToolType.RECT;
    }

    @FXML
    void textButtonClicked(ActionEvent event) {
        currentTool = ToolType.TEXT;
    }

    @FXML
    private void eraserButtonClicked(ActionEvent event) {
        currentTool = ToolType.ERASER;
    }

    @FXML
    private void handleClose(ActionEvent event) {

    }

    @FXML
    private void handleKickOut(ActionEvent event) {

    }

    @FXML
    private void handleNew(ActionEvent event) {

    }

    @FXML
    private void handleOpen(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

    @FXML
    void handleSaveAs(ActionEvent event) {

    }

    public void setWhiteBoardService(WhiteBoardService whiteBoardService) {
        this.whiteBoardService = whiteBoardService;
    }
}