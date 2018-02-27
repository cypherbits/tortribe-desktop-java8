package es.avanix.tortribe.gui.parts;

import es.avanix.tortribe.actionhandlers.Connect;
import es.avanix.tortribe.actionhandlers.Message;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.net.Connection;
import es.avanix.tortribe.net.ConnectionManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author imhotep
 */
public class ChatTabController implements Initializable {

    @FXML
    private Button main_chatBtnSend;

    @FXML
    private TextArea main_txtChat;

    @FXML
    private TextField main_chatMessage;

    private FriendIdentity identity;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void init() {

        this.setDisabled(true);

        this.makeconnection();

    }

    @FXML
    private void handleSendMessageButtonAction(ActionEvent event) {

        String tosend = main_chatMessage.getText();

        main_chatMessage.setText("");

        if (!tosend.trim().equals("")) {
            main_txtChat.appendText("ME: " + tosend + "\n");

            Message.send(identity, tosend);
        }

    }

    public void setIdentity(FriendIdentity id) {
        this.identity = id;
    }

    private void makeconnection() {
        if (this.identity != null) {

            Thread thread_autotest = new Thread(new Runnable() {
                @Override
                public void run() {

                    Connection con = ConnectionManager.getConnection(identity);

                    if (con.getSocket().isConnected() && con.getThread().isAlive()) {
                        setDisabled(false);

                        Connect.send(identity);
                    } else {
                        System.err.println("Cannot connect.");
                        Platform.runLater(() -> {
                            main_txtChat.appendText("Cannot connect.");
                        });
                    }
                }
            });
            thread_autotest.setName("new chat tab connection thread to " + this.identity.getNick());
            thread_autotest.setDaemon(true);
            thread_autotest.start();

        } else {
            System.err.println("Identity is null, Cannot get friend from database");
        }

    }

    public void addMessage(String message) {
        this.main_txtChat.appendText(this.identity.getNick().toUpperCase() + ": " + message + "\n");
    }

    public TextArea getTextArea() {
        return this.main_txtChat;
    }

    public void setDisabled(boolean disabled) {

        if (disabled) {
            main_chatMessage.setEditable(false);
            main_chatBtnSend.setDisable(true);
        } else {
            main_chatMessage.setEditable(true);
            main_chatBtnSend.setDisable(false);
        }

    }

}
