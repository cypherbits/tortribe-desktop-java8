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
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/**
 *
 * @author imhotep Interesante:
 * https://code.briarproject.org/akwizgran/briar/commit/84b3670624a06c45b9d8ffe065a242323163324e
 */
public class TorControl {

    private static final String cookieDir = "Browser/TorBrowser/Data/Tor/";
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

//    public static void test(String privKey) {
//        try {
//            System.out.println("test de obtener public key y onion name:");
//
////            privKey = "-----BEGIN RSA PRIVATE KEY-----\n"
////                    + "MIICXAIBAAKBgQCsEGyNizZJb3PdUiugAQsTPx6FbFt63sPEFbRtfTX/t8PO0oe8"
////                    + "0yn4T1ZINT+VTfsXtQdWCFcg8dXS4fHCksIJ8DOp5D9B89veyhmuy2yKNpzd4LPs"
////                    + "82o27Oz6w6ucA35oU1ltY5RuE0KHA/kYF5tZT+qes9d7oHgMW3fIypQM7QIDAQAB"
////                    + "AoGAGkMrfnM4Jv9G7QoEtJhUK2zf6LeAQwHLWZuCfY+19UEENwY1R6HnbwoU/lJ6"
////                    + "UPylDZpI8120hsfCvjxUXUFvMjraoDcQBh77naypmW9fyHC0Y1RJcqSYhr+2e+iz"
////                    + "roS50CcbR/hIPeOnCFUCMz8oE1GIOKRraZjsLQgGBw8vyEECQQDjqLUAxiSsVI2K"
////                    + "Ak3XMzHntoakP/VKIExV1a9mIfptqIe2KpDi1Xv/sGWVZPipL09mQ8dAovZgFORs"
////                    + "K9JhjIvRAkEAwXv1LZUVA05iG8td9ds4nMgW+225haJhVRunnvZKYb8hAZ8vIEhy"
////                    + "4qPq61Pamr1b5RuqhONXWxyMRvLsuxuiXQJBAMDcy3o8YhUcDEw+Z3NIC58hIi9D"
////                    + "f6mscv7EaDM91cnQXCgXJ2cDNGkIJwbI419wlPMtuD8pz07WZCy/cxrw1gECQG54"
////                    + "AbI2zjqHBEjuQgVfVuFc9JI1QZlk7sGS+o8t+6X3ZDby1gtOkhmIkVYvGD8FInSa"
////                    + "6S7aRkQE9qqDhFoIGxkCQCoF0ikEMHA8snUEO3UNq4mvbNl+P7KBeVcgvQ8FVlQk"
////                    + "YdbHMh1Q7OKh3nVoap/a4ZfI1mwnNzfSHytt1Dhg6oY=\n"
////                    + "-----END RSA PRIVATE KEY-----";
//            privKey = privKey.replace("RSA1024:", "-----BEGIN RSA PRIVATE KEY-----\n");
//            privKey = privKey.concat("\n-----END RSA PRIVATE KEY-----");
//            privKey = TextUtils.insertPeriodically(privKey, "\n", 64);
//
//            System.out.println("private key: " + privKey);
//
//            // privKey = privKey.replace("\n", ""); //.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----BEGIN RSA PRIVATE KEY-----", "");
//            Security.addProvider(new BouncyCastleProvider());
//            PEMParser pemParser = new PEMParser(new StringReader(privKey));
//            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
//            Object object = pemParser.readObject();
//            KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
//
//            PrivateKey privateKey = kp.getPrivate();
//            PublicKey publicKey = kp.getPublic();
//
//            System.out.println(privateKey.toString());
//            System.out.println(publicKey.toString());
//
//            byte[] pubBytes = publicKey.getEncoded();
//            byte[] prvBytes = privateKey.getEncoded();
//
//            System.out.println(Base64.encode(pubBytes));
//            System.out.println(Base64.encode(prvBytes));
//
//            //convert public key to pkcs1
//            SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(pubBytes);
//            ASN1Primitive primitive = spkInfo.parsePublicKey();
//            byte[] publicKeyPKCS1 = primitive.getEncoded();
//
//            //pkcs1 to pem
////            PemObject pemObject = new PemObject("RSA PUBLIC KEY", publicKeyPKCS1);
////            StringWriter stringWriter = new StringWriter();
////            PemWriter pemWriter = new PemWriter(stringWriter);
////            pemWriter.writeObject(pemObject);
////            pemWriter.close();
////            String pemString = stringWriter.toString();
////
////            System.out.println(pemString);
//
//            //convert private key to pkcs1
//            PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(prvBytes);
//            ASN1Encodable encodable = pkInfo.parsePrivateKey();
//            ASN1Primitive primitive2 = encodable.toASN1Primitive();
//            byte[] privateKeyPKCS1 = primitive2.getEncoded();
//
//            //pkcs1 to pem
////            PemObject pemObject2 = new PemObject("RSA PRIVATE KEY", privateKeyPKCS1);
////            StringWriter stringWriter2 = new StringWriter();
////            PemWriter pemWriter2 = new PemWriter(stringWriter2);
////            pemWriter2.writeObject(pemObject2);
////            pemWriter2.close();
////            String pemString2 = stringWriter2.toString();
////
////            System.out.println(pemString2);
//
//            //generar onion test
//            //String publicKeyfinal = new String(publicKeyPKCS1);
//            String publicKeyfinal = Base64.encode(publicKeyPKCS1);
//
//            System.out.println(publicKeyfinal);
//
//            //String parahashear = publicKeyfinal.replace("-----BEGIN RSA PUBLIC KEY-----\n", "").replace("\n", "").replace("-----END RSA PUBLIC KEY-----", "").substring(22, publicKeyfinal.length());
//            String parahashear = publicKeyfinal.substring(16, publicKeyfinal.length());
//            
//            System.out.println("PK a hash: " + parahashear);
//
//            byte[] sha1 = SHAHash.getSHA1(publicKeyPKCS1);
//            //String sha1 = new String(SHAHash.toSHA1(publicKeyfinal.getBytes()));
//
//            System.out.println("hash sha1: " + Base64.encode(sha1));
//
//            String base32 = Base32.encode(sha1);
//            
//            String genonion = base32.substring(0,16);
//
//            System.out.println(genonion.toLowerCase());
//            
//            
//            //TEST FIRMAR
//            Signature signature = Signature.getInstance("SHA1withRSA", "BC");
//        signature.initSign(privateKey, new SecureRandom());
//        byte[] message = "hola soy edu feliz navidad".getBytes();
//        signature.update(message);
//        byte[] sigBytes = signature.sign();
//        
//        System.out.println("sign bytes: "+ Base64.encode(sigBytes));
//
//        Signature signature1 = Signature.getInstance("SHA1withRSA", "BC");
//        signature1.initVerify(publicKey);
//        signature1.update(message);
//
//        boolean result = signature1.verify(sigBytes);
//        System.out.println("result = "+result);
//            
//
//        } catch (IOException ex) {
//            Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchProviderException ex) {
//            Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeyException ex) {
//            Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SignatureException ex) {
//            Logger.getLogger(TorControl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

}
