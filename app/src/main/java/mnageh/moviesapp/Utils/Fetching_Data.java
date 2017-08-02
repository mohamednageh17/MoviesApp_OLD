package mnageh.moviesapp.Utils;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fetching_Data extends AsyncTask<Void,Void,String> {
    private String Url_Key;
    private String id;
    public static String Basic_Url = "http://api.themoviedb.org/3/tv";
    public static String PopMovies_Url = "/popular?api_key=d6464c49a9503b302888a4a139f1ed70";
    public static String TopMovies_Url = "/top_rated?api_key=d6464c49a9503b302888a4a139f1ed70";
    public static String TrailersMovies_Url = "/videos?api_key=d6464c49a9503b302888a4a139f1ed70";
    public static String ReviewsMovies_Url = "/reviews?api_key=d6464c49a9503b302888a4a139f1ed70";
    public static String latest_Url = "/latest?api_key=d6464c49a9503b302888a4a139f1ed70";
    public static String airing_today = "/airing_today?api_key=d6464c49a9503b302888a4a139f1ed70";
    public static String on_the_air = "/on_the_air?api_key=d6464c49a9503b302888a4a139f1ed70";
    public static String searchURL = "http://api.themoviedb.org/3/search/tv?api_key=d6464c49a9503b302888a4a139f1ed70&query=";


    public Fetching_Data() {
    }

    public Fetching_Data(String Key, String id) {
        Url_Key = Key;
        this.id=id;
    }

    network_Response networkResponse;

    public void setNetworkResponse(network_Response networkResponse) {
        this.networkResponse = networkResponse;
    }

    public String Fetching_Data(String UrlKey) {
        HttpURLConnection urlConnect = null;
        BufferedReader reader = null;
        String JsonData = null;
        try {
            String UrlWithKey = UrlKey;
            URL url = new URL(UrlWithKey);
            urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.setRequestMethod("GET");
            urlConnect.connect();
            InputStream inputStream = urlConnect.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            JsonData = buffer.toString();
            Log.d("JSON", JsonData);
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return null;
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return JsonData;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String JsonData = "";
        if(Url_Key.equals("Popular Movies"))
              JsonData = Fetching_Data(Basic_Url+PopMovies_Url);
        else if(Url_Key.equals("Top Rated Movies"))
              JsonData = Fetching_Data( Basic_Url+TopMovies_Url);
        else if(Url_Key.equals("latest_Url"))
            JsonData = Fetching_Data( Basic_Url+latest_Url);
        else if(Url_Key.equals("airing_today"))
            JsonData = Fetching_Data( Basic_Url+airing_today);
        else if(Url_Key.equals("on_the_air"))
            JsonData = Fetching_Data( Basic_Url+on_the_air);
        else if(Url_Key.equals("search"))
            JsonData = Fetching_Data( searchURL+id);
        else if(Url_Key.equals("review_Model"))
            JsonData = Fetching_Data( Basic_Url + "/" + id + ReviewsMovies_Url);
        else if(Url_Key.equals("trailer_Model"))
            JsonData = Fetching_Data(  Basic_Url + "/" + id + TrailersMovies_Url);
        return JsonData;
    }

    @Override
    protected void onPostExecute(String JsonData) {
            networkResponse.OnSuccess(JsonData);
    }
}