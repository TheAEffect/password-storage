import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.URL;

/**
 * Account Class
 */
public class Account {
    int userId;
    int accId = 0;
    String name;
    String URL;
    String usernameEmail;
    String password;
    String favicon;

    /**
     * Account Constructor for creating
     * @param userId the userid of the owner
     * @param url the url of the given website
     * @param name name of the account
     * @param usernameEmail username of the account
     * @param password password of the account
     * @throws IOException
     */
    Account(int userId, String url, String name, String usernameEmail, String password) throws IOException {
        this.name = name;
        this.URL = url;
        this.userId = userId;
        this.usernameEmail = usernameEmail;
        this.password = password;
        this.favicon = setFavicon();
    }

    /**
     * Account Constructor for RE-creating from db
     * @param id the id of the account itself
     * @param userId the userid of the owner
     * @param url the url of the given website
     * @param name name of the account
     * @param usernameEmail username of the account
     * @param password password of the account
     * @throws IOException
     */
    Account(int id, String favicon, int userId, String url, String name, String usernameEmail, String password) {
        this.accId = id;
        this.name = name;
        this.URL = url;
        this.userId = userId;
        this.usernameEmail = usernameEmail;
        this.password = password;
        this.favicon = favicon;
    }

    /**
     * Returns the username/email
     * @return username
     */
    public String getUsernameEmail() {
        return this.usernameEmail;
    }

    /**
     * Returns the password
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the name
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the user id
     * @return userId
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Returns the URL
     * @return URL
     */
    public String getURL() {
        return this.URL;
    }

    /**
     * Returns the Account id
     * @return accId
     */
    public int getId() {
        return this.accId;
    }

    /**
     * Converts the website URL conform and returns it
     * @return the website url
     */
    public String getWebsite() {
        if(this.URL == null || this.URL.equals("")) {
            return "";
        } else if(this.URL.startsWith("http://") || this.URL.startsWith("https://")) {
            if(URL.contains("www.")) {
                return "https://www."+this.URL.replace("https://","").replace("http://","").replace("www.","");
            } else {
                return "https://www."+this.URL.replace("https://","").replace("http://","");
            }
        } else {
            if(URL.contains("www.")) {
                return "https://"+this.URL.replace("www.","");
            }
            return "https://www."+this.URL.replace("www.","");
        }
    }

    /**
     * Returns the favicon
     * @return favicon
     */
    public String getFavicon() {
        return this.favicon;
    }

    /**
     * Downloads the ico image from the given website, converts it to base64 and returns it
     * @return base64 string
     */
    public String setFavicon() {
        if(this.URL == null || this.URL.equals("")) {
            return null;
        } else {
            try {
                java.net.URL faviconURL;
                if (this.URL.startsWith("http://") || this.URL.startsWith("https://")) {
                    faviconURL = new URL(this.URL);
                } else {
                    faviconURL = new URL("http://" + this.URL);
                }
                String hostname = faviconURL.getHost();
                if (hostname != null) {
                    hostname = hostname.startsWith("www.") ? hostname.substring(4) : hostname;
                }

                URL url = new URL("https://www.google.com/s2/favicons?sz=64&domain_url=" + hostname);

                InputStream is = url.openStream();
                byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
                return new String(Base64.encodeBase64(bytes));
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}