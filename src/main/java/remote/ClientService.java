package remote;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {
    void synchronizeCanvas(byte[] snapshotBytes) throws RemoteException;

    void addNewUser(String newUser) throws RemoteException;

    void removeUser(String username) throws RemoteException;

    void receiveMessage(String message) throws RemoteException;

    void kickUser(String username) throws RemoteException;

    void notifyBoardClosed() throws RemoteException;

    void askJoin(String username) throws RemoteException;

    void notifyJoinApproval(boolean isApproved) throws RemoteException;
}
