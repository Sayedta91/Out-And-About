package com.example.tariq.outandabout.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    LatLng mPosition;
    private String reference,placeTitle,snippet;

    public MyItem(double lat, double lng,String ref,String title, String snip) {
        mPosition = new LatLng(lat, lng);
        reference= ref;
        placeTitle= title;
        snippet = snip;
    }

    @Override
    public LatLng getPosition() {
        // TODO Auto-generated method stub
        return mPosition;
    }
    public String getReference() {
        // TODO Auto-generated method stub
        return reference;
    }
    public String getTitle() {
        // TODO Auto-generated method stub
        return placeTitle;
    }

    public String getSnippet() {
        // TODO Auto-generated method stub
        return snippet;
    }
}