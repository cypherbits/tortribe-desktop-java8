package es.avanix.tortribe.actionhandlers;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import es.avanix.tortribe.utils.AlertHelper;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class AddFriend extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public AddFriend(ActionEvent ae, FriendIdentity fi) {
        this.ae = ae;
        this.fi = fi;
         this.handle();
    }

    @Override
    public void handle() {
        if (fi.getStatus() == FriendIdentity.STATUS_NOFRIEND) {

            fi.setStatus(FriendIdentity.STATUS_PENDING);

            FriendsManager.addFriend(fi);

            Platform.runLater(() -> {
                Tortribe.friendsListView.add(fi);
            });
            
            this.ae.getConnection().setIdentity(fi);

            JSONObject req = new JSONObject();
            req.put("action", "add_friend_ok");

            JSONObject id = new JSONObject();
            id.put("nick", MyIdentity.getMyidentity().getNick());
            id.put("onion", MyIdentity.getMyidentity().getOnion().getName());
            id.put("port", MyIdentity.getMyidentity().getPort());
            id.put("public_key", MyIdentity.getMyidentity().getHsPublicKey());

            req.put("identity", id);
            req.put("signature", Base64.encode(MyIdentity.signMessage(id.toString())));

            String enviar = req.toString();

            this.ae.getConnection().sendMessage(enviar);

        } else {

            System.err.println("requested add_friend error: already in database");
        }

    }

    public static void send(FriendIdentity friendIdentity) {
        Thread thread_autotest = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("addfriend thread");

                Connection fc_addfriend = ConnectionManager.getConnection(friendIdentity);

                if (fc_addfriend != null) {

                    JSONObject req = new JSONObject();
                    req.put("action", "add_friend");

                    JSONObject id = new JSONObject();
                    id.put("nick", MyIdentity.getMyidentity().getNick());
                    id.put("onion", MyIdentity.getMyidentity().getOnion().getName());
                    id.put("port", MyIdentity.getMyidentity().getPort());
                    id.put("public_key", MyIdentity.getMyidentity().getHsPublicKey());

                    req.put("identity", id);
                    req.put("signature", Base64.encode(MyIdentity.signMessage(id.toString())));

                    String msg = req.toString();

                    fc_addfriend.sendMessage(msg);

                    System.out.println("add_friend sent");

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

        thread_autotest.setName("add_friend thread");
        thread_autotest.setDaemon(true);
        thread_autotest.start();

    }

}
