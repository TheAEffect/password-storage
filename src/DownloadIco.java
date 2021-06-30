import java.awt.Container;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.image4j.codec.ico.ICODecoder;

public class DownloadIco {

    public static List<BufferedImage> readImage() {

        List<BufferedImage> images = null;

        try {
            String path = "https://www.google.com/s2/favicons?sz=64&domain_url=facebook.de";
            InputStream istr = new URL(path).openStream();
            images = ICODecoder.read(istr);

        } catch (IOException ex) {
            Logger.getLogger(DownloadIco.class.getName()).log(Level.SEVERE, null, ex);
        }

        return images;
    }
}