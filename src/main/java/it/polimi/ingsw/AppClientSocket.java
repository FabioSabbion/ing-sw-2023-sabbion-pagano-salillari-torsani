package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.Client;
import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.ServerStub;

import java.rmi.RemoteException;
import java.util.Scanner;

public class AppClientSocket {
    public static void main(String[] args) throws RemoteException {
        ServerStub serverStub = new ServerStub("localhost", 4445);
        Client client = new ClientImpl(serverStub);
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        serverStub.receive(client);
                    } catch (RemoteException e) {
                        System.err.println("Cannot receive from server. Stopping...");
                        try {
                            serverStub.close();
                        } catch (RemoteException ex) {
                            System.err.println("Cannot close connection with server. Halting...");
                        }
                        System.exit(1);
                    }
                }
            }
        }.start();

        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        serverStub.setNickname(s, client);

        sc.nextLine();

        System.out.println("Terminating client");

        // TODO: run view with client.run
    }
}
