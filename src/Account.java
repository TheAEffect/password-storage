import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.URL;

public class Account {
    int userId;
    int accId = 0;
    String name;
    String URL;
    String usernameEmail;
    String password;
    String favicon;

    Account(int userId, String url, String name, String usernameEmail, String password) throws IOException {
        this.name = name;
        this.URL = url;
        this.userId = userId;
        this.usernameEmail = usernameEmail;
        this.password = password;
        this.favicon = setFavicon();
    }

    Account(int id, String favicon, int userId, String url, String name, String usernameEmail, String password) {
        this.accId = id;
        this.name = name;
        this.URL = url;
        this.userId = userId;
        this.usernameEmail = usernameEmail;
        this.password = password;
        this.favicon = favicon;
    }

    public String getUsernameEmail() {
        return this.usernameEmail;
    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getURL() {
        return this.URL;
    }

    public int getId() {
        return this.accId;
    }

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

    public String getFavicon() {
        return this.favicon;
    }

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