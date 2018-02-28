package es.avanix.tortribe.core;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.crypto.Base32;
import es.avanix.tortribe.crypto.SHAHash;
import java.nio.charset.Charset;
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
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

/**
 *
 * @author imhotep
 */
public class Identity {

    private String nick;
    private OnionAdress onion;
    private int port;
    private String hs_public_key;

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

    /**
     *
     * @param message
     * @param signature
     * @return
     */
    public boolean verifyMessage(String message, String signature) {

        try {
            byte[] publicKeybytes = Base64.decode(this.hs_public_key);

            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
            EdDSAPublicKeySpec publicKey = new EdDSAPublicKeySpec(publicKeybytes, spec);
            EdDSAPublicKey pukey = new EdDSAPublicKey(publicKey);

            EdDSAEngine engine = new EdDSAEngine();
            engine.initVerify(pukey);

            byte[] msg = message.getBytes();
            byte[] sign = Base64.decode(signature);
            
            return engine.verifyOneShot(msg, sign);
        } catch (InvalidKeyException | SignatureException ex) {
            Logger.getLogger(Identity.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }

    public boolean verifyOnion() {

        byte[] publicKey = Base64.decode(this.hs_public_key);

        byte[] part1 = ".onion checksum".getBytes(Charset.forName("UTF-8"));
        byte[] part3 = new byte[1];
        part3[0] = (byte) 3;

        byte[] tochecksum = new byte[part1.length + publicKey.length + part3.length];
        System.arraycopy(part1, 0, tochecksum, 0, part1.length);
        System.arraycopy(publicKey, 0, tochecksum, part1.length, publicKey.length);
        System.arraycopy(part3, 0, tochecksum, part1.length + publicKey.length, part3.length);

        byte[] _sha3256 = SHAHash.getSHA3256(tochecksum);

        byte[] checksum = Arrays.copyOfRange(_sha3256, 0, 2);

        byte[] finalarray = new byte[publicKey.length + checksum.length + part3.length];
        System.arraycopy(publicKey, 0, finalarray, 0, publicKey.length);
        System.arraycopy(checksum, 0, finalarray, publicKey.length, checksum.length);
        System.arraycopy(part3, 0, finalarray, publicKey.length + checksum.length, part3.length);

        String generatedOnion = Base32.encode(finalarray).toLowerCase();

        return this.onion.getName().equals(generatedOnion);
    }
}
