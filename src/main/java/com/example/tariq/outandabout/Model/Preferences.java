package com.example.tariq.outandabout.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

// the following tutorial/link helped in the creation of this class as well as the FavouriteListFragment class.
// http://stackoverflow.com/questions/34838485/favoritelist-using-listview-gson-and-sharedpreferences

public class Preferences {

    public static final String FAVORITES = "Product_Favorite";

    public Preferences() {
        super();
    }

    // This four methods are used for maintaining favourites.
    public void saveFavourite(Context context, List<GetFavourites> favorites) {
        SharedPreferences faveSettings;
        Editor faveEditor;

        faveSettings = context.getSharedPreferences("PREFS_FAVES",
                Context.MODE_PRIVATE);
        faveEditor = faveSettings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        faveEditor.putString(FAVORITES, jsonFavorites);

        faveEditor.commit();
    }

    public void addFavourite(Context context, GetFavourites getFavourites) {
        List<GetFavourites> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<GetFavourites>();
        favorites.add(getFavourites);
        saveFavourite(context, favorites);
    }

    public void removeFavourite(Context context, GetFavourites getFavourites) {
        ArrayList<GetFavourites> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(getFavourites);
            saveFavourite(context, favorites);
        }
    }

    public ArrayList<GetFavourites> getFavorites(Context context) {
        SharedPreferences settings;
        List<GetFavourites> favorites;

        settings = context.getSharedPreferences("PREFS_FAVES",
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            GetFavourites[] favoriteItems = gson.fromJson(jsonFavorites,
                    GetFavourites[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<GetFavourites>(favorites);
        } else
            return null;

        return (ArrayList<GetFavourites>) favorites;
    }
}
