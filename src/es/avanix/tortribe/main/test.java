package es.avanix.tortribe.main;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import es.avanix.tortribe.core.TorControl;
import es.avanix.tortribe.crypto.Base32;
import es.avanix.tortribe.crypto.SHAHash;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.Utils;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

/**
 *
 * @author imhotep
 */
public class test {

    static final byte[] TEST_SEED = Utils.hexToBytes("0000000000000000000000000000000000000000000000000000000000000000");
    static final byte[] TEST_PK = Utils.hexToBytes("3b6a27bcceb6a42d62a3a8d02a6f0d73653215771de243a63ac048a18b59da29");

//    public static void onion() {
//
//        //Curve25519 de https://github.com/neilalexander/jnacl
//        try {
//            String[] str = {};
//
//            Map<Integer, String> onionConfig = new HashMap<Integer, String>();
//            onionConfig.put(Tortribe.listenPORT, "127.0.0.1:" + Tortribe.listenPORT);
//            Map<String, String> dice = TorControl.getConnection(str).addOnion("NEW:ED25519-V3", onionConfig);
//
//            System.out.println(dice.toString());
//
//            String privKey = dice.get("onionPrivKey").toString().substring("ED25519-V3:".length());
//
//            byte[] privateKey = Base64.decode(privKey);
//
//            System.out.println("Try to get public key from private key" + privateKey.length + ": " + privKey);
//
//            byte[] publicKey = new byte[32];
//            int result = curve25519.crypto_scalarmult_base(publicKey, privateKey);
//
//            System.out.println("public key: " + Base32.encode(publicKey).toLowerCase());
//
//            byte[] part1 = ".onion checksum".getBytes();
//            byte[] part3 = "\u0003".getBytes();
//
//            byte[] tochecksum = new byte[part1.length + publicKey.length + part3.length];
//            System.arraycopy(part1, 0, tochecksum, 0, part1.length);
//            System.arraycopy(publicKey, 0, tochecksum, part1.length, publicKey.length);
//            System.arraycopy(part3, 0, tochecksum, part1.length + publicKey.length, part3.length);
//
//            System.out.println(new String(tochecksum));
//
//            byte[] _sha3256 = SHAHash.getSHA3256(tochecksum);
//
//            byte[] checksum = Arrays.copyOfRange(_sha3256, 0, 1);
//
//            System.out.println("checksum: " + Base32.encode(checksum).toLowerCase());
//
//            String generatedOnion = Base32.encode(publicKey) + Base32.encode(checksum) + Base32.encode(part3);
//
//            System.out.println("generated onion: " + generatedOnion.length() + ": " + generatedOnion.toLowerCase());
//
//        } catch (IOException ex) {
//            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public static void onion2() {
        try {
            String[] str = {};

            Map<Integer, String> onionConfig = new HashMap<Integer, String>();
            onionConfig.put(Tortribe.listenPORT, "127.0.0.1:" + Tortribe.listenPORT);
            Map<String, String> dice = TorControl.getConnection(str).addOnion("NEW:ED25519-V3", onionConfig);

            System.out.println(dice.toString());

            String privKey = dice.get("onionPrivKey").toString().substring("ED25519-V3:".length());

            byte[] privateKey = Base64.decode(privKey);

            System.out.println("Try to get public key from private key" + privateKey.length + ": " + privKey);

            EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
            EdDSAPrivateKeySpec encoded = new EdDSAPrivateKeySpec(spec, privateKey);

            System.out.println("Generated public key: " + Base32.encode(encoded.getA().toByteArray()).toLowerCase());
            System.out.println("Generated private key: " + Base32.encode(encoded.geta()).toLowerCase());

            //-------------
            byte[] publicKey = encoded.getA().toByteArray();

            byte[] part1 = ".onion checksum".getBytes(Charset.forName("UTF-8"));
            byte[] part3 = new byte[1];
            part3[0] = (byte) 3;

            System.out.println("longitud: " + part3.length);

            byte[] tochecksum = new byte[part1.length + publicKey.length + part3.length];
            System.arraycopy(part1, 0, tochecksum, 0, part1.length);
            System.arraycopy(publicKey, 0, tochecksum, part1.length, publicKey.length);
            System.arraycopy(part3, 0, tochecksum, part1.length + publicKey.length, part3.length);

//String tochecksum =".onion checksum" + new String(publicKey) + 3;

            System.out.println(new String(tochecksum));

            byte[] _sha3256 = SHAHash.getSHA3256(tochecksum);

            System.out.println("sha: " + _sha3256.length + ": " + Base32.encode(_sha3256).toLowerCase());

            byte[] checksum = Arrays.copyOfRange(_sha3256, 0, 2);
            //checksum = new String(_sha3256).substring(0, 2).getBytes(Charset.forName("UTF-8"));

            System.out.println("checksum: " + checksum.length + ": " + Base32.encode(checksum).toLowerCase());

            byte[] finalarray = new byte[publicKey.length + checksum.length + part3.length];
            System.arraycopy(publicKey, 0, finalarray, 0, publicKey.length);
            System.arraycopy(checksum, 0, finalarray, publicKey.length, checksum.length);
            System.arraycopy(part3, 0, finalarray, publicKey.length + checksum.length, part3.length);

            String generatedOnion = Base32.encode(finalarray);

            System.out.println("generated onion: " + generatedOnion.length() + ": " + generatedOnion.toLowerCase());

        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {

    }

}
