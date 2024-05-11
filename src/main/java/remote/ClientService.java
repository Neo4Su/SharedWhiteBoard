package remote;

import client.DrawCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    void synchronizeCanvas(byte[] snapshotBytes) throws RemoteException;
}
