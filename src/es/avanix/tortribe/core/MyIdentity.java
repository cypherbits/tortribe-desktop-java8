package es.avanix.tortribe.core;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.utils.TextUtils;
import java.io.IOException;
import java.io.StringReader;
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

/**
 *
 * @author imhotep
 */
public class MyIdentity extends Identity {

    private static String hs_private_key;

    private static MyIdentity myidentity;

    private static PrivateKey privatekey;

    /**
     *
     * @param hs_private_key
     * @param nick
     * @param onion
     * @param port
     */
    public MyIdentity(String hs_private_key, String nick, OnionAdress onion, int port) {
        super(nick, onion, port, null);

        MyIdentity.hs_private_key = hs_private_key;

        //TODO: get public key from private key
    }

    public static MyIdentity getMyidentity() {
        return MyIdentity.myidentity;
    }

    public static void setMyIdentity(MyIdentity mid) {
        MyIdentity.myidentity = mid;
    }

    public String getHs_private_key() {
        return MyIdentity.hs_private_key;
    }

    public void setHs_private_key(String hs_private_key) {
        MyIdentity.hs_private_key = hs_private_key;
    }

    //TODO get private key, PrivateKey and String. parse etc...
    public static PrivateKey getPrivateKey() {

        if (MyIdentity.privatekey == null) {

           //TODO: get Private key object from string

            return null;

        } else {
            return MyIdentity.privatekey;
        }

    }

    public static byte[] signMessage(String message) {
        //TODO: sign message ED25519

        return null;
    }

}
