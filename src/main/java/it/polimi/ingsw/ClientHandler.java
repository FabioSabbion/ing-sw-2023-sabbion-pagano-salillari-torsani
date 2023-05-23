package it.polimi.ingsw;

import it.polimi.ingsw.distributed.networking.ClientSkeleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    ObjectInputStream is = null;
    ObjectOutputStream os = null;
    Socket socket = null;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ClientSkeleton clientSkeleton;
        try {
            is = new ObjectInputStream(socket.getInputStream());
            os = new ObjectOutputStream(socket.getOutputStream());
            clientSkeleton = new ClientSkeleton(is, os);
        } catch (IOException e) {
            System.out.println("IO error in server thread");
            throw new RuntimeException(e);
        }
        String message;
        try {
            while (true) {
                clientSkeleton.receive();
            }
        } catch (IOException e) {
            // socket connection failed
            System.out.println("IO Error/ Client terminated abruptly");
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println("Client Closed");

            System.err.println(e.getMessage());

            throw new RuntimeException(e);
        } finally {
            try {
                System.out.println("Connection Closing..");
                if (is != null) {
                    is.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if (os != null) {
                    os.close();
                    System.out.println("Socket Out Closed");
                }
                if (socket != null) {
                    socket.close();
                    System.out.println("Socket Closed");
                }

            } catch (IOException ie) {
                System.out.println("Socket Close Error");
            }
        }//end finally
    }
}
