package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.ServerStub;
import it.polimi.ingsw.view.CLI.CLIController;

import java.rmi.RemoteException;

public class AppClientSocket {
    public static void start() throws RemoteException {
        ServerStub serverStub = new ServerStub("localhost", 4445);
        ClientImpl client = new ClientImpl();
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


        CLIController cliController = new CLIController(client, serverStub);
        cliController.start();

        System.out.println("Terminating client");
    }
}
