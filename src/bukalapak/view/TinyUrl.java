package bukalapak.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.util.Log;

public class TinyUrl {

    private static String T_URL = "http://tinyurl.com/api-create.php?url=";
    private final static String LOG_TAG = "TinyUrl";
   
    public static String getTinyUrl(String longUrl) {
           
            String tinyUrl = "";
            String urlString = T_URL + longUrl;
           
            try {
                    URL url = new URL(urlString);
               
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
           
            while ((str = in.readLine()) != null) {
              tinyUrl += str;
            }
            in.close();
            }
            catch (Exception e) {
                    Log.e(LOG_TAG, "Can not create an tinyurl link",e);
            }
            return tinyUrl;
    }
}
