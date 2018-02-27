package es.avanix.tortribe.net;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.actionhandlers.ActionHandler;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.gui.parts.ChatTabController;
import es.avanix.tortribe.main.Tortribe;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Tab;

/**
 *
 * @author imhotep
 */
public class Connection implements Runnable {

    private Socket socket;
    private Thread thread;

    private BufferedReader in;

    private BufferedWriter bfo;

    private FriendIdentity friend;

    private boolean interrupt;

    public Connection(Socket socket, int n) {

        this.socket = socket;

        this.interrupt = false;

        this.thread = new Thread(this);
        this.thread.setName("connection " + String.valueOf(n));
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public Thread getThread() {
        return this.thread;
    }

    public FriendIdentity getIdentity() {
        return this.friend;
    }

    public void setIdentity(FriendIdentity id) {
        this.friend = id;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    @Override
    public void run() {

        //Tortribe.mainWindowController.getMain_txtChat().appendText("CONECTADO");
        try {

            //Set BufferedReader
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));

            //Set BufferedWriter
            this.bfo = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));

            String message = null;
            while (!this.interrupt && (message = in.readLine()) != null) {

                new ActionHandler(this, message);

            }

            if (message == null) {

                this.closeConnection();

            }

        } catch (SocketTimeoutException ex) {
            System.err.println("Socket timeout, cannot read more");
            this.closeConnection();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            this.closeConnection();
        }
    }

    public void closeConnection() {
        this.interrupt = true;
        try {
            this.in.close();
            this.bfo.close();

            this.socket.close();

            if (this.friend != null && ConnectionManager.getFriendConnections().containsKey(this.friend.getOnion().getName())) {
                ConnectionManager.getFriendConnections().remove(this.friend.getOnion().getName());

                Platform.runLater(() -> {
                    if (this.friend.getStatus() == FriendIdentity.STATUS_OK) {
                        FriendsManager.populateFriends();

                        Tab tab = (Tab) Tortribe.tabs.get(this.friend.getOnion().getName());

                        if (tab != null) {
                            ChatTabController ctc2 = (ChatTabController) tab.getUserData();

                            ctc2.setDisabled(true);
                        }
                    }
                });
            }

        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String msg) {

        System.out.println("Sending: " + msg);

        try {

            bfo.write(msg);
            bfo.newLine();
            bfo.flush();

        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
