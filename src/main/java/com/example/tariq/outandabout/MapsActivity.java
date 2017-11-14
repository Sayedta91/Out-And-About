package com.example.tariq.outandabout;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tariq.outandabout.Model.MyItem;
import com.example.tariq.outandabout.Model.PlaceJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;


public class MapsActivity extends FragmentActivity implements LocationListener,ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {

    GoogleMap mMap;
    double myLatitude = 0;
    double myLongitude = 0;

    HashMap<String, String> mMarker = new HashMap<String, String>();
    PlaceJSONParser placeJsonParser = new PlaceJSONParser();

    private ClusterManager<MyItem> mClusterManager;
    protected MyItem clickedClusterItem;

    String[] placeType;
    String[] placeTypeName;
    Spinner spinPlaceType;
    String type;

    Location item;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mMap = mapFragment.getMap();
        onMapReady();

        Toast.makeText(getApplicationContext(), "Long press to search in a specific location.",
                Toast.LENGTH_LONG).show();

        // Array of place types
        placeType = getResources().getStringArray(R.array.placeType);
        // Array of place type names
        placeTypeName = getResources().getStringArray(R.array.placeTypeName);
        // Creating an array adapter with an array of Place types
        // to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,  R.layout.spinner_item, R.id.textview, placeTypeName);
        // Getting reference to the Spinner
        spinPlaceType = (Spinner) findViewById(R.id.spinPlaceType);
        // Setting adapter on Spinner to set place types
        spinPlaceType.setAdapter(adapter);
        spinPlaceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int selectedPosition = spinPlaceType.getSelectedItemPosition();
                type = placeType[selectedPosition];

                StringBuilder sb = new StringBuilder(
                        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                sb.append("location=" + myLatitude + "," + myLongitude);
                sb.append("&type=" + type);
                sb.append("&radius=4000");
                sb.append("&key=AIzaSyDS5pNmEN_3KAqDocbtDLzdNPC9_fAHBrg");
                // Creating a new non-ui thread task to download Google place json
                // data
                PlacesTask placesTask = new PlacesTask();

                // Invokes the "doInBackground()" method of the class PlaceTask
                placesTask.execute(sb.toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FloatingActionButton fab_types = (FloatingActionButton) findViewById(R.id.fab_types);
        fab_types.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinPlaceType.performClick();
                spinPlaceType.setPrompt("What are you looking for?");
            }
        });

        // Will display next 20 places returned form the next_page_token
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_more);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Finding you some more places.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                StringBuilder sb = new StringBuilder(
                        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                sb.append("pagetoken=" + placeJsonParser.getNext_Page_token());
                sb.append("&key=AIzaSyDS5pNmEN_3KAqDocbtDLzdNPC9_fAHBrg");
                // Creating a new non-ui thread task to download Google place json
                // data

                if (placeJsonParser.getNext_Page_token() == null || placeJsonParser.getNext_Page_token() == "") {
                    Snackbar.make(view, "No more places left to find.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

                PlacesTask placesTask = new PlacesTask();

                // Invokes the "doInBackground()" method of the class PlaceTask
                placesTask.execute(sb.toString());
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                double dbLat = point.latitude;
                double dbLong = point.longitude;
                // Position the map to the center of the circles coordinates.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dbLat, dbLong), 13));
                drawCircle(point);

                StringBuilder stringBuilder = new StringBuilder(
                        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + dbLat + "," + dbLong);
                stringBuilder.append("&type=" + type);
                stringBuilder.append("&radius=1500");
                stringBuilder.append("&key=AIzaSyDS5pNmEN_3KAqDocbtDLzdNPC9_fAHBrg");
                // Creating a new non-ui thread task to download Google place json
                // data
                PlacesTask placesTask = new PlacesTask();

                // Invokes the "doInBackground()" method of the class PlaceTask
                placesTask.execute(stringBuilder.toString());

            }
        });

    }

    // Draws a circle based on where the users finger is.
    private void drawCircle(LatLng point){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(2000);
        circleOptions.strokeColor(Color.parseColor("#006699"));
        circleOptions.fillColor(Color.TRANSPARENT);
        circleOptions.strokeWidth(5);
        mMap.addCircle(circleOptions);
    }

    public void onMapReady(){
        // Enabling MyLocation in Google Map
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Getting LocationManager object from System Service
        // LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location From GPS
        Location location = locationManager.getLastKnownLocation(provider);

        // onLocationChanged(location);
        if (location != null) {
            onLocationChanged(location);
        }
    }

    /**
     * A method to download json data from url
     */
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


    /**
     * A class, to download Google Places
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            List<HashMap<String, String>> places = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            // Clears all the existing markers
            mMap.clear();
            setUpClusterer(list);

        }
    }

    private void setUpClusterer(List<HashMap<String, String>> list) {

        // Position the map.
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLatitude,myLongitude), 13));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        mClusterManager
                .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        clickedClusterItem = item;
                        return false;
                    }
                });

        // Add cluster items (markers) to the cluster manager.
        addItems(list);

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                new MyCustomAdapterForItems());
    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyCustomAdapterForItems() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.info_window, null);
        }
        @Override
        public View getInfoWindow(Marker marker) {

            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.txtTitle));
            TextView tvSnippet = ((TextView) myContentsView
                    .findViewById(R.id.txtSnippet));

            tvTitle.setText(clickedClusterItem.getTitle());
            tvSnippet.setText(clickedClusterItem.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private void addItems(List<HashMap<String, String>> list) {
        double latitude;
        double longitude;

        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> hmPlace = list.get(i);

            // Getting latitude of the place
            latitude = Double.parseDouble(hmPlace.get("lat"));

            // Getting longitude of the place
            longitude = Double.parseDouble(hmPlace.get("lng"));

            String name = hmPlace.get("place_name");

            // Getting vicinity
            String vicinity = hmPlace.get("vicinity");
            MyItem offsetItem = new MyItem(latitude, longitude, hmPlace.get("reference"), name, vicinity);
            mClusterManager.addItem(offsetItem);

        }
    }

    public void onClusterItemInfoWindowClick(MyItem item) {
        Intent placesIntent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
        String reference = item.getReference();

        placesIntent.putExtra("name", item.getTitle());
        placesIntent.putExtra("vicinity", item.getSnippet());
        placesIntent.putExtra("reference", reference);
        placesIntent.putExtra("sourcelat", myLatitude);
        placesIntent.putExtra("sourcelng", myLongitude);
        startActivity(placesIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        LatLng myLocation = new LatLng(myLatitude, myLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}