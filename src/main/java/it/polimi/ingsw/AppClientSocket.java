package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.ServerStub;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.GUI.GUIController;
import it.polimi.ingsw.view.ViewController;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * The AppClientSocket class provides a method to start a client socket connection with a server.
 */
public class AppClientSocket {
    public static void start(ViewController viewController, String IP) throws RemoteException {
        ServerStub serverStub = new ServerStub(IP, 4445);
        ClientImpl client = new ClientImpl();
        new Thread() {
            @Override
            public void run() {
                boolean toRun = true;
                while(toRun) {
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
        }.start();

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        serverStub.checkConnection();
                    } catch (RemoteException e) {
                        System.err.println("Cannot establish a connection with the server.\nCheck your internet connection and try again.");
                        System.exit(0);
                    }
                }
            }
        }.start();


        viewController.start(client, serverStub);
    }
}
