package client;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import remote.ClientService;
import remote.WhiteBoardService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ClientServiceImpl extends UnicastRemoteObject implements ClientService {

    private WhiteBoardController whiteBoardController;
    public ClientServiceImpl(WhiteBoardController whiteBoardController) throws RemoteException {
        this.whiteBoardController = whiteBoardController;
    }

    @Override
    public void synchronizeCanvas(byte[] snapshotBytes) throws RemoteException {
        Platform.runLater(() -> {
            Canvas canvas = whiteBoardController.getCanvas();
            GraphicsContext gc = canvas.getGraphicsContext2D();

            WritableImage snapshot = ImageBytesConverter.BytesToImage(snapshotBytes);
            whiteBoardController.setSnapshot(snapshot);

            gc.drawImage(snapshot, 0, 0, canvas.getWidth(), canvas.getHeight());
        });

    }

    @Override
    public void addNewUser(String newUser) throws RemoteException {
        Platform.runLater(() -> {
            whiteBoardController.addNewUser(newUser);
        });

    }

    @Override
    public void removeUser(String username) throws RemoteException {
        Platform.runLater(() -> {
            whiteBoardController.removeUser(username);
        });
    }

    @Override
    public void receiveMessage(String message) {
        Platform.runLater(() -> {
            whiteBoardController.updateChat(message);
        });
    }

    @Override
    public void kickUser(String username) throws RemoteException {
        Platform.runLater(() -> {
            whiteBoardController.kickUser(username);
        });
    }

    @Override
    public void notifyBoardClosed() throws RemoteException {
        Platform.runLater(() -> {
            whiteBoardController.notifyBoardClosed();
        });
    }

    @Override
    public void askJoin(String username) throws RemoteException {
        Platform.runLater(() -> {
            try {
                whiteBoardController.askJoin(username);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void notifyJoinApproval(boolean isApproved) throws RemoteException {
        System.out.println("platform run later");
        Platform.runLater(() -> {
            System.out.println("Join approved: " + isApproved);
            whiteBoardController.notifyJoinApproval(isApproved);
        });
        System.out.println("platform run later 2");
    }


}
