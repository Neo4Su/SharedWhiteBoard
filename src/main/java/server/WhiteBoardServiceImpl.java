package server;

import client.DrawCommand;
import remote.ClientService;
import remote.WhiteBoardService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class WhiteBoardServiceImpl extends UnicastRemoteObject implements WhiteBoardService {
    List<User> userList;


    public WhiteBoardServiceImpl() throws RemoteException {
        userList = new ArrayList<>();
    }


    @Override
    public void registerUser(String username, ClientService clientService) throws RemoteException {
        User user = new User(username, clientService);

        userList.add(user);
    }

    @Override
    public void drawOnCanvas(DrawCommand drawCommand) throws RemoteException {
        for (User user : userList) {
            user.getClientService().synchronizeCanvas(drawCommand);
        }
    }
}
