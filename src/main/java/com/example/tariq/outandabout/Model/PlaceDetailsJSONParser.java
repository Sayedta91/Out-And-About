package com.example.tariq.outandabout.Model;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsJSONParser {

    /** Receives a JSONObject and returns a list */
    public HashMap<String,String> parse(JSONObject jObject){

        JSONObject JSONPlaceDetails = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            JSONPlaceDetails = jObject.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaceDetails(JSONPlaceDetails);
    }


    /** Parsing the Place Details Object object */
    private HashMap<String, String> getPlaceDetails(JSONObject JSONPlaceDetails){


        HashMap<String, String> currentPlaceDetails = new HashMap<String, String>();

        String name = "-NA-";
        String icon = "-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";
        String formatted_address="-NA-";
        String formatted_phone="-NA-";
        String website="-NA-";
        String rating="-NA-";


        try {
            // Extracting Place name, if available
            if(!JSONPlaceDetails.isNull("name")){
                name = JSONPlaceDetails.getString("name");
            }

            // Extracting Icon, if available
            if(!JSONPlaceDetails.isNull("icon")){
                icon = JSONPlaceDetails.getString("icon");
            }

            // Extracting Place Vicinity, if available
            if(!JSONPlaceDetails.isNull("vicinity")){
                vicinity = JSONPlaceDetails.getString("vicinity");
            }

            // Extracting Place formatted_address, if available
            if(!JSONPlaceDetails.isNull("formatted_address")){
                formatted_address = JSONPlaceDetails.getString("formatted_address");
            }

            // Extracting Place formatted_phone, if available
            if(!JSONPlaceDetails.isNull("formatted_phone_number")){
                formatted_phone = JSONPlaceDetails.getString("formatted_phone_number");
            }

            // Extracting website, if available
            if(!JSONPlaceDetails.isNull("website")){
                website = JSONPlaceDetails.getString("website");
            }

            // Extracting rating, if available
            if(!JSONPlaceDetails.isNull("rating")){
                rating = JSONPlaceDetails.getString("rating");
            }


            latitude = JSONPlaceDetails.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = JSONPlaceDetails.getJSONObject("geometry").getJSONObject("location").getString("lng");


            currentPlaceDetails.put("name", name);
            currentPlaceDetails.put("icon", icon);
            currentPlaceDetails.put("vicinity", vicinity);
            currentPlaceDetails.put("lat", latitude);
            currentPlaceDetails.put("lng", longitude);
            currentPlaceDetails.put("formatted_address", formatted_address);
            currentPlaceDetails.put("formatted_phone", formatted_phone);
            currentPlaceDetails.put("website", website);
            currentPlaceDetails.put("rating", rating);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currentPlaceDetails;
    }
}

