package es.avanix.tortribe.gui.login;

import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.core.OnionAdress;
import es.avanix.tortribe.core.SQLite;
import es.avanix.tortribe.core.TorControl;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.utils.AlertHelper;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author imhotep
 */
public class LoginController implements Initializable {

    @FXML
    private Hyperlink login_local_btnJoin;

    @FXML
    private Button login_local_btnLogin;

    @FXML
    private Label login_local_labelNick;

    @FXML
    private Label login_local_labelOnion;

    @FXML
    private Tab login_tabLogin;

    private ResourceBundle rb;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;

        String onionAddress = SQLite.getConfig("onionAddress");
        String onionPrivKey = SQLite.getConfig("onionPrivKey");

        if (onionAddress != null && onionPrivKey != null) {
            login_local_btnLogin.setDisable(false);

            login_local_labelNick.setText(SQLite.getConfig("nick"));
            login_local_labelOnion.setText(onionAddress + ":" + Tortribe.listenPORT);
        }
    }

    @FXML
    private void handleLocalJoinButtonAction(ActionEvent event) {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/es/avanix/tortribe/gui/login/Join.fxml"));
            Stage stage = new Stage();
            //stage.setTitle(rb.getString("about.title"));
            stage.setTitle(Tortribe.APP_NAME + " - create user/address");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            Scene s = (Scene) login_local_btnJoin.getScene();
            s.getWindow().hide();

        } catch (IOException ex) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLocalLoginButtonAction(ActionEvent event) {

        String onionAddress = SQLite.getConfig("onionAddress");
        String onionPrivKey = SQLite.getConfig("onionPrivKey");
        String nick = SQLite.getConfig("nick");

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws IOException {
                
                //Init TorControl again to get the password cookie in case TorBrowser was not started when this app was started.
                String tbdir = SQLite.getConfig("tbdir");
                TorControl.init(tbdir);

                String[] str = {};

                try {

                    Map<Integer, String> onionConfig = new HashMap<Integer, String>();
                    onionConfig.put(Tortribe.listenPORT, "127.0.0.1:" + Tortribe.listenPORT);

                    Map<String, String> dice = TorControl.getConnection(str).addOnion(onionPrivKey, onionConfig);

                    //TEST RSA SIGNATURES ETC
                    //TorControl.test(onionPrivKey);
                    System.out.println(dice.toString());

                } catch (IOException ex) {
                    Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            AlertHelper.newErrorAlert("Tor Control connection error", "Please check Tor or Tor Browser is running at port " + Tortribe.TOR_CONTROL_PORT + ""
                                    + "\n Remember to run Tor Browser/Tor daemon before starting TorTribe.");

                        }
                    });

                    this.cancel(true);

                }

                return null;
            }
        };
        task.setOnSucceeded(e -> {

            try {

                MyIdentity myIdentity = new MyIdentity(onionPrivKey, nick, new OnionAdress(onionAddress), Tortribe.listenPORT);
                MyIdentity.setMyIdentity(myIdentity);

                System.out.println("Nick ahora es: " + MyIdentity.getMyidentity().getNick());
                System.out.println("Tu onion esta configurado para ser: " + MyIdentity.getMyidentity().getOnion().getURI());

                Parent root;
                root = FXMLLoader.load(getClass().getResource("/es/avanix/tortribe/gui/main/MainWindow.fxml"));
                Stage stage = new Stage();
                stage.setTitle(Tortribe.APP_NAME + " " + Tortribe.APP_VERSION);
                stage.setScene(new Scene(root));
                stage.setMinWidth(800);
                stage.setMinHeight(600);
                stage.show();

                Scene s = (Scene) login_local_btnLogin.getScene();
                s.getWindow().hide();

            } catch (Exception ex) {
                Logger.getLogger(JoinController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        task.setOnFailed(e -> {

            Logger.getLogger(JoinController.class.getName()).log(Level.SEVERE, null, e);

            AlertHelper.newExceptionAlert((Exception) e.getSource().getException());

            System.exit(1);
        });
        Thread thread = new Thread(task);
        thread.setName("Login Tor Control Thread");
        thread.setDaemon(true);
        thread.start();

    }

}
