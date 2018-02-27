package es.avanix.tortribe.actionhandlers;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.gui.main.MainWindowController;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class AutoTest extends Action {

    private ActionEvent ae;
    private FriendIdentity fi;

    public AutoTest(ActionEvent ae, FriendIdentity fi) {
        this.fi = fi;
        if (ae instanceof ActionHandler) {
            this.ae = (ActionHandler) ae;
            this.handle();
        }
//        else if (ae instanceof ActionSender) {
//            this.ae = (ActionSender) ae;
//            this.send();
//        }

    }

    public void handle() {
        if (MyIdentity.getMyidentity().getOnion().getName().equals(this.fi.getOnion().getName()) && MyIdentity.getMyidentity().getPort() == this.fi.getPort()) {
            JSONObject res = new JSONObject();
            res.append("code", 0);
            res.append("msg", "");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //Tortribe.mainWindowController.setMyStatus("Connected.");
                    Tortribe.mainWindowController.getMain_circleStatus().setFill(Color.GREEN);
                    Tooltip t = new Tooltip("Connected.");
                    Tooltip.install(Tortribe.mainWindowController.getMain_circleStatus(), t);
                    Tortribe.mainWindowController.main_labelStatus.setText("Invitation link, click to copy:");
                    Tortribe.mainWindowController.main_linkInvite.setVisible(true);
                }
            });
            System.out.println("Autotest OK");
        } else {
            JSONObject res = new JSONObject();
            res.append("code", -1);
            res.append("msg", "error: Autotest data identity doesnt match");
        }

        this.ae.getConnection().closeConnection();
    }

    public static void send() {
        Thread thread_autotest = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("autotest thread");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Tortribe.mainWindowController.getMain_circleStatus().setFill(Color.ORANGE);
                        Tooltip t = new Tooltip("Connecting...");
                        Tooltip.install(Tortribe.mainWindowController.getMain_circleStatus(), t);
                        Tortribe.mainWindowController.main_labelStatus.setText("Connecting...");
                    }
                });

                Connection fc_autotest = ConnectionManager.getConnection(MyIdentity.getMyidentity());

                if (fc_autotest != null) {

                    System.out.println("Autotest starting");

                    JSONObject req = new JSONObject();
                    req.put("action", "autotest");

                    JSONObject id = new JSONObject();
                    id.put("nick", MyIdentity.getMyidentity().getNick());
                    id.put("onion", MyIdentity.getMyidentity().getOnion().getName());
                    id.put("port", MyIdentity.getMyidentity().getPort());
                    id.put("public_key", MyIdentity.getMyidentity().getHsPublicKey());

                    req.put("identity", id);
                    req.put("signature", Base64.encode(MyIdentity.signMessage(id.toString())));

                    String msg = req.toString();

                    fc_autotest.sendMessage(msg);
                    System.out.println("Autotest sent");

                    fc_autotest.closeConnection();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Tortribe.mainWindowController.getMain_circleStatus().setFill(Color.RED);
                            Tooltip t = new Tooltip("Autotest failed.");
                            Tooltip.install(Tortribe.mainWindowController.getMain_circleStatus(), t);
                            Tortribe.mainWindowController.main_labelStatus.setText("Autotest failed.");
                        }
                    });
                }
            }
        });

        thread_autotest.setName("autotest thread");
        thread_autotest.setDaemon(true);
        thread_autotest.start();

    }

}
