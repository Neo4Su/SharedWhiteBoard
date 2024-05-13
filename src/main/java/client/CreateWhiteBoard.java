package client;//Painter.java


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import remote.ClientService;
import remote.WhiteBoardService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;

public class CreateWhiteBoard extends Application {
    private static String username;
    private static String hostname;
    private static int port;
    private static WhiteBoardService whiteBoardService;
    private static ClientService clientService;

    // set gui of the whiteboard
    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(Locale.US);
        FXMLLoader fxmlLoader = new FXMLLoader(CreateWhiteBoard.class.getResource("/Whiteboard.fxml"));
        Parent root = fxmlLoader.load();


        WhiteBoardController whiteBoardController = fxmlLoader.getController();

        // initialize the whiteboard controller
        whiteBoardController.setWhiteBoardService(whiteBoardService);
        whiteBoardController.initializeIdentity("Manager");
        whiteBoardController.setCurrentUser(username);
        whiteBoardController.initializeUserList();
        whiteBoardController.initializeChat();

        // register the manager
        clientService = new ClientServiceImpl(whiteBoardController);
        if (!whiteBoardService.registerUser(username, clientService, true)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The whiteboard already has a manager.");
            alert.setOnCloseRequest(event -> System.exit(0));
            alert.showAndWait();

        }

        // show the whiteboard gui
        Scene scene = new Scene(root);
        stage.setTitle("WhiteBoard (Manager)"); // displayed in window's title bar
        stage.setScene(scene);

        whiteBoardController.setupCloseHandler(stage);
        stage.show();
    }

    public static void main(String[] args) throws RemoteException {
        // connect to the server to get whiteboard service
        try {
            username = args[0];
            hostname = args[1];
            port = Integer.parseInt(args[2]);
        }catch (Exception e) {
            System.err.println("Invalid parameters. " +
                    "Usage: java -jar CreateWhiteBoard.jar <username> <hostname> <port>");
            System.exit(0);
        }


        connectServer();

        launch(args);
    }


    // connect to the server and get the whiteboard service
    private static void connectServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(hostname, port);
            whiteBoardService = (WhiteBoardService) registry.lookup("whiteBoardService");
            System.out.println("Connected to server");
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Error: Cannot connect to whiteboard server.");
            System.exit(0);
        }
    }

}
