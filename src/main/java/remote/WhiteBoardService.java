package remote;

import client.DrawCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteBoardService extends Remote {
    void registerUser(String username, ClientService clientService) throws RemoteException;

    void drawOnCanvas(byte[] snapshotBytes) throws RemoteException;



}
