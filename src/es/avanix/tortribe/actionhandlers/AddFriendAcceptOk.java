package es.avanix.tortribe.actionhandlers;

import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.net.ConnectionManager;
import javafx.application.Platform;

/**
 *
 * @author imhotep
 */
public class AddFriendAcceptOk extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public AddFriendAcceptOk(ActionEvent ae) {
        this.ae = ae;
        this.fi = ae.getConnection().getIdentity();
         this.handle();
    }

    @Override
    public void handle() {
        this.ae.getConnection().getIdentity().setStatus(FriendIdentity.STATUS_OK);

        FriendsManager.addFriend(this.ae.getConnection().getIdentity());

        Platform.runLater(() -> {
            FriendsManager.populateFriends();
        });

        ConnectionManager.getFriendConnections().put(fi.getOnion().getName(), this.ae.getConnection());
    }

}
