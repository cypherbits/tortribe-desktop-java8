package es.avanix.tortribe.actionhandlers;

import es.avanix.tortribe.core.FileManager;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.gui.main.MainWindowController;
import es.avanix.tortribe.gui.parts.ChatTabController;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class GetFileHashList extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public GetFileHashList(ActionEvent ae) {
        this.ae = ae;
        this.fi = ae.getConnection().getIdentity();
        this.handle();
    }

    @Override
    public void handle() {
        System.out.println("getfilehashlist handler");

        JSONObject resp = new JSONObject();
        resp.put("action", "getfilehashlist_ok");
        
        Set<byte[]> hashSet = FileManager.getLocalFiles().keySet();
        
        JSONArray files = new JSONArray(hashSet.toArray());
        
        resp.put("files", files);

    }

    public static void send(FriendIdentity identity, String message) {

        Thread thread_autotest = new Thread(new Runnable() {
            @Override
            public void run() {

                JSONObject req = new JSONObject();
                req.put("action", "getfilehashlist");

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
