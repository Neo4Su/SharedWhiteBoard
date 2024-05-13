package remote;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;



//TODO:
public interface WhiteBoardService extends Remote {
    boolean registerUser(String username, ClientService clientService, boolean isManager) throws RemoteException;

    void judgeJoinRequest(String username, boolean allowJoin) throws RemoteException;

    void drawOnCanvas(byte[] snapshotBytes) throws RemoteException;

    byte[] getCanvas() throws RemoteException;

    ArrayList<String> getUserList() throws RemoteException;



    void userQuit(String username, boolean isManager) throws RemoteException;

    void sendMessage(String message) throws RemoteException;

    // get previous chat when a new client joins
    ArrayList<String> getPastMessages() throws RemoteException;

    void kickUser(String username) throws RemoteException;

    boolean hasManager() throws RemoteException;

}
