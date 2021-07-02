import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DB class
 */
public class DB {
    String dbHost = "134.108.190.89";
    int dbPort = 1433;
    String dbName = "SWB_DB2_Projekt";
    String dbUser = "wkb4";
    String dbPass = "wkb4";

    /*
     * For docker only:
     *
     * String dbHost = "127.0.0.1";
     * int dbPort = 14331;
     * String dbName = "storage";
     * String dbUser = "storage";
     * String dbPass = "Password1!";
     */

    String connectionUrl = "jdbc:sqlserver://"+dbHost+":"+dbPort+";databaseName="+dbName;

    Connection con = null;
    ResultSet rs = null;

    /**
     * Checks if user has entered correct login data and responses with object[boolean, msg]
     * @param username the eusername
     * @param password the password
     * @return Object[] with [0]=true if all is okay, else [0]=false and [1] with error message
     * @throws SQLException sql exception
     */
    public Object[] login(String username, String password) throws SQLException {
        Object[] obj = new Object[2];
        obj[0] = false;
        CallableStatement cs;
        con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
        cs = this.con.prepareCall("{call dbo.Schnell_Login(?)}");
        cs.setString(1, username.toLowerCase());

        rs = cs.executeQuery();
        if(rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
            obj[0] = true;
            obj[1] = rs.getString("id");
        }
        cs.close();
        return obj;
    }

    /**
     * Gets the accounts
     * @param userId the userId
     * @return all accounts to given userId
     */
    public List<Account> getPasswordList(int userId) {
        try {
            CallableStatement cs;
            con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
            cs = this.con.prepareCall("{call dbo.Schnell_GetAccounts(?)}");
            cs.setInt(1, userId);
            rs = cs.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while(rs.next()) {
                Account acc = new Account(  rs.getInt("id"),
                                            rs.getString("favicon"),
                                            userId,
                                            rs.getString("url"),
                                            rs.getString("name"),
                                            rs.getString("account"),
                                            rs.getString("password"));
                accounts.add(acc);
            }
            return accounts;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds an account to the accounts
     * @param a the account
     * @return true if all went ok, else false
     */
    public boolean addAccount(Account a) {
        try {
            CallableStatement cs;
            con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);

            cs = this.con.prepareCall("{call dbo.Schnell_AddAccount(?, ?, ?, ?, ? ,?)}");
            cs.setInt(1, a.getUserId());
            cs.setString(2, a.getName());
            cs.setString(3, a.getURL());
            cs.setString(4, a.getFavicon());
            cs.setString(5, a.getUsernameEmail());
            cs.setString(6, AES256.encrypt(a.getPassword()));

            cs.executeUpdate();
            cs.close();
            return true;
        }catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            return false;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * deletes an account from the accounts
     * @param profileId the userId
     * @return true, if all went ok, else false
     * @throws SQLException sql exception
     */
    public boolean deleteAccount(int profileId) throws SQLException {
        CallableStatement cs;
        con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
        cs = this.con.prepareCall("{call dbo.Schnell_DeleteAccount(?)}");
        cs.setInt(1, profileId);
        cs.executeUpdate();
        cs.close();
        return false;
    }

    /**
     * deletes profile
     * @param profileId given id to delete
     */
    public void deleteProfile(int profileId) {
        try {
            CallableStatement cs;
            con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
            cs = this.con.prepareCall("{call dbo.Schnell_DeleteProfile(?)}");
            cs.setInt(1, profileId);
            cs.executeUpdate();
            cs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds profile if all data were okay and responses with object[boolean, msg]
     * @param username the username
     * @param password the password
     * @param passwordRepeat the repeated password
     * @return Object[] with [0]=true if all is okay, else [0]=false and [1] with error message
     */
    public Object[] addProfile(String username, String password, String passwordRepeat) {
        Object[] o = new Object[2];
        o[0] = false;
        try {
            CallableStatement cs;
            con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);

            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            if(username.equals("")) {
                o[1] = "Please enter an username";
                return o;
            }
            if(password.equals("") || password.length() < 8) {
                o[1] = "Please choose a longer password";
                return o;
            }
            if(password.equals(passwordRepeat)) {
                cs = this.con.prepareCall("{call dbo.Schnell_AddProfile(?, ?)}");
                cs.setString(1, username.toLowerCase());
                cs.setString(2, hashed);

                cs.executeUpdate();
                cs.close();
                o[0] = true;
                return o;
            } else {
                o[1] = "Passwords don't match";
                return o;
            }
        } catch (Exception ignored) {
            o[1] = "Username already registered";
        }
        return o;
    }
}
