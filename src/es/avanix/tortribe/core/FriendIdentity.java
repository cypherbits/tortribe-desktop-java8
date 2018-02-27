package es.avanix.tortribe.core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imhotep
 */
public class FriendIdentity extends Identity {

    private int status;
    /* 
    1 = waiting_for (friend request sent, waiting for friend to approval)
    2 = to_accept (friend request received, waiting for user to approval)
    3 = ok (friend is ok and validated as friend)
    4 = friend blocked 
     */
    
    public boolean connected;

    public static final int STATUS_NOFRIEND = 0;
    public static final int STATUS_WAITING = 1;
    public static final int STATUS_PENDING = 2;
    public static final int STATUS_OK = 3;
    public static final int STATUS_BLOCKED = 4;

    public FriendIdentity(int status, String nick, OnionAdress onion, int port, String hs_public_key) {
        super(nick, onion, port, hs_public_key);
        this.status = status;
        this.connected = false;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
    public static FriendIdentity getFriendIdentityFromString(String friendLink) {
        String[] arr = friendLink.split(":");

        if (arr.length == 2) {
            if (OnionAdress.isOnionValid(arr[0])) {
                try {
                    OnionAdress oa = new OnionAdress(arr[0]);

                    int port = Integer.parseInt(arr[1]);

                    return new FriendIdentity(STATUS_NOFRIEND, null, oa, port, null);
                } catch (Exception ex) {
                    Logger.getLogger(Identity.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return null;
    }

}
