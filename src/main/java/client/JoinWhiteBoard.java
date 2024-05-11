package client;//Painter.java


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        // pass the whiteboard service to the controller
        WhiteBoardController whiteBoardController = fxmlLoader.getController();
        whiteBoardController.setWhiteBoardService(whiteBoardService);

        // register the user
        clientService = new ClientServiceImpl(whiteBoardController);
        whiteBoardService.registerUser(username, clientService);

        // show the whiteboard gui
        Scene scene = new Scene(root);
        stage.setTitle("WhiteBoard"); // displayed in window's title bar
        stage.setScene(scene);
        stage.show();
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
