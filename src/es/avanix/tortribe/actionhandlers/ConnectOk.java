package es.avanix.tortribe.actionhandlers;

import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.gui.parts.ChatTabController;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.ConnectionManager;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Tab;

/**
 *
 * @author imhotep
 */
public class ConnectOk extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public ConnectOk(ActionEvent ae) {
        this.ae = ae;
        this.fi = ae.getConnection().getIdentity();
        this.handle();
    }

    @Override
    public void handle() {
        try {

            this.ae.getConnection().getSocket().setKeepAlive(true);
        } catch (SocketException ex) {
            Logger.getLogger(ConnectOk.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        ConnectionManager.getFriendConnections().put(fi.getOnion().getName(), this.ae.getConnection());
    }

}
