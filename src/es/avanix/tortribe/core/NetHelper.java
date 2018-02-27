package es.avanix.tortribe.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imhotep
 */
public class NetHelper {

    private static final int minPort = 49152;
    private static final int maxPort = 65535;

    private static int getRandomPortNumber() {
        Random rn = new Random();
        int a = rn.nextInt(maxPort - minPort + 1) + minPort;
        return a;
    }

    public static int getRandomPort() {
        int n = getRandomPortNumber();
        try {
            new ServerSocket(n).close();
            return n;
        } catch (IOException ex) {
            Logger.getLogger(NetHelper.class.getName()).log(Level.SEVERE, null, ex);
            return getRandomPort();
        }
    }

}
