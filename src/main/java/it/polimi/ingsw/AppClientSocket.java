package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.ServerStub;
import it.polimi.ingsw.view.CLI.CLIController;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AppClientSocket {
    public static void start() throws RemoteException {
        ServerStub serverStub = new ServerStub("localhost", 4445);
        ClientImpl client = new ClientImpl();
        Thread thread = new Thread() {
            public void run() {
            boolean toRun = true;
                while (toRun) {
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
                    } catch (IOException e) {
                        if (this.isInterrupted()) {
                            toRun = false;
                        } else {
                            System.out.println("Connection fallen");
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };

        thread.start();

        CLIController cliController = new CLIController(client, serverStub);
        cliController.start();

        System.out.println("Terminating client");

        thread.interrupt();
        serverStub.close();
        UnicastRemoteObject.unexportObject(client, true);
    }
}
