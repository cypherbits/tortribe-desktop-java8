package es.avanix.tortribe.gui.login;

import es.avanix.tortribe.core.SQLite;
import es.avanix.tortribe.core.TorControl;
import es.avanix.tortribe.main.Tortribe;
import static es.avanix.tortribe.main.Tortribe.APP_VERSION;
import static es.avanix.tortribe.main.Tortribe.LOCALE;
import static es.avanix.tortribe.main.Tortribe.getOSLocale;
import es.avanix.tortribe.main.test;
import es.avanix.tortribe.utils.OSValidator;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author imhotep
 */
public class AppLoaderController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label labelState;

    @FXML
    private ProgressBar pbProgress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void startOLD() {
        String tbdir = SQLite.getConfig("tbdir");
        if (tbdir == null) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("SELECT TOR BROWSER DIRECTORY");
            File selectedDirectory = null;

            while (selectedDirectory == null) {
                selectedDirectory = chooser.showDialog(labelState.getScene().getWindow());
            }

            SQLite.setConfig("tbdir", selectedDirectory.getAbsolutePath());
            tbdir = selectedDirectory.getAbsolutePath();
        }

        startTwo(tbdir, (Stage) labelState.getScene().getWindow());
    }

//    public void startOne() {
//        String tbdir = SQLite.getConfig("tbdir");
//        if (tbdir == null) {
//
//            Task<Void> task = new Task<Void>() {
//                @Override
//                protected Void call() throws Exception {
//                    if (OSValidator.isMac()) {
//                        updateMessage("Downloading Tor for Mac...");
//
//                        String url = "https://archive.torproject.org/tor-package-archive/torbrowser/7.5/TorBrowser-7.5-osx64_en-US.dmg";
//                        String filename = "TorBrowser-7.5-osx64_en-US.dmg";
//                        String expectedsha256 = "43a8dc0afd0a77e42766311eb54ad9fc8714f67fcd2d3582a3bcb98b22c2e629";
//
//                    } else if (OSValidator.isWindows()) {
//                        updateMessage("Downloading Tor for Windows...");
//
//                        String url = "https://archive.torproject.org/tor-package-archive/torbrowser/7.5/torbrowser-install-7.5_en-US.exe";
//                        String filename = "torbrowser-install-7.5_en-US.exe";
//                        String expectedsha256 = "81ccb9456118cf8fa755a3eafb5c514665fc69599cdd41e9eb36baa335ebe233";
//
//                    } else if (OSValidator.isUnix()) {
//                        updateMessage("Downloading Tor for Linux...");
//
//                        String url = "https://archive.torproject.org/tor-package-archive/torbrowser/7.5/tor-browser-linux64-7.5_en-US.tar.xz";
//                        String filename = "tor-browser-linux64-7.5_en-US.tar.xz";
//                        String expectedsha256 = "67735b807da20fc3a94978f40c39d034d33c74310ea75622cdf91f09cbc648c5";
//
//                    }
//
//                    return null;
//                }
//            };
//
//            labelState.textProperty().bind(task.messageProperty());
//            pbProgress.progressProperty().bind(task.progressProperty());
//
//            final Thread taskThread = new Thread(task, "tor-downloader");
//            taskThread.setDaemon(true);
//            taskThread.start();
//            try {
//                taskThread.join();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(AppLoaderController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            startTwo(tbdir, (Stage) labelState.getScene().getWindow());
//
//        } else {
//            startTwo(tbdir, (Stage) labelState.getScene().getWindow());
//        }
//    }

    public void startTwo(String tbdir, Stage stage) {

//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                updateMessage("Connecting to Tor control port...");
//
//                TorControl.init(tbdir);
//
////                String listenPORTs = SQLite.getConfig("listenPORT");
////                if (listenPORTs != null) {
////                    listenPORT = Integer.parseInt(listenPORTs);
////                }
//
//                return null;
//            }
//        };
//
//        labelState.textProperty().bind(task.messageProperty());
//        pbProgress.progressProperty().bind(task.progressProperty());
//
//        final Thread taskThread = new Thread(task, "tor-start");
//        taskThread.setDaemon(true);
//        taskThread.start();
//        try {
//            taskThread.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(AppLoaderController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        String listenPORTs = SQLite.getConfig("listenPORT");
        if (listenPORTs != null) {
            Tortribe.listenPORT = Integer.parseInt(listenPORTs);
        }

        TorControl.init(tbdir);
//        
//        test.onion2();
//        System.exit(0);

        try {
            if (getOSLocale().equals("es")) {
                LOCALE = "es";
            } else {
                LOCALE = "en";
            }

            Parent root = FXMLLoader.load(getClass().getResource("/es/avanix/tortribe/gui/login/Login.fxml"));

            Scene scene = new Scene(root);
            stage.setTitle(Tortribe.APP_NAME + " " + APP_VERSION);
            stage.setResizable(false);
            //stage.getIcons().add(new Image(getClass().getResourceAsStream("meta.png")));

            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Tortribe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
