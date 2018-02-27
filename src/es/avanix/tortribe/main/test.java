package es.avanix.tortribe.main;

import es.avanix.tortribe.core.TorControl;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

/**
 *
 * @author imhotep
 */
public class test {

    public static void onion() {
//        try {
//            String[] str = {};
//
//            Map<Integer, String> onionConfig = new HashMap<Integer, String>();
//            onionConfig.put(Tortribe.listenPORT, "127.0.0.1:" + Tortribe.listenPORT);
//
//            Map<String, String> dice = TorControl.getConnection(str).addOnion("NEW:ED25519-V3", onionConfig);
//
//            System.out.println(dice.toString());
//
//            System.out.println("Try to get public key from private key");
//
//            Security.addProvider(new BouncyCastleProvider());
//            ECNamedCurveParameterSpec ecsp = ECNamedCurveTable.getParameterSpec("curve25519");
//            KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");
//            kpg.initialize(ecsp, new SecureRandom());
//            ECPoint publicKeyPoint = ecsp.getG().multiply(new BigInteger("41064391395585835474368114493246041522492081331069568852355680125134811285088"));
//            byte[] data = publicKeyPoint.getEncoded(true);
//            StringBuilder sb = new StringBuilder();
//            for (byte b : data) {
//                sb.append(String.format("%02x", b & 0xff));
//            }
//            System.out.println("recreated public key: " + sb.toString());
//
//        } catch (IOException ex) {
//            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidAlgorithmParameterException ex) {
//            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public static void main(String[] args) throws ClassNotFoundException {

    }

}
