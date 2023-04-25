package it.polimi.ingsw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class AppServerSocket extends Thread
{
    static final int PORT = 4445;
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Socket Server started on port " + PORT);
            while(true){
                try{
                    Socket socket = serverSocket.accept();
                    System.out.println("New socket client connected");
                    new ClientHandler(socket).start();
                }
                catch(IOException e){
                    // e.printStackTrace();
                    System.out.println("A socket client failed to connect");
                }
            }
        } catch (IOException e) {
            System.out.println("Couldn't start Socket Server.");
        }
    }
}

