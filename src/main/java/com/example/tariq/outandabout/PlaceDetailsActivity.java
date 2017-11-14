package com.example.tariq.outandabout;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.tariq.outandabout.Model.GetFavourites;
import com.example.tariq.outandabout.Model.PlaceDetailsJSONParser;
import com.example.tariq.outandabout.Model.Preferences;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PlaceDetailsActivity extends AppCompatActivity {
    WebView wvPlaceDetails;
    private double markerLat;
    private double markerLong;
    private String name;
    private double destlng;
    private double destlat;
    Button btnFav;
    Button btnShare;
    private ArrayList<GetFavourites> favourites;
    GetFavourites prod;
    Preferences preferences;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        // Getting reference to WebView ( wv_place_details ) of the layout
        // activity_place_details
        wvPlaceDetails = (WebView) findViewById(R.id.placeDetailsWebView);
        btnFav = (Button) findViewById(R.id.btnsetFav);
        btnShare = (Button) findViewById(R.id.btnShare);
        wvPlaceDetails.getSettings().setUseWideViewPort(false);

        // Getting place reference from the map
        final String reference = getIntent().getStringExtra("reference");
        final String name = getIntent().getStringExtra("name");
        final String vicinity = getIntent().getStringExtra("vicinity");
        markerLat = getIntent().getDoubleExtra("markerLat", 0.0);
        markerLong = getIntent().getDoubleExtra("MarkerLong", 0.0);
        StringBuilder sb = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("reference=" + reference);
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyDS5pNmEN_3KAqDocbtDLzdNPC9_fAHBrg");
        Log.d("sb", "sb" + reference);

        preferences = new Preferences();
        favourites = preferences.getFavorites(this);

        try {
            prod = new GetFavourites(favourites.size(), reference, name, markerLat,
                    markerLong, destlat, destlng);
        } catch (Exception e) {
            prod = new GetFavourites(1, reference, name, markerLat, markerLong,
                    destlat, destlng);
        }

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.addFavourite(PlaceDetailsActivity.this, prod);
                Toast.makeText(getApplicationContext(), "Added to favourites!",
                        Toast.LENGTH_SHORT).show();

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check this place out!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, vicinity);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, name);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

            }
        });


        // Creating a new non-ui thread task to download Google place details
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());

    };

    /** A method to download json details from url */
    private String downloadUrl(String strUrl) throws IOException {
        String referer ="";
        StringBuilder jsonResults = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            conn = (HttpURLConnection) url.openConnection();
            if (referer != null) {
                conn.setRequestProperty("Referer", referer);
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            // Displays the list of places found in the terminal.
            Log.i("Data", "Places Found: " + jsonResults);
        } catch (MalformedURLException e) {
            Log.i("Google Places Utility", "Error processing Places API URL");
            return null;
        } catch (IOException e) {
            Log.i("Google Places Utility", "Error connecting to Places API");
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults.toString();
    }

    /** A class, to download Google Place Details */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String details = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                details = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return details;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google place details in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Place Details in JSON format */
    private class ParserTask extends
            AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> currentPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Start parsing Google place details in JSON format
                currentPlaceDetails = placeDetailsJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return currentPlaceDetails;
        }

        // Executed after the complete execution of doInBackground() method4
        @Override
        protected void onPostExecute(HashMap<String, String> currentPlaceDetails) {

            name = currentPlaceDetails.get("name");
            String icon = currentPlaceDetails.get("icon");
            String vicinity = currentPlaceDetails.get("vicinity");
            String lat = currentPlaceDetails.get("lat");
            String lng = currentPlaceDetails.get("lng");
            String formatted_address = currentPlaceDetails.get("formatted_address");
            String formatted_phone = currentPlaceDetails.get("formatted_phone");
            String website = currentPlaceDetails.get("website");
            String rating = currentPlaceDetails.get("rating");

            String mimeType = "text/html";
            String encoding = "utf-8";

            String details = "<html>" + "<body><img style='float:left' src="+ icon + " />"
                    + "<h2><center>" + name + "</center></h2>"
                    + "<br style='clear:both' />"
                    + "<hr/>"
                    + "<p style ='align:center'>Vicinity : " + vicinity + "</p>"
                    + "<p>Location : "+ lat + "," + lng + "</p>"
                    + "<p>Address : "+ formatted_address + "</p>"
                    + "<p>Phone : "+ formatted_phone + "</p>"
                    + "<p>Website : " + website+ "</p>"
                    + "<p>Rating : " + rating + "</p>"
                    + "</p>" + "</body></html>";

            // Setting the details in WebView
            wvPlaceDetails.loadDataWithBaseURL("", details, mimeType, encoding, "");
        }
    }
}