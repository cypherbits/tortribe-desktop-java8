package es.avanix.tortribe.core;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.crypto.Base32;
import es.avanix.tortribe.crypto.SHAHash;
import es.avanix.tortribe.main.Tortribe;
import es.avanix.tortribe.utils.TextUtils;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.freehaven.tor.control.TorControlConnection;

/**
 *
 * @author imhotep Interesante:
 * https://code.briarproject.org/akwizgran/briar/commit/84b3670624a06c45b9d8ffe065a242323163324e
 */
public class TorControl {

    private static final String cookieDir = "tor-browser_en-US/Browser/TorBrowser/Data/Tor/";
    private static File cookieFile;
    private static byte[] cookie;

    public TorControl() {

    }

    public static void init(String tbdir) {
        cookieFile = new File(tbdir + "/" + cookieDir + "control_auth_cookie");
        if (!cookieFile.exists()) {
            System.err.println("Error: Tor cookie control file not found on " + cookieFile.toString());
            System.exit(1);
        } else {
            try {
                cookie = Files.readAllBytes(cookieFile.toPath());
            } catch (IOException ex) {
                Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static TorControlConnection getConnection(String[] args,
            boolean daemon) throws IOException {
        Socket s = new Socket("127.0.0.1", Tortribe.TOR_CONTROL_PORT);
        TorControlConnection conn = new TorControlConnection(s);
        conn.launchThread(daemon);
        conn.authenticate(cookie);
        return conn;
    }

    public static TorControlConnection getConnection(String[] args)
            throws IOException {
        return getConnection(args, true);
    }

}
