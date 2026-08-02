import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * DB class
 */
public class DB {
    private static final Logger logger = LoggerFactory.getLogger(DB.class);

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();
    private static final String DB_HOST = DOTENV.get("DB_HOST" );
    private static final String DB_NAME = DOTENV.get("DB_NAME");
    private static final String DB_USER = DOTENV.get("DB_USER");
    private static final String DB_PASS = DOTENV.get("DB_PASS");
    private static final int DB_PORT = Integer.parseInt(DOTENV.get("DB_PORT"));

    private final String connectionUrl = "jdbc:sqlserver://" + DB_HOST + ":" + DB_PORT + ";databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true";

    private boolean schemaChecked = false;

    private synchronized Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(connectionUrl, DB_USER, DB_PASS);
        if (!schemaChecked) {
            ensureSchema(connection);
            schemaChecked = true;
        }
        return connection;
    }

    private void ensureSchema(Connection connection) throws SQLException {
        String createUsers = "IF OBJECT_ID('dbo.users','U') IS NULL " +
                "CREATE TABLE dbo.users (" +
                "id INT IDENTITY(1,1) PRIMARY KEY NOT NULL, " +
                "username_email VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL)";

        String createAccounts = "IF OBJECT_ID('dbo.accounts','U') IS NULL " +
                "CREATE TABLE dbo.accounts (" +
                "id INT IDENTITY(1,1) PRIMARY KEY NOT NULL, " +
                "user_id INT NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "url VARCHAR(255) NULL, " +
                "favicon VARCHAR(MAX), " +
                "account VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL)";

        try (PreparedStatement usersStatement = connection.prepareStatement(createUsers)) {
            usersStatement.executeUpdate();
        }
        try (PreparedStatement accountsStatement = connection.prepareStatement(createAccounts)) {
            accountsStatement.executeUpdate();
        }
    }

    /**
     * Checks if user has entered correct login data.
     *
     * @param username the username
     * @param password the password
     * @return a typed result containing the success flag and the user id on success
     * @throws SQLException sql exception
     */
    protected Storage.LoginResult login(String username, String password) throws SQLException {
        if (username == null || username.isBlank()) {
            return new Storage.LoginResult(false, null);
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, password FROM dbo.users WHERE username_email = ?")) {
            statement.setString(1, username.toLowerCase());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && BCrypt.checkpw(password, resultSet.getString("password"))) {
                    return new Storage.LoginResult(true, resultSet.getString("id"));
                }
            }
        }
        return new Storage.LoginResult(false, null);
    }

    /**
     * Gets the accounts
     *
     * @param userId the userId
     * @return all accounts to given userId
     */
    protected List<Account> getPasswordList(int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, favicon, url, name, account, password FROM dbo.accounts WHERE user_id = ? ORDER BY name")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Account> accounts = new ArrayList<>();
                while (resultSet.next()) {
                    Account acc = new Account(resultSet.getInt("id"),
                            resultSet.getString("favicon"),
                            userId,
                            resultSet.getString("url"),
                            resultSet.getString("name"),
                            resultSet.getString("account"),
                            resultSet.getString("password"));
                    accounts.add(acc);
                }
                return accounts;
            }
        } catch (SQLException sqlEx) {
            logger.error("Fehler beim Datenbankzugriff: {}", sqlEx.getMessage(), sqlEx);
        }
        return null;
    }

    /**
     * Adds an account to the accounts
     *
     * @param a the account
     * @return true if all went ok, else false
     */
    protected boolean addAccount(Account a) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO dbo.accounts (user_id, name, url, favicon, account, password) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, a.getUserId());
            statement.setString(2, a.getName());
            statement.setString(3, a.getURL());
            statement.setString(4, a.getFavicon());
            statement.setString(5, a.getUsernameEmail());
            statement.setString(6, AES256.encrypt(a.getPassword()));

            statement.executeUpdate();
            return true;
        } catch (SQLException sqlEx) {
            logger.error("Fehler beim Datenbankzugriff: {}", sqlEx.getMessage(), sqlEx);
            return false;
        } catch (Exception ex) {
            logger.error("Fehler: {}", ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * deletes an account from the accounts
     *
     * @param profileId the userId
     * @return true, if all went ok, else false
     * @throws SQLException sql exception
     */
    protected boolean deleteAccount(int profileId) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM dbo.accounts WHERE id = ?")) {
            statement.setInt(1, profileId);
            statement.executeUpdate();
            return true;
        }
    }

    /**
     * deletes profile
     *
     * @param profileId given id to delete
     */
    protected void deleteProfile(int profileId) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement deleteAccounts = connection.prepareStatement("DELETE FROM dbo.accounts WHERE user_id = ?")) {
                deleteAccounts.setInt(1, profileId);
                deleteAccounts.executeUpdate();
            }
            try (PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM dbo.users WHERE id = ?")) {
                deleteUser.setInt(1, profileId);
                deleteUser.executeUpdate();
            }
        } catch (Exception ex) {
            logger.error("Fehler: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Adds profile if all data were okay and responses with object[boolean,
     * msg]
     *
     * @param username the username
     * @param password the password
     * @param passwordRepeat the repeated password
     * @return Object[] with [0]=true if all is okay, else [0]=false and [1]
     * with error message
     */
    protected Storage.RegistrationResult addProfile(String username, String password, String passwordRepeat) {
        if (username == null || username.isBlank()) {
            return new Storage.RegistrationResult(false, "Please enter an username");
        }

        if (password == null || password.isBlank() || password.length() < 8) {
            return new Storage.RegistrationResult(false, "Please choose a longer password");
        }

        if (!password.equals(passwordRepeat)) {
            return new Storage.RegistrationResult(false, "Passwords don't match");
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO dbo.users (username_email, password) VALUES (?, ?)")) {
            statement.setString(1, username.toLowerCase());
            statement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            statement.executeUpdate();
            return new Storage.RegistrationResult(true, null);
        } catch (SQLException e) {
            logger.error("Fehler beim Anlegen des Profils: {}", e.getMessage(), e);
            return new Storage.RegistrationResult(false, "Username already registered");
        }
    }
}
