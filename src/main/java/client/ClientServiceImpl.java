package client;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    public void synchronizeCanvas(DrawCommand drawCommand) throws RemoteException {
        Canvas canvas = whiteBoardController.canvas;
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double startX = drawCommand.startX, startY = drawCommand.startY,
                endX = drawCommand.endX, endY = drawCommand.endY;





    }
}
