package server;

import remote.ClientService;
import remote.WhiteBoardService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class WhiteBoardServiceImpl extends UnicastRemoteObject implements WhiteBoardService {
    List<User> userList;

    // clients waiting for manager to accept
    List<User> clientWaitList;

    List<String> messageList;

    boolean hasManager = false;

    public WhiteBoardServiceImpl() throws RemoteException {
        userList = new ArrayList<>();
        messageList = new ArrayList<>();
        clientWaitList = new ArrayList<>();
    }


    // to do: manager manges clients
    @Override
    public boolean registerUser(String username, ClientService clientService, boolean isManager) throws RemoteException {
        if (isManager) {
            if (hasManager) return false;
            else hasManager = true;
        }



        // reject if username already exists
        for (User u : userList) {
            if (u.getUsername().equals(username)) {
                return false;
            }
        }
        for (User u : clientWaitList) {
            if (u.getUsername().equals(username)) {
                return false;
            }
        }

        User newUser = new User(username, clientService);
        clientWaitList.add(newUser);

        // if new user is client, ask manager for approval
        if(!isManager){
            userList.get(0).getClientService().askJoin(username);
            return true;
        }

        // add manager to the whiteboard
        userList.add(newUser);
        for (User u : userList) {
            u.getClientService().addNewUser(username);
        }
        return true;
    }

    @Override
    public void judgeJoinRequest(String username, boolean allowJoin) throws RemoteException {
        User newClient = new User();
        for (User user : clientWaitList) {
            if (user.getUsername().equals(username)) {
                newClient = user;
                break;
            }
        }
        clientWaitList.remove(newClient);

        if (allowJoin) {
            // add manager to the whiteboard
            userList.add(newClient);
            newClient.getClientService().notifyJoinApproval(true);
            for (User u : userList) {
                u.getClientService().addNewUser(username);
            }
        } else {
            newClient.getClientService().notifyJoinApproval(false);
        }
    }



    @Override
    public void drawOnCanvas(byte[] snapshotBytes) throws RemoteException {
        for (User user : userList) {
            user.getClientService().synchronizeCanvas(snapshotBytes);
        }
    }

    @Override
    public ArrayList<String> getUserList() throws RemoteException {
        ArrayList<String> usernameList = new ArrayList<>();
        for (User user : userList) {
            usernameList.add(user.getUsername());
        }
        return usernameList;
    }

    @Override
    public void userQuit(String username, boolean isManager) throws RemoteException {
        if (isManager) {

            for (User user : userList) {
                user.getClientService().notifyBoardClosed();
            }
            userList.clear();
            return;
        }


        for (User user : userList) {
            user.getClientService().removeUser(username);
        }

        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                userList.remove(user);
                break;
            }
        }
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        messageList.add(message);
        for (User user : userList) {
            user.getClientService().receiveMessage(message);
        }
    }

    // get previous chat when a new client joins
    @Override
    public ArrayList<String> getPastMessages() throws RemoteException {
        return new ArrayList<>(messageList);
    }

    @Override
    public void kickUser(String username) throws RemoteException{
        for (User user : userList) {
            user.getClientService().kickUser(username);
        }

        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                userList.remove(user);
                break;
            }
        }

    }


}
