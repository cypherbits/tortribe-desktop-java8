package es.avanix.tortribe.gui.login;

import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.core.NetHelper;
import es.avanix.tortribe.core.OnionAdress;
import es.avanix.tortribe.core.SQLite;
import es.avanix.tortribe.core.TorControl;
import es.avanix.tortribe.main.Tortribe;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author imhotep
 */
public class JoinController implements Initializable {

    @FXML
    private Button join_btnJoin;

    @FXML
    private TextField join_txtNick;

    private ResourceBundle rb;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
    }

    @FXML
    private void handleJoinButtonAction(ActionEvent event) {

        join_btnJoin.setDisable(true);

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                Tortribe.listenPORT = NetHelper.getRandomPort();
                SQLite.setConfig("listenPORT", String.valueOf(Tortribe.listenPORT));
                System.out.println("puerto elegido: " + Tortribe.listenPORT);

                try {

                    String[] str = {};

                    Map<Integer, String> onionConfig = new HashMap<Integer, String>();
                    onionConfig.put(Tortribe.listenPORT, "127.0.0.1:" + Tortribe.listenPORT);

                    Map<String, String> dice = TorControl.getConnection(str).addOnion("NEW:ED25519-V3", onionConfig);

                    OnionAdress oa = new OnionAdress(dice.get("onionAddress"));
                    
                    SQLite.setConfig("onionAddress", dice.get("onionAddress"));
                    SQLite.setConfig("onionPrivKey", dice.get("onionPrivKey"));

                    String onionAddress = SQLite.getConfig("onionAddress");
                    String onionPrivKey = SQLite.getConfig("onionPrivKey");

                    SQLite.setConfig("nick", join_txtNick.getText());

                    MyIdentity myIdentity = new MyIdentity(onionPrivKey, join_txtNick.getText(), oa, Tortribe.listenPORT);
                    MyIdentity.setMyIdentity(myIdentity);

                    System.out.println("Nick ahora es: " + MyIdentity.getMyidentity().getNick());
                    System.out.println("Tu onion esta configurado para ser: " + MyIdentity.getMyidentity().getOnion().getURI());

                } catch (IOException ex) {
                    Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                } catch (Exception ex) {
                    Logger.getLogger(JoinController.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };
        task.setOnSucceeded(e -> {

            showMainWindow();

        });
        Thread thread = new Thread(task);
        thread.setName("Tor Control Join");
        thread.start();

    }

    private void showMainWindow() {
        try {

            Parent root;
            root = FXMLLoader.load(getClass().getResource("/es/avanix/tortribe/gui/main/MainWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle(Tortribe.APP_NAME + " " + Tortribe.APP_VERSION);
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

            Scene s = (Scene) join_btnJoin.getScene();
            s.getWindow().hide();

        } catch (Exception ex) {
            Logger.getLogger(JoinController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
