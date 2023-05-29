package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientImpl;
import it.polimi.ingsw.distributed.networking.ServerStub;
import it.polimi.ingsw.view.GUI.GUI;
import it.polimi.ingsw.view.GUI.GUIController;
import it.polimi.ingsw.view.ViewController;

import java.rmi.RemoteException;

public class AppClientSocket {
    public static void start(ViewController viewController) throws RemoteException {
        if (viewController instanceof GUIController) {
            GUI.main(null);
            return;
        }
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


        viewController.start(client, serverStub);

        System.out.println("Terminating client");
    }
}
