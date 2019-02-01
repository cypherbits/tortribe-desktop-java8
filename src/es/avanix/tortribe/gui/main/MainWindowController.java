package es.avanix.tortribe.gui.main;

import es.avanix.tortribe.actionhandlers.AddFriendAccept;
import es.avanix.tortribe.actionhandlers.AutoTest;
import es.avanix.tortribe.core.FileManager;
import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.core.ListenThread;
import es.avanix.tortribe.core.MyIdentity;
import es.avanix.tortribe.gui.parts.ChatTabController;
import es.avanix.tortribe.gui.parts.FileInTable;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.net.ConnectionManager;
import es.avanix.tortribe.utils.AlertHelper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author imhotep
 */
public class MainWindowController implements Initializable {

    private ResourceBundle rb;

    @FXML
    private Button main_btnAddFriend;

    @FXML
    private ListView main_listFriends;

    @FXML
    private TabPane main_tabs;

    @FXML
    private Tab main_tabChatExample;

    @FXML
    private TableView myFiles_tableView;

    @FXML
    private Circle main_circleStatus;

    @FXML
    public Hyperlink main_linkInvite;

    @FXML
    private Label main_labelNick;

    @FXML
    public Label main_labelStatus;

    @FXML
    private TableColumn column_filename;
    @FXML
    private TableColumn column_filehash;

    public Circle getMain_circleStatus() {
        return main_circleStatus;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;

        main_linkInvite.setVisible(false);

        main_labelNick.setText(MyIdentity.getMyidentity().getNick());
        main_linkInvite.setText(MyIdentity.getMyidentity().getOnionLink());

        Tortribe.mainWindowController = this;
        Tortribe.main_tabs = main_tabs;

        Tortribe.listenThread = new Thread(new ListenThread());
        Tortribe.listenThread.setDaemon(true);
        Tortribe.listenThread.setName("Listen thread");
        Tortribe.listenThread.start();

        Tooltip t = new Tooltip("Copy link.");
        Tooltip.install(main_linkInvite, t);

        Tortribe.myFiles_tableView = myFiles_tableView;

        //GUI test 
//        ObservableList<String> items = FXCollections.observableArrayList(
//                "Single", "Double", "Suite", "Family App");
//
//        main_listFriends.setItems(items);
        Tortribe.friendsListView = FXCollections.observableArrayList();

        main_listFriends.setCellFactory(param -> new ListCell<FriendIdentity>() {
            @Override
            protected void updateItem(FriendIdentity item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getNick() == null) {
                    setText(null);
                } else {
                    setText(item.getNick());
                    Circle circle = new Circle();
                    circle.setCenterX(100.0f);
                    circle.setCenterY(100.0f);
                    circle.setRadius(5.0f);

                    switch (item.getStatus()) {
                        case FriendIdentity.STATUS_BLOCKED:
                            circle.setFill(Color.web("#333"));
                            break;
                        case FriendIdentity.STATUS_WAITING:
                            circle.setFill(Color.web("#CCC"));
                            break;
                        case FriendIdentity.STATUS_PENDING:
                            circle.setFill(Color.web("#CCC"));
                            break;
                        case FriendIdentity.STATUS_OK:
                            if (item.connected) {
                                circle.setFill(Color.GREEN);
                            } else {
                                circle.setFill(Color.web("red"));
                            }
                            break;
                    }

                    setGraphic(circle);

                    //Context menu
                    ContextMenu contextMenu = new ContextMenu();

                    if (item.getStatus() == FriendIdentity.STATUS_OK || item.getStatus() == FriendIdentity.STATUS_BLOCKED) {
                        MenuItem blockItem = new MenuItem();
                        if (item.getStatus() == FriendIdentity.STATUS_BLOCKED) {
                            blockItem.setText("Unblock selected contact");
                        } else {
                            blockItem.setText("Block selected contact");
                        }

                        blockItem.setOnAction(event -> {
                            System.out.println("Block/unblock user.");

                            FriendIdentity fi = item;

                            if (ConnectionManager.getFriendConnections().containsKey(fi.getOnion().getName())) {
                                ConnectionManager.getFriendConnections().get(fi.getOnion().getName()).closeConnection();
                                ConnectionManager.getFriendConnections().remove(fi.getOnion().getName());
                            }

                            if (item.getStatus() == FriendIdentity.STATUS_BLOCKED) {
                                fi.setStatus(FriendIdentity.STATUS_OK);
                            } else {
                                fi.setStatus(FriendIdentity.STATUS_BLOCKED);
                            }

                            FriendsManager.addFriend(fi);
                            FriendsManager.populateFriends();

                        });

                        contextMenu.getItems().add(blockItem);
                    }

                    MenuItem deleteItem = new MenuItem();
                    deleteItem.setText("Delete selected contact");
                    deleteItem.setOnAction(event -> {
                        FriendsManager.deleteFriend(item);
                    });

                    contextMenu.getItems().add(deleteItem);

                    this.setContextMenu(contextMenu);

                }

            }
        });

        main_listFriends.setItems(Tortribe.friendsListView);

        FriendsManager.populateFriends();

        Tortribe.tabs.put("downloads", main_tabs.getTabs().get(0));
        Tortribe.tabs.put("myfiles", main_tabs.getTabs().get(1));
        //main_tabs.getTabs().removeAll(main_tabs.getTabs());
        main_tabs.getTabs().setAll(Tortribe.tabs.values());

        column_filename.setCellValueFactory(
    new PropertyValueFactory<FileInTable,String>("name")
);
        column_filehash.setCellValueFactory(
    new PropertyValueFactory<FileInTable,String>("hash")
);

        //Start self connection test
        AutoTest.send();

        Thread thread_filelist = new Thread(new Runnable() {
            @Override
            public void run() {
                File downloadDirectory = new File("./DownloadDir");
                if (!downloadDirectory.exists() || !downloadDirectory.isDirectory()) {
                    downloadDirectory.mkdirs();
                }

                FileManager.init("./DownloadDir");

                Platform.runLater(() -> {
                    FileManager.populate();
                });
            }
        });
        thread_filelist.setName("thread_filelist");
        thread_filelist.setDaemon(true);
        thread_filelist.start();

        main_listFriends.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    FriendIdentity fi = (FriendIdentity) main_listFriends.getSelectionModel()
                            .getSelectedItem();

                    System.out.println("Wanna start chat with friend " + fi.getOnion().getName());

                    switch (fi.getStatus()) {
                        case FriendIdentity.STATUS_BLOCKED:
                            System.err.println("Contact is blocked");
                            AlertHelper.newWarningAlert("Contact blocked", "This contact is blocked. Unblock it before starting a chat.");
                            break;
                        case FriendIdentity.STATUS_NOFRIEND:
                            System.err.println("Contact is not a friend, why is it on the list??");
                            AlertHelper.newErrorAlert("Error: this contact is not a friend", "Please try restarting, reseting the app. Contact developer.");
                            break;
                        case FriendIdentity.STATUS_PENDING:
                            System.out.println("Contact is pending local user accept");

                            Alert alert = new Alert(AlertType.CONFIRMATION);
                            alert.setTitle("Friend request");
                            alert.setHeaderText("Accept friend request?");
                            alert.setContentText("Friend information: \n\n\t"
                                    + "Nick: " + fi.getNick()
                                    + "\n\tFriendLink: " + fi.getOnionLink());

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                // ... user chose OK
                                System.out.println("user accepted friend request");

                                AddFriendAccept.send(fi);

                            } else {
                                // ... user chose CANCEL or closed the dialog
                                System.out.println("user cancelled friend request dialog");
                            }

                            break;
                        case FriendIdentity.STATUS_WAITING:
                            System.out.println("Contact is waiting for remote friend to accept");
                            AlertHelper.newInfoAlert("Friend request sent", "Friend request sent. Your friend hasnt accepted your friend request yet.");
                            break;
                        case FriendIdentity.STATUS_OK:
                            System.out.println("Contact is friend, starting connection and chat");

                            Tab tab = (Tab) Tortribe.tabs.get(fi.getOnion().getName());

                            if (tab != null) {
                                Platform.runLater(() -> {
                                    if (Tortribe.main_tabs.getTabs().indexOf(tab) == -1) {
                                        Tortribe.main_tabs.getTabs().add(tab);
                                    }
                                    main_tabs.getSelectionModel().select(tab);
                                });
                                ChatTabController ctc = (ChatTabController) tab.getUserData();
                                ctc.init();
                            } else {
                                try {

                                    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/es/avanix/tortribe/gui/parts/ChatTab.fxml"));

                                    Tab newtab = (Tab) loader.load();
                                    ChatTabController controller = loader.getController();
                                    controller.setIdentity(fi);
                                    controller.init();

                                    newtab.setUserData(controller);
                                    newtab.setText(fi.getNick());

                                    Tortribe.tabs.put(fi.getOnion().getName(), newtab);
                                    main_tabs.getTabs().setAll(Tortribe.tabs.values());

                                    main_tabs.getSelectionModel().select(newtab);

                                } catch (IOException ex) {
                                    Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            break;
                    }

                }
            }
        });

    }

    @FXML
    private void handleAddFriendButtonAction(ActionEvent event) {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/es/avanix/tortribe/gui/main/AddFriend.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add friend");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

//            Scene s = (Scene) main_btnAddFriend.getScene();
//            s.getWindow().hide();
        } catch (IOException ex) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleAboutButtonAction(ActionEvent event) {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/es/avanix/tortribe/gui/main/About.fxml"));
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleInviteLinkAction(ActionEvent event) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(MyIdentity.getMyidentity().getOnionLink());
        clipboard.setContent(content);
    }

    @FXML
    public void exitApplication(ActionEvent event) {

        Tortribe.listenThread.interrupt();

        Platform.exit();
    }

}
