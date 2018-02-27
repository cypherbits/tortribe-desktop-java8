package es.avanix.tortribe.actionhandlers;

import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.gui.main.MainWindowController;
import es.avanix.tortribe.gui.parts.ChatTabController;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class Message extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public Message(ActionEvent ae) {
        this.ae = ae;
        this.fi = ae.getConnection().getIdentity();
        this.handle();
    }

    @Override
    public void handle() {
        System.out.println("message handler");

        JSONObject obj = new JSONObject(this.ae.getMessage());
        if (!obj.isNull("msg")) {

            String message = obj.getString("msg");

            //1-validate identity+we are friends 2-show message
            //TODO -> GET IDENTITY REAL FROM LIST
            if (!ConnectionManager.getFriendConnections().containsKey(this.fi.getOnion().getName())) {
                ConnectionManager.getFriendConnections().put(this.fi.getOnion().getName(), this.ae.getConnection());
            }

            try {

                //FriendIdentity fi = new FriendIdentity(nick, new OnionAdress(rOnionString), port, null, 2);
                Tab tab = (Tab) Tortribe.tabs.get(this.fi.getOnion().getName());

                if (tab != null) {

                    Platform.runLater(() -> {
                        if (Tortribe.main_tabs.getTabs().indexOf(tab) == -1) {
                            Tortribe.main_tabs.getTabs().add(tab);
                        }
                    });

                    ChatTabController ctc2 = (ChatTabController) tab.getUserData();

                    ctc2.addMessage(message);

                } else {
                    try {

                        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/es/avanix/tortribe/gui/parts/ChatTab.fxml"));

                        Tab newtab = (Tab) loader.load();
                        ChatTabController controller = loader.getController();
                        controller.setIdentity(this.fi);
                        controller.init();

                        newtab.setUserData(controller);
                        newtab.setText(this.fi.getNick());

                        Tortribe.tabs.put(this.fi.getOnion().getName(), newtab);

                        Platform.runLater(() -> {
                            Tortribe.main_tabs.getTabs().setAll(Tortribe.tabs.values());

                            Tortribe.main_tabs.getSelectionModel().select(newtab);

                            ChatTabController ctc2 = (ChatTabController) newtab.getUserData();

                            ctc2.addMessage(message);
                        });

                    } catch (IOException ex) {
                        Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.err.println("Message received is null");
        }

    }

    public static void send(FriendIdentity identity, String message) {

        Thread thread_autotest = new Thread(new Runnable() {
            @Override
            public void run() {

                JSONObject req = new JSONObject();
                req.put("action", "message");
                req.put("msg", message);

                String jsonmsg = req.toString();

                Connection fc = ConnectionManager.getConnection(identity);

                fc.sendMessage(jsonmsg);

            }
        });
        thread_autotest.setName("send message to " + identity.getNick());
        thread_autotest.setDaemon(true);
        thread_autotest.start();

    }

}
