package com.example.tariq.outandabout.Model;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tariq.outandabout.PlaceDetailsActivity;
import com.example.tariq.outandabout.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


public class FavouriteMap extends FragmentActivity implements LocationListener, ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {
    GoogleMap mMap;

    double mLatitude = 0;
    double mLongitude = 0;
    private Intent intent;
    private ClusterManager<MyItem> mClusterManager;
    protected MyItem clickedClusterItem;
    Preferences preferences;
    List<GetFavourites> favorites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting reference to the SupportMapFragment
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Getting Google Map
        mMap = fragment.getMap();

        // Enabling MyLocation in Google Map
        mMap.setMyLocationEnabled(true);

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

        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        intent = getIntent();

        preferences = new Preferences();
        favorites = preferences.getFavorites(this);

        setUpClusterer(favorites);


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
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

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
        }
    }

    private void setUpClusterer(List<GetFavourites> list) {

        // Getting a place from the places list
        //	HashMap<String, String> hmPlace = list.get(0);

        // Getting latitude of the place
        double lat = list.get(0).getdeslat();

        // Getting longitude of the place
        double lng = list.get(0).getdeslng();

        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
                lng), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this); // added

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
            myContentsView = getLayoutInflater().inflate(R.layout.info_window,
                    null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.txtTitle));
            TextView tvSnippet = ((TextView) myContentsView
                    .findViewById(R.id.txtSnippet));

            tvTitle.setText(clickedClusterItem.getTitle());
            tvSnippet.setVisibility(View.GONE);
            // Text(clickedClusterItem.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private void addItems(List<GetFavourites> list) {

        // Set some lat/lng coordinates to start with.
        double lat;
        double lng;

        for (int i = 0; i < list.size(); i++) {
            MyItem offsetItem = new MyItem(list.get(i).getdeslat(), list.get(i).getdeslng(), list.get(i).getName(),
                    list.get(i).getTitle(), list.get(i).getTitle());
            mClusterManager.addItem(offsetItem);

        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
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

    @Override
    public void onClusterItemInfoWindowClick(MyItem item) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
        String reference = item.getReference();
        LatLng dest = item.getPosition();

        intent.putExtra("name", item.getTitle());
        intent.putExtra("reference", reference);
        intent.putExtra("sourcelat", mLatitude);
        intent.putExtra("sourcelng", mLongitude);
        intent.putExtra("destinationlat", dest.latitude);
        intent.putExtra("destinationlng", dest.longitude);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
