package es.avanix.tortribe.net;

import es.avanix.tortribe.core.Identity;
import es.avanix.tortribe.main.Tortribe;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imhotep
 */
public class ConnectionManager {

    private static HashSet<Connection> notFriendConnections = new HashSet<Connection>();

    private static HashMap<String, Connection> friendConnections = new HashMap<String, Connection>();

    public ConnectionManager() {

    }

    public static HashMap<String, Connection> getFriendConnections() {
        return friendConnections;
    }

    public static void addConnection(Socket socket) {
        notFriendConnections.add(new Connection(socket, notFriendConnections.size() + 1));
    }

    public static void addFriendConnection(String onion, Connection con) {
        friendConnections.put(onion, con);
    }

    private static Connection getNewCon(Identity fi) {
        if (fi != null) {

            try {
                SocketAddress addr = new InetSocketAddress("127.0.0.1", Tortribe.TOR_SOCKS_PROXY_PORT);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, addr);
                Socket socket = new Socket(proxy);
                InetSocketAddress dest = new InetSocketAddress(fi.getOnion().getURI(), fi.getPort());
                System.out.println("cliente a " + fi.getOnion().getURI());
                //socket.setSoTimeout(30000);
                socket.connect(dest, 10000);
                if (socket.isConnected()) {
                    Connection con = new Connection(socket, friendConnections.size() + 1);
                    addFriendConnection(fi.getOnion().getName(), con);
                    return con;
                }

            } catch (IOException ex) {
                //Logger.getLogger(ChatTabController.class.getName()).log(Level.SEVERE, null, ex);

                System.err.println("Connection to " + fi.getOnion().getURI() + " failed: " + ex.getMessage());

                return null;
            }

        }

        return null;
    }

    private static Connection getNewConnection(Identity fi, int retry) {

        Connection fc = getNewCon(fi);

        //Sometimes user can click to start trying get a connection many times, if we get a connection in one thread we should stop 
        //trying and return the already connected connection
        while (fc == null && retry > 0 && !ConnectionManager.getFriendConnections().containsKey(fi.getOnion().getName())) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("try " + String.valueOf(retry));
            fc = getNewCon(fi);
            retry -= 1;
        }

        if (ConnectionManager.getFriendConnections().containsKey(fi.getOnion().getName())) {
            return ConnectionManager.getFriendConnections().get(fi.getOnion().getName());
        }

        return fc;
    }

    public static Connection getConnection(Identity identity) {
        Connection connection = null;
        if (ConnectionManager.getFriendConnections().containsKey(identity.getOnion().getName())) {
            connection = ConnectionManager.getFriendConnections().get(identity.getOnion().getName());
        } else {
            connection = ConnectionManager.getNewConnection(identity, 25);
        }

        return connection;
    }

}
