package it.polimi.ingsw;

import it.polimi.ingsw.distributed.Lobby;

import java.util.Scanner;

public class AppServer {
    public static void main(String[] args) {
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
