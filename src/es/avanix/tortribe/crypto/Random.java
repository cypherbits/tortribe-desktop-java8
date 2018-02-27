
package es.avanix.tortribe.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author imhotep
 */
public class Random {
    
    private static SecureRandom random = new SecureRandom();
    
    public static String getRandom() {
        return new BigInteger(130, random).toString(32);
    }
    
}
