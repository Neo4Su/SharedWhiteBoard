package server;

import remote.WhiteBoardService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WhiteBoardServer {
    public static void main(String[] args) {

        try {
            WhiteBoardService whiteBoardService = new WhiteBoardServiceImpl();
            int port = Integer.parseInt(args[0]);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("whiteBoardService", whiteBoardService);
            System.out.println("Server is ready on port: " + port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
