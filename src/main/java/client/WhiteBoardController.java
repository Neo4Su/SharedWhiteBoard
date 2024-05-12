package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import remote.WhiteBoardService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import static java.lang.System.exit;

//TODO: 1.eraser 2.text
//
public class WhiteBoardController {

    private String userType;
    private String currentUsername;
    private WhiteBoardService whiteBoardService;
    private double SMALL = 2;
    private double MEDIUM = 4;
    private double LARGE = 6;

    private double size=SMALL;
    // tools for drawing on the canvas

    //private DrawCommand drawCommand = new DrawCommand();
    Stage stage;
    Scene scene;


    @FXML
    private ChoiceBox<String> sizeChoiceBox;

    @FXML
    private TextArea chatTextArea;

    @FXML
    private TextField sendTextField;

    @FXML
    private Button sendButton;



    @FXML
    private Button pencilButton;


    ObservableList<String> userList = FXCollections.observableArrayList();
    @FXML
    ListView<String> userListView;

    private File currentFile=null;

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private ToolType currentTool;
    private double startX, startY, endX, endY; // coordinates for drawing shapes
    double eraserSize=SMALL;


    private WritableImage snapshot;


    @FXML
    private ColorPicker colorPicker;
    private Color color;

    @FXML
    MenuBar menuBar;

    @FXML
    private TextField canvasTextInput;

    @FXML
    private void initialize() throws RemoteException {
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

    public void initializeUserList() throws RemoteException {

        userList = FXCollections.observableArrayList(whiteBoardService.getUserList());
        userListView.setItems(userList);
    }
    public void initializeChat() throws RemoteException {
        ArrayList<String> messages = whiteBoardService.getPastMessages();
        for(String message: messages){
            chatTextArea.appendText(message + "\n");
        }
    }

    public void initializeIdentity(String identity) throws RemoteException {
        if (identity.equals("Manager")) {
            userType = "Manager";
        }else if (identity.equals("Client")) {
            menuBar.setManaged(false);
            userType = "Client";

        }else {
            System.out.println("Invalid identity");
            exit(0);
        }
    }







    @FXML
    private void canvasMousePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();


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
    private void canvasMouseReleased(MouseEvent event) throws RemoteException {

        snapshot = canvas.snapshot(null, null);
        whiteBoardService.drawOnCanvas(ImageBytesConverter.ImageToBytes(snapshot));
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
    private void handleSendMessage(){
        String message = sendTextField.getText().trim();
        if (!message.isEmpty()) {
            try {
                if (userType.equals("Manager")) {
                    whiteBoardService.sendMessage(currentUsername+"(manager)" + ": " + message);
                } else {
                    whiteBoardService.sendMessage(currentUsername + ": " + message);
                }

                sendTextField.setText(""); // 清空输入框
            } catch (RemoteException e) {
                e.printStackTrace(); // 处理远程调用异常
            }
        }
    }


    @FXML
    private void handleNew(ActionEvent event) {
        // set canvas background to white
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        snapshot = canvas.snapshot(null, null);
    }

    @FXML
    private void handleOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );


        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());


        if (file != null) {
            try {

                BufferedImage bufferedImage = ImageIO.read(file);
                snapshot = SwingFXUtils.toFXImage(bufferedImage, null);
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.drawImage(snapshot, 0, 0, canvas.getWidth(), canvas.getHeight());

                currentFile = file;
            } catch (IOException ex) {
                ex.printStackTrace();
                // 可以在这里添加错误处理逻辑，如弹出一个对话框通知用户文件读取失败
            }
        }

    }

    @FXML
    void handleSave(ActionEvent event) {
        if (currentFile == null) {
            handleSaveAs(event); // 如果没有当前文件，调用 Save As
        } else {
            try {
                // 直接保存到当前文件
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", currentFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                // 可以在这里添加错误处理逻辑，如弹出一个对话框通知用户保存失败
            }
        }

    }

    @FXML
    void handleSaveAs(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialFileName("untitled");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file != null) {

            try {
                // save current canvas to the file
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                currentFile = file;
            } catch (IOException ex) {
                ex.printStackTrace();
                // 这里可以添加错误处理逻辑，如弹出一个对话框通知用户保存失败
            }
        }
    }



    public void setupCloseHandler(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            event.consume();  // 防止窗口立即关闭，以便完成必要的清理操作
            try {
                handleClose(null);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // remove current user from the whiteboard when closing the window
    // if the user is a manager, inform all clients to exit
    @FXML
    private void handleClose(ActionEvent event) throws RemoteException {
        Stage stage = (Stage) canvas.getScene().getWindow();
        whiteBoardService.userQuit(currentUsername, userType.equals("Manager"));
        stage.close();
        System.exit(0);
    }

    // manager kicks a client
    @FXML
    private void handleKickClient(ActionEvent event) {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null || selectedUser.equals(currentUsername)  ) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Select a client to kick", ButtonType.OK);
            alert.setTitle("Message");
            alert.setHeaderText(null);
            alert.initOwner(canvas.getScene().getWindow());
            alert.showAndWait();
            return;
        }

        if (selectedUser != null) {
            try {
                whiteBoardService.kickUser(selectedUser);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    // Update user list after kick
    public void kickUser(String username) {
        // show message and exit the program if the kicked user is the current user
        if (currentUsername.equals(username)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "You have been kicked out by the manager.", ButtonType.OK);
            alert.setTitle("Message");
            alert.setHeaderText(null);
            alert.initOwner(canvas.getScene().getWindow());

            alert.setOnCloseRequest(event -> {
                System.exit(0);
            });

            alert.showAndWait();
        }else {
            userList.remove(username);
        }
    }





    public void setWhiteBoardService(WhiteBoardService whiteBoardService) {
        this.whiteBoardService = whiteBoardService;
    }


    public WritableImage getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(WritableImage snapshot) {
        this.snapshot = snapshot;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public void setGc(GraphicsContext gc) {
        this.gc = gc;
    }

    public void addNewUser(String newUser) {
        userList.add(newUser);
    }

    public void removeUser(String username) {
        userList.remove(username);
    }

    public void setCurrentUser(String username) {
        currentUsername = username;
    }

    public void updateChat(String message) {
        chatTextArea.appendText(message + "\n");
    }


    public void notifyBoardClosed() {
        if (userType.equals("Manager")) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "The whiteboard has been closed by the manager.", ButtonType.OK);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.initOwner(canvas.getScene().getWindow());

        alert.setOnCloseRequest(event -> {
            System.exit(0);
        });

        alert.showAndWait();
    }

    public void askJoin(String username) throws RemoteException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                username + " wants to join the whiteboard. Do you agree?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.initOwner(canvas.getScene().getWindow());
        alert.showAndWait();

        whiteBoardService.judgeJoinRequest(username, alert.getResult() == ButtonType.YES);

    }

    public void notifyJoinApproval(boolean isApproved) {
        if (isApproved) {
            displayMainStage();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Join Approved");
            alert.setHeaderText(null);
            alert.setContentText("You have been approved to join the whiteboard!");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Join Denied");
            alert.setHeaderText(null);
            alert.setContentText("You have been denied to join the whiteboard.");
            alert.setOnCloseRequest(event -> System.exit(0));
            alert.show();
        }
    }


    public void displayMainStage() {
        stage.setTitle("WhiteBoard (Client)"); // displayed in window's title bar
        stage.setScene(scene);
        stage.show();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}