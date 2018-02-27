package es.avanix.tortribe.actionhandlers;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import es.avanix.tortribe.utils.AlertHelper;
import javafx.application.Platform;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class AddFriendAccept extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public AddFriendAccept(ActionEvent ae, FriendIdentity fi) {
        this.ae = ae;
        this.fi = fi;
         this.handle();
    }

    @Override
    public void handle() {
        if (fi.getStatus() == FriendIdentity.STATUS_WAITING) {

            fi.setStatus(FriendIdentity.STATUS_OK);

            FriendsManager.addFriend(fi);

            this.ae.getConnection().setIdentity(fi);

            //Tortribe.friendsListView.add(remoteFI);
            Platform.runLater(() -> {
                FriendsManager.populateFriends();
            });

            JSONObject req = new JSONObject();
            req.put("action", "add_friend_accept_ok");

            String enviar = req.toString();

            this.ae.getConnection().sendMessage(enviar);

            ConnectionManager.getFriendConnections().put(fi.getOnion().getName(), this.ae.getConnection());

        } else {

            System.err.println("requested add_friend error: already in database");
        }
    }

    public static void send(FriendIdentity friendIdentity) {

        Thread thread_autotest = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("addfriendaccept thread");

                Connection fc_addfriend = ConnectionManager.getConnection(friendIdentity);

                if (fc_addfriend != null) {

                    JSONObject req = new JSONObject();
                    req.put("action", "add_friend_accept");

                    JSONObject id = new JSONObject();
                    id.put("nick", MyIdentity.getMyidentity().getNick());
                    id.put("onion", MyIdentity.getMyidentity().getOnion().getName());
                    id.put("port", MyIdentity.getMyidentity().getPort());
                    id.put("public_key", MyIdentity.getMyidentity().getHsPublicKey());

                    req.put("identity", id);
                    req.put("signature", Base64.encode(MyIdentity.signMessage(id.toString())));

                    String msg = req.toString();

                    fc_addfriend.sendMessage(msg);

                    fc_addfriend.setIdentity(friendIdentity);

                    System.out.println("add_friend_accept sent");

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
