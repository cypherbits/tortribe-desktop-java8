package es.avanix.tortribe.utils;

import javafx.scene.Node;

/**
 *
 * @author imhotep
 */
public class GetControllers {

    public static Object getController(Node node) {
        Object controller = null;
        do {
            controller = node.getUserData();
            node = node.getParent();
        } while (controller == null && node != null);
        return controller;
    }

}
