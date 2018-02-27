package es.avanix.tortribe.actionhandlers;

import es.avanix.tortribe.net.Connection;

/**
 *
 * @author imhotep
 */
public abstract class ActionEvent implements Runnable{
    
    public abstract Connection getConnection();
    
    public abstract String getMessage();
    
}
