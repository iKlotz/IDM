import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Utility {

    public static long getFileSize(String url){
        long contentLength = 0;

        try {
            URL urlLink = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlLink.openConnection();
            contentLength = conn.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        };

        return contentLength;
    }

    public static void printLinks(ArrayList<String> links){
        for (String link : links){
            System.err.println(link);
        }
    }
}
