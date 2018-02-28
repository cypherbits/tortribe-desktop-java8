package es.avanix.tortribe.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author imhotep
 */
public class OnionAdress {

    private static final String DOMAIN = "onion";
    //private static final Pattern ONION = Pattern.compile("[a-z2-7]{16}");
    private static final Pattern ONIONV3 = Pattern.compile("[a-z2-7]{56}");

    private String name;

    public OnionAdress(String name) throws Exception {
        if (isOnionValid(name)) {
            this.name = name;
        } else {
            throw new Exception("Onion name string not valid, pattern: " + OnionAdress.ONIONV3.toString());
        }
    }

    public String getName() {
        return this.name;
    }

    public String getURI() {
        return this.name + "." + OnionAdress.DOMAIN;
    }

    public static boolean isOnionValid(String name) {
        Matcher matcher = OnionAdress.ONIONV3.matcher(name);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }
}
