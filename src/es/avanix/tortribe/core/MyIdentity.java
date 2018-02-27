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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

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

        try {

            Security.addProvider(new BouncyCastleProvider());

            String privKey = MyIdentity.hs_private_key;
            privKey = privKey.replace("RSA1024:", "-----BEGIN RSA PRIVATE KEY-----\n");
            privKey = privKey.concat("\n-----END RSA PRIVATE KEY-----");
            privKey = TextUtils.insertPeriodically(privKey, "\n", 64);

            PEMParser pemParser = new PEMParser(new StringReader(privKey));
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            Object object = pemParser.readObject();
            KeyPair kp = converter.getKeyPair((PEMKeyPair) object);

            MyIdentity.privatekey = kp.getPrivate();
            PublicKey publicKey = kp.getPublic();

            super.setHsPublicKey(Base64.encode(publicKey.getEncoded()));

        } catch (IOException ex) {
            Logger.getLogger(MyIdentity.class.getName()).log(Level.SEVERE, null, ex);
        }

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

            try {
                Security.addProvider(new BouncyCastleProvider());

                String privKey = MyIdentity.hs_private_key;
                privKey = privKey.replace("RSA1024:", "-----BEGIN RSA PRIVATE KEY-----\n");
                privKey = privKey.concat("\n-----END RSA PRIVATE KEY-----");
                privKey = TextUtils.insertPeriodically(privKey, "\n", 64);

                PEMParser pemParser = new PEMParser(new StringReader(privKey));
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
                Object object = pemParser.readObject();
                KeyPair kp = converter.getKeyPair((PEMKeyPair) object);

                MyIdentity.privatekey = kp.getPrivate();

                return MyIdentity.privatekey;
            } catch (IOException ex) {
                Logger.getLogger(MyIdentity.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;

        } else {
            return MyIdentity.privatekey;
        }

    }

    public static byte[] signMessage(String message) {
        try {
            Security.addProvider(new BouncyCastleProvider());

            Signature signature = Signature.getInstance("SHA1withRSA", "BC");
            signature.initSign(MyIdentity.getPrivateKey(), new SecureRandom());
            byte[] messagebytes = message.getBytes();
            signature.update(messagebytes);
            return signature.sign();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MyIdentity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(MyIdentity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MyIdentity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(MyIdentity.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
