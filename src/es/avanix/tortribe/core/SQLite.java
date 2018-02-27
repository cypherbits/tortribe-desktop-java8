package es.avanix.tortribe.core;

import es.avanix.tortribe.main.Tortribe;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author imhotep
 */
public class SQLite {

    private static final String DBFILENAME = "tortribe.db";
    private static Connection connect;

    public static void init() {
        if (!new File(Tortribe.CONFIG_DATADIR).exists()) {
            new File(Tortribe.CONFIG_DATADIR).mkdir();
        }

        connect();

        try {
            PreparedStatement st = connect.prepareStatement("create table if not exists config(key text, value text)");
            st.execute();

            st = connect.prepareStatement("create table if not exists friends(id integer primary key, nick text, onion text, port integer, status integer, public_key text)");
            st.execute();

//            st = connect.prepareStatement("create table if not exists chat(id integer primary key, nick text, onion text, port integer, alias text)");
//            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(SQLite.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Connection getConnection() {
        return connect;
    }

    public static void connect() {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:" + Tortribe.CONFIG_DATADIR + "/" + SQLite.DBFILENAME);
            if (connect != null) {
                System.out.println("SQLite connection OK");
            }
        } catch (SQLException ex) {
            System.err.println("Can't connect to database\n" + ex.getMessage());
        }
    }

    public static void close() {
        try {
            connect.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setConfig(String key, String value) {
        try {
            ResultSet result;
            PreparedStatement stq = connect.prepareStatement("select count(*) as total from config where key=?");
            stq.setString(1, key);
            result = stq.executeQuery();
            if (result.getInt("total") > 0) {
                PreparedStatement st = connect.prepareStatement("update config set value=? where key=?");
                st.setString(1, value);
                st.setString(2, key);
                st.execute();
            } else {
                PreparedStatement st = connect.prepareStatement("insert into config (key, value) values (?,?)");
                st.setString(1, key);
                st.setString(2, value);
                st.execute();
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static String getConfig(String key) {
        ResultSet result;
        try {
            PreparedStatement st = connect.prepareStatement("select value from config where key=? limit 1");
            st.setString(1, key);
            result = st.executeQuery();
            return result.getString("value");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return null;

    }

}
