package it.polimi.ingsw;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

public class AppServerSocket
{
    static final int PORT = 4445;
    public static void main( String[] args ) throws RemoteException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while(true){
                try{
                    Socket socket = serverSocket.accept();
                    System.out.println("New client connected");
                    new ClientHandler(socket).start();
                }
                catch(IOException e){
                    e.printStackTrace();
                    System.out.println("New client tried to connect but failed");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

