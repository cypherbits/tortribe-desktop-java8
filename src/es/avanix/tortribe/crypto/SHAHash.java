package es.avanix.tortribe.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jcajce.provider.digest.SHA3;

/**
 *
 * @author imhotep
 */
public class SHAHash {

    public static byte[] getSHA256(byte[] original) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(original);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SHAHash.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static byte[] getSHA256fromFile(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            };

            return md.digest();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SHAHash.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SHAHash.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SHAHash.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static byte[] getSHA1(byte[] convertme) {
        try {
            java.security.MessageDigest d = null;
            d = java.security.MessageDigest.getInstance("SHA-1");
            d.reset();
            d.update(convertme);
            return d.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SHAHash.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static byte[] getSHA3(byte[] input){
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(input);
        return digest;
    }


}
