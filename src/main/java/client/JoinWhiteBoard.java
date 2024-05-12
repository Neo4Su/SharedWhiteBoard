package client;//Painter.java


import javafx.application.Application;
import javafx.application.Platform;
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

public class JoinWhiteBoard extends Application {
    private static String username;
    private static String hostname;
    private static int port;
    private static WhiteBoardService whiteBoardService;
    private static ClientService clientService;

    // set gui of the whiteboard
    @Override
    public void start(Stage stage) throws Exception {



        FXMLLoader fxmlLoader = new FXMLLoader(CreateWhiteBoard.class.getResource("/Whiteboard.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        // pass the whiteboard service to the controller
        WhiteBoardController whiteBoardController = fxmlLoader.getController();
        whiteBoardController.setWhiteBoardService(whiteBoardService);
        whiteBoardController.initializeIdentity("Client");
        whiteBoardController.setCurrentUser(username);
        whiteBoardController.initializeUserList();
        whiteBoardController.initializeChat();

        whiteBoardController.setStage(stage);
        whiteBoardController.setScene(scene);
        whiteBoardController.setupCloseHandler(stage);

        Platform.setImplicitExit(false);

        // register the client
        clientService = new ClientServiceImpl(whiteBoardController);
        if (whiteBoardService.registerUser(username, clientService,false)==false){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Duplicate username, please try again with a different username.");
            alert.setOnCloseRequest(event -> System.exit(0));
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Whiteboard (Client)");
            alert.setHeaderText(null);
            alert.setContentText("Waiting for manager to approve your join");
            alert.show();
        }


//        stage.setTitle("WhiteBoard (Client)"); // displayed in window's title bar
//        stage.setScene(scene);
//        stage.show();


    }

    public static void main(String[] args) throws RemoteException {
        // connect to the server to get whiteboard service
        username = args[0];
        hostname = args[1];
        port = Integer.parseInt(args[2]);

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
            e.printStackTrace();
        }
    }
}
