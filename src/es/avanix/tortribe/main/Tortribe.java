package es.avanix.tortribe.main;

import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.SQLite;
import es.avanix.tortribe.gui.login.AppLoaderController;
import es.avanix.tortribe.gui.main.MainWindowController;
import es.avanix.tortribe.utils.AlertHelper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author imhotep
 */
public class Tortribe extends Application {

    public static final String APP_VERSION = "v0.2 prototype x/02/2018";
    public static final String APP_NAME = "TorTribe";
    public static final int TOR_SOCKS_PROXY_PORT = 9150;
    public static final int TOR_CONTROL_PORT = 9151;
    public static final String CONFIG_DATADIR = "TortribeData";
    //public static final String CONFIG_TORDIR = "Tor";
    public static final boolean CONFIG_DEBUG = true;

    public static Thread listenThread;

    public static int listenPORT = 11113;

    public static String LOCALE;

    public static MainWindowController mainWindowController;

    public static ObservableList<FriendIdentity> friendsListView;

    public static Map<String, Tab> tabs;

    public static TabPane main_tabs;
    
    public static TableView myFiles_tableView;

    @Override
    public void start(Stage stage) {

        //Thread.setDefaultUncaughtExceptionHandler(Tortribe::showError);

        //friendsConnections = new HashMap<String, Connection>();

        tabs = new HashMap<String, Tab>();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/avanix/tortribe/gui/login/AppLoader.fxml"));
            Parent root = (Parent) loader.load();
            AppLoaderController controller = (AppLoaderController) loader.getController();

            Scene scene = new Scene(root);
            stage.setTitle("Loading " + Tortribe.APP_NAME + " " + APP_VERSION);
            stage.setResizable(false);
            //stage.getIcons().add(new Image(getClass().getResourceAsStream("meta.png")));

            stage.setScene(scene);
            stage.setOnShown((WindowEvent event) -> {
                //controller.startOne();
                controller.startOLD();
            });
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(Tortribe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        SQLite.init();

        launch(args);
    }

    public static String getOSLocale() {
        Locale currentLocale = Locale.getDefault();

//        System.out.println(currentLocale.getDisplayLanguage());
//        System.out.println(currentLocale.getDisplayCountry());
//
//        System.out.println(currentLocale.getLanguage());
//        System.out.println(currentLocale.getCountry());
//
//        System.out.println(System.getProperty("user.country"));
//        System.out.println(System.getProperty("user.language"));
        return currentLocale.getLanguage();
    }

    public static void showError(Thread thread, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            AlertHelper.newExceptionAlert(e);
        } else {
            AlertHelper.newExceptionAlert(e);
        }
    }

}
