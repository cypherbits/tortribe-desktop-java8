package es.avanix.tortribe.actionhandlers;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import javafx.application.Platform;

/**
 *
 * @author imhotep
 */
public class AddFriendOk extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public AddFriendOk(ActionEvent ae, FriendIdentity fi) {
        this.ae = ae;
        this.fi = fi;
         this.handle();
    }

    @Override
    public void handle() {
        this.ae.getConnection().setIdentity(fi);

        this.ae.getConnection().getIdentity().setStatus(FriendIdentity.STATUS_WAITING);

        FriendsManager.addFriend(this.ae.getConnection().getIdentity());

        Platform.runLater(() -> {
            FriendsManager.populateFriends();
        });

        this.ae.getConnection().closeConnection();
    }

//    public static void send(FriendIdentity friendIdentity) {
//       
//
//    }

}
