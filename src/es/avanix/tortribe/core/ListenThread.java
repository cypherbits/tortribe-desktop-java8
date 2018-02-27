package es.avanix.tortribe.core;

import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.main.test;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imhotep
 */
public class ListenThread implements Runnable {

    public ListenThread() {
        
    }

    @Override
    public void run() {

        try {

            // Socket for server to listen at.
            ServerSocket listener = new ServerSocket(Tortribe.listenPORT);

            System.out.println("Server is now running at port: " + Tortribe.listenPORT);

            // Simply making Server run continously.
            while (true) {
                    // Accept a client connection once Server recieves one.
                    Socket socket = listener.accept();
                    //socket.setSoTimeout(30000);
                    
                    ConnectionManager.addConnection(socket);
                    
            }
        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
