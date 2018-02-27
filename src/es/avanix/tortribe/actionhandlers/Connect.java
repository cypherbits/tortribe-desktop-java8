package es.avanix.tortribe.actionhandlers;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.gui.parts.ChatTabController;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import es.avanix.tortribe.utils.AlertHelper;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class Connect extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public Connect(ActionEvent ae, FriendIdentity fi) {
        this.ae = ae;
        this.fi = fi;
         this.handle();
    }

    @Override
    public void handle() {
        if (fi.getStatus() == FriendIdentity.STATUS_OK) {

            try {
                ConnectionManager.getFriendConnections().put(fi.getOnion().getName(), this.ae.getConnection());
                this.ae.getConnection().getSocket().setKeepAlive(true);
                //this.ae.getConnection().getSocket().setSoTimeout(0);

                this.ae.getConnection().setIdentity(fi);

                Platform.runLater(() -> {
                    if (this.ae.getConnection().getIdentity().getStatus() == FriendIdentity.STATUS_OK) {
                        FriendsManager.populateFriends();

                        Tab tab = (Tab) Tortribe.tabs.get(this.ae.getConnection().getIdentity().getOnion().getName());

                        if (tab != null) {
                            ChatTabController ctc2 = (ChatTabController) tab.getUserData();

                            ctc2.setDisabled(false);
                        }
                    }
                });

                JSONObject req = new JSONObject();
                req.put("action", "connect_ok");

                String enviar = req.toString();

                this.ae.getConnection().sendMessage(enviar);
            } catch (SocketException ex) {
                Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            //TODO send error: you are not my friend
            System.err.println("Error: you are not my friend");
        }
    }

    public static void send(FriendIdentity friendIdentity) {

        Thread thread_autotest = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("send connect thread");

                Connection fc_addfriend = ConnectionManager.getConnection(friendIdentity);

                if (fc_addfriend != null) {
                    JSONObject req = new JSONObject();
                    req.put("action", "connect");

                    JSONObject id = new JSONObject();
                    id.put("nick", MyIdentity.getMyidentity().getNick());
                    id.put("onion", MyIdentity.getMyidentity().getOnion().getName());
                    id.put("port", MyIdentity.getMyidentity().getPort());
                    id.put("public_key", MyIdentity.getMyidentity().getHsPublicKey());

                    req.put("identity", id);
                    req.put("signature", Base64.encode(MyIdentity.signMessage(id.toString())));
                    String msg = req.toString();
                    
                    fc_addfriend.setIdentity(friendIdentity);

                    fc_addfriend.sendMessage(msg);

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.err.println("Connection to friend failed.");

                            AlertHelper.newErrorAlert("Connection error", "Error connecting to friend.");
                        }
                    });
                }

            }
        });

        thread_autotest.setName("connect send thread");
        thread_autotest.setDaemon(true);
        thread_autotest.start();

    }

}
