package es.avanix.tortribe.actionhandlers;

import es.avanix.tortribe.core.FriendIdentity;
import es.avanix.tortribe.core.FriendsManager;
import es.avanix.tortribe.core.OnionAdress;
import es.avanix.tortribe.net.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author imhotep
 */
public class ActionHandler extends ActionEvent implements Runnable {

    private Connection con;
    private String message;
    private Thread thread;

    public ActionHandler(Connection con, String message) {
        this.con = con;
        this.message = message;

        this.thread = new Thread(this);
        this.thread.setName("action handler for connection " + con.getThread().getName());
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public Connection getConnection() {
        return this.con;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void run() {
        System.out.println("Received: " + message);

        JSONObject obj = new JSONObject(message);

        if (!obj.isNull("action")) {
            String action = obj.getString("action");

            if (this.con.getIdentity() == null) {

                //MUST have identity json
                //TODO: check if rOnionString, port, nick, public_key exists too
                if (!obj.isNull("identity") && !obj.isNull("signature")) {
                    //Has identity json

                    JSONObject rIdentity = obj.getJSONObject("identity");

                    String signature = obj.getString("signature");

                    if (!rIdentity.isNull("onion") && !rIdentity.isNull("port") && !rIdentity.isNull("nick") && !rIdentity.isNull("public_key")) {

                        String rOnionString = rIdentity.getString("onion");
                        int rPort = rIdentity.getInt("port");
                        String rNick = rIdentity.getString("nick");
                        String rPublicKey = rIdentity.getString("public_key");

                        FriendIdentity localFI = FriendsManager.getFriend(rOnionString);

                        try {

                            int localStatus = FriendIdentity.STATUS_NOFRIEND;

                            if (localFI != null) {
                                localStatus = localFI.getStatus();

                                if (localStatus == FriendIdentity.STATUS_BLOCKED) {
                                    //Contact is blocked, so we drop the request right now
                                    //We can put this before verifying the signature on this message...
                                    this.con.closeConnection();
                                    return;
                                }

                            }

                            FriendIdentity remoteFI = new FriendIdentity(localStatus, rNick, new OnionAdress(rOnionString), rPort, rPublicKey);

                            //verify sign message
                            if (remoteFI.verifyMessage(rIdentity.toString(), signature)) {
                                //verify OK

                                System.out.println("Identity signature verification OK");

                                if (remoteFI.verifyOnion()) {

                                    switch (action) {
                                        case "autotest":

                                            new AutoTest(this, remoteFI);

                                            break;

                                        case "add_friend":

                                            new AddFriend(this, remoteFI);

                                            break;

                                        case "add_friend_ok":

                                            new AddFriendOk(this, remoteFI);

                                            break;

                                        case "add_friend_accept":

                                            new AddFriendAccept(this, remoteFI);

                                            break;

                                        case "connect":

                                            new Connect(this, remoteFI);

                                            break;

                                        default:

                                            //TODO: send error saying action not found
                                            System.err.println("Command/action not found " + action);

                                            this.con.closeConnection();

                                            break;
                                    }
                                } else {
                                    //error
                                    System.err.println("Public key from remote friend cannot be validated for onion sent");
                                }

                            } else {
                                //error
                                System.err.println("Identity signature is not valid");
                            }

                        } catch (Exception ex) {
                            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        //ERROR: identity sub objects required are not present
                        System.err.println("Error: identity sub objects required are not present.");
                    }

                } else {
                    //Error: identity and signature json needed

                    //SEND ERROR
                    System.err.println("Error: message was required an identity but it doesnt have one.");
                    this.con.closeConnection();
                }

            } else {
                //must not have identity json

                switch (action) {

                    case "add_friend_accept_ok":

                        new AddFriendAcceptOk(this);

                        break;

                    case "connect_ok":

                        new ConnectOk(this);

                        break;

                    case "message":

                        new Message(this);

                        break;
                        
                    case "getfilehashlist":
                        
                        new GetFileHashList(this);
                        
                        break;

                    default:

                        //TODO: send error saying action not found
                        break;
                }
            }

        } else {
            //TODO: send error saying action is required
            System.err.println("Message doesnt have action.");
            this.con.closeConnection();
        }
    }

}
