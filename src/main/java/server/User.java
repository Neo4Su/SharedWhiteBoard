package server;

import remote.ClientService;

public class User {
    private String username;
    private ClientService clientService;

    public User(){
    }

    public User(String username, ClientService clientService){
        this.username = username;
        this.clientService = clientService;
    }

    public String getUsername(){
        return username;
    }
    public ClientService getClientService(){
        return clientService;
    }
}
