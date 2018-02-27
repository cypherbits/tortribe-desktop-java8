package es.avanix.tortribe.core;

import es.avanix.tortribe.crypto.Base32;
import es.avanix.tortribe.crypto.SHAHash;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author imhotep
 */
public class Identity {

    private String nick;
    private OnionAdress onion;
    private int port;
    private String hs_public_key;

    private PublicKey publickey;

    public Identity(String nick, OnionAdress onion, int port, String hs_public_key) {
        this.nick = nick;
        this.onion = onion;
        this.port = port;
        this.hs_public_key = hs_public_key;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public OnionAdress getOnion() {
        return onion;
    }

    public void setOnion(OnionAdress onion) {
        this.onion = onion;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHsPublicKey() {
        return hs_public_key;
    }

    public void setHsPublicKey(String hs_public_key) {
        this.hs_public_key = hs_public_key;
    }

    public String getOnionLink() {
        return this.onion.getName() + ":" + String.valueOf(this.port);
    }

    public static Identity getIdentityFromString(String friendLink) {
        String[] arr = friendLink.split(":");

        if (arr.length == 2) {
            if (OnionAdress.isOnionValid(arr[0])) {
                try {
                    OnionAdress oa = new OnionAdress(arr[0]);

                    int port = Integer.parseInt(arr[1]);

                    return new Identity(null, oa, port, null);
                } catch (Exception ex) {
                    Logger.getLogger(Identity.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return null;
    }

    //TODO get public key, PublicKey and String, parse etc...
    public PublicKey getPublicKey() {

        if (this.publickey == null) {
            try {
                byte[] byteKey = Base64.decode(this.hs_public_key.getBytes());
                X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
                KeyFactory kf = KeyFactory.getInstance("RSA");

                this.publickey = kf.generatePublic(X509publicKey);

                return this.publickey;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        } else {
            return this.publickey;
        }

    }

    /**
     *
     * @param message
     * @param signature
     * @return
     */
    public boolean verifyMessage(String message, String signature) {

        try {
            byte[] signaturebytes = Base64.decode(signature);

            Security.addProvider(new BouncyCastleProvider());

            Signature signature1 = Signature.getInstance("SHA1withRSA", "BC");
            signature1.initVerify(this.getPublicKey());
            signature1.update(message.getBytes());

            return signature1.verify(signaturebytes);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Identity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Identity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(Identity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Identity.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public boolean verifyOnion() {
        //System.out.println("Original HS name: " + this.onion.getName());
        byte[] bytesPK = Base64.decode(this.hs_public_key);
        byte[] sliced = Arrays.copyOfRange(bytesPK, 22, bytesPK.length);
        byte[] sha1 = SHAHash.getSHA1(sliced);
        byte[] sha1_2 = Arrays.copyOfRange(sha1, 0, 10);
        String generatedOnion = Base32.encode(sha1_2).toLowerCase();
        //System.out.println("Generated HS name: " + generatedOnion);
        return this.onion.getName().equals(generatedOnion);
    }
}
