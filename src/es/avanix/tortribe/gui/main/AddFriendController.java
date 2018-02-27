/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.avanix.tortribe.gui.main;

import es.avanix.tortribe.actionhandlers.AddFriend;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.utils.AlertHelper;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author imhotep
 */
public class AddFriendController implements Initializable {

    @FXML
    private TextField addfriend_txtFriendId;

    @FXML
    private Button addfriend_btnAdd;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {

        String friendLink = addfriend_txtFriendId.getText();

        FriendIdentity friendIdentity = FriendIdentity.getFriendIdentityFromString(friendLink);

        if (friendIdentity != null && !friendIdentity.getOnionLink().equals(MyIdentity.getMyidentity().getOnionLink())) {

            FriendIdentity localIdentity = FriendsManager.getFriend(friendIdentity.getOnion().getName());

            if (localIdentity == null) {

                AddFriend.send(friendIdentity);

                Scene s = (Scene) addfriend_btnAdd.getScene();
                s.getWindow().hide();

            } else {
                System.err.println("Friend link: friend is already on local database.");
                AlertHelper.newWarningAlert("Friend already in local database", addfriend_txtFriendId.getText() + " is already in the local database.");
                addfriend_txtFriendId.setText("");
            }

        } else {
            System.err.println("Friend link invalid.");
            AlertHelper.newWarningAlert("Friend link invalid", addfriend_txtFriendId.getText() + " is not a valid friend link.");
            addfriend_txtFriendId.setText("");
        }

    }

}
