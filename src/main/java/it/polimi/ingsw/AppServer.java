package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Lobby;

import java.util.Scanner;

/**
 * Entry point class for the Server
 */
public class AppServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid number of arguments.\nThe argument should be the IP of the Server (x.x.x.x)");
            return;
        }
        System.setProperty("java.rmi.server.hostname", args[0]);
        Lobby.getInstance();


        new AppServerRMI().start();
        new AppServerSocket().start();

        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        while(!s.equals("quit")) {
            s = sc.nextLine();
        }
        System.out.println("Server stopped");
    }
}
