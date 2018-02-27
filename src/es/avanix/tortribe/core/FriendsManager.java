package es.avanix.tortribe.core;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.ConnectionManager;
import es.avanix.tortribe.utils.AlertHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class FriendsManager {

    public static void addFriend(FriendIdentity fi) {

        Connection connect = SQLite.getConnection();

        try {
            ResultSet result;
            PreparedStatement stq = connect.prepareStatement("select count(*) as total from friends where onion=?");
            stq.setString(1, fi.getOnion().getName());
            result = stq.executeQuery();
            if (result.getInt("total") > 0) {
                //already a friend

                PreparedStatement stq2 = connect.prepareStatement("select * from friends where onion=?");
                stq2.setString(1, fi.getOnion().getName());
                ResultSet result2 = stq2.executeQuery();

                int id = result2.getInt("id");

                PreparedStatement st = connect.prepareStatement("update friends set status = ? where id = ?");
                st.setInt(1, fi.getStatus());
                st.setInt(2, id);
                st.execute();

            } else {
                PreparedStatement st = connect.prepareStatement("insert into friends (nick, onion, port, status, public_key) values (?,?,?,?,?)");
                st.setString(1, fi.getNick());
                st.setString(2, fi.getOnion().getName());
                st.setInt(3, fi.getPort());
                st.setInt(4, fi.getStatus());
                st.setString(5, fi.getHsPublicKey());
                st.execute();
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    public static FriendIdentity getFriend(String onionstring) {
        Connection connect = SQLite.getConnection();

        FriendIdentity fi = null;

        try {
            ResultSet result;
            PreparedStatement stq = connect.prepareStatement("select * from friends where onion=?");
            stq.setString(1, onionstring);
            result = stq.executeQuery();

            //System.out.println("numero encontrado: ");
            if (result.next()) {

                String nick = result.getString("nick");
                String onion = result.getString("onion");
                int port = result.getInt("port");
                int status = result.getInt("status");
                String public_key = result.getString("public_key");

                try {

                    fi = new FriendIdentity(status, nick, new OnionAdress(onion), port, public_key);

                    return fi;
                } catch (Exception ex) {
                    Logger.getLogger(FriendsManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return fi;
    }

    public static void deleteFriend(FriendIdentity fi) {
        Thread thread_autotest = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("delete user thread");

                if (ConnectionManager.getFriendConnections().containsKey(fi.getOnion().getName())) {
                    //ConnectionManager.getFriendConnections().get(fi.getOnion().getName()).closeConnection();
                    //TODO: why the method before freeze the thread?!!
                    ConnectionManager.getFriendConnections().get(fi.getOnion().getName()).setInterrupt(true);
                }

                FriendsManager.removeFriend(fi.getOnion().getName());
                Platform.runLater(() -> {
                    FriendsManager.populateFriends();
                });

            }
        });

        thread_autotest.setName("delete friend thread");
        thread_autotest.setDaemon(true);
        thread_autotest.start();
    }

    private static void removeFriend(String onion) {
        try {
            Connection connect = SQLite.getConnection();

            PreparedStatement st = connect.prepareStatement("delete from friends where onion = ?");
            st.setString(1, onion);
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(FriendsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void populateFriends() {

        Tortribe.friendsListView.clear();

        Connection connect = SQLite.getConnection();

        try {
            ResultSet result;
            PreparedStatement stq = connect.prepareStatement("select * from friends");
            result = stq.executeQuery();

            while (result.next()) {
                String nick = result.getString("nick");
                String onion = result.getString("onion");
                int port = result.getInt("port");
                int status = result.getInt("status");
                String public_key = result.getString("public_key");

                try {

                    FriendIdentity fi = new FriendIdentity(status, nick, new OnionAdress(onion), port, public_key);

                    //WORKAROUND for connected status
                    if (ConnectionManager.getFriendConnections().containsKey(fi.getOnion().getName()) && ConnectionManager.getFriendConnections().get(fi.getOnion().getName()).getSocket().isConnected()) {
                        fi.connected = true;
                    }

                    Tortribe.friendsListView.add(fi);
                } catch (Exception ex) {
                    Logger.getLogger(FriendsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
