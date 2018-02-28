package es.avanix.tortribe.core;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.crypto.Base32;
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
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

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

        //Get public key from private key
        byte[] privateKeybytes = Base64.decode(myidentity.hs_private_key.substring("ED25519-V3:".length()));
        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        EdDSAPrivateKeySpec privateKey = new EdDSAPrivateKeySpec(spec, privateKeybytes);
        super.setHsPublicKey(Base64.encode(privateKey.getA().toByteArray()));
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

    public static byte[] signMessage(String message) {
        try {
            byte[] privateKeybytes = Base64.decode(myidentity.hs_private_key.substring("ED25519-V3:".length()));

            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
            EdDSAPrivateKeySpec privateKey = new EdDSAPrivateKeySpec(spec, privateKeybytes);
            EdDSAPrivateKey prikey = new EdDSAPrivateKey(privateKey);

            EdDSAEngine engine = new EdDSAEngine();
            engine.initSign(prikey);

            return engine.signOneShot(message.getBytes());
        } catch (InvalidKeyException | SignatureException ex) {
            Logger.getLogger(MyIdentity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
