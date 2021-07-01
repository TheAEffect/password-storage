import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DB {
    String dbHost = "134.108.190.89";
    int dbPort = 1433;
    String dbName = "SWB_DB2_Projekt";
    String dbUser = "wkb4";
    String dbPass = "wkb4";

    String connectionUrl = "jdbc:sqlserver://"+dbHost+":"+dbPort+";databaseName="+dbName;

    Connection con = null;
    ResultSet rs = null;

    public Object[] login(String email, String password) throws SQLException {
        Object[] obj = new Object[2];
        obj[0] = false;
        CallableStatement cs;
        con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
        cs = this.con.prepareCall("{call dbo.Schnell_Login(?)}");
        cs.setString(1, email.toLowerCase());

        rs = cs.executeQuery();
        if(rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
            obj[0] = true;
            obj[1] = rs.getString("id");
        }
        cs.close();
        return obj;
    }

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

    public boolean deleteAccount(int profileId) throws SQLException {
        CallableStatement cs;
        con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
        cs = this.con.prepareCall("{call dbo.Schnell_DeleteAccount(?)}");
        cs.setInt(1, profileId);
        cs.executeUpdate();
        cs.close();
        return false;
    }

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

    public boolean addProfile(String email, String password, String passwordRepeat) {
        try {
            CallableStatement cs;
            con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);

            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            if(password.equals(passwordRepeat)) {
                cs = this.con.prepareCall("{call dbo.Schnell_AddProfile(?, ?)}");
                cs.setString(1, email.toLowerCase());
                cs.setString(2, hashed);

                cs.executeUpdate();
                cs.close();
                return true;
            } else {
                return false;
            }
        } catch (Exception Ex) {
            return false;
        }
    }
}
