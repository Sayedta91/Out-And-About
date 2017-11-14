package com.example.tariq.outandabout.Model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tariq.outandabout.PlaceDetailsActivity;
import com.example.tariq.outandabout.R;

import java.util.List;

public class FavouriteListFragment extends Fragment {

    ListView favouriteList;
    Preferences preferences;
    List<GetFavourites> favourites;

    Activity activity;
    ProductListAdapter productListAdapter;

    private Button sumbit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container,
                false);
        // Get favorite items from SharedPreferences.
        preferences = new Preferences();
        favourites = preferences.getFavorites(activity);

        if (favourites == null) {
            Toast.makeText(getActivity().getApplicationContext(), "You dont have any favourites.",
                    Toast.LENGTH_SHORT).show();
        } else {

            if (favourites.size() == 0) {
                Toast.makeText(getActivity().getApplicationContext(), "You dont have any favourites.",
                        Toast.LENGTH_SHORT).show();
            }
//            sumbit = (Button) view.findViewById(R.id.sumbit);
//            sumbit.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    Intent intent = new Intent(getActivity(), FavouriteMap.class);
//
//                    startActivity(intent);
//
//                }
//            });
            favouriteList = (ListView) view.findViewById(R.id.list_product);
            if (favourites != null) {
                productListAdapter = new ProductListAdapter(activity, favourites);
                favouriteList.setAdapter(productListAdapter);

                favouriteList.setOnItemClickListener(new OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View arg1,
                                            int position, long arg3) {
                        Log.d("destinationlat", "destinationlat" + favourites.get(position).getdeslat());
                        Intent intent = new Intent(getActivity(), PlaceDetailsActivity.class);

                        intent.putExtra("name", favourites.get(position).getTitle());
                        intent.putExtra("reference", favourites.get(position).getName());
                        intent.putExtra("sourcelat", favourites.get(position).getsrclat());
                        intent.putExtra("sourcelng", favourites.get(position).getsrclng());
                        intent.putExtra("destinationlat", favourites.get(position).getdeslat());
                        intent.putExtra("destinationlng", favourites.get(position).getdeslng());
                        startActivity(intent);
                    }
                });

                favouriteList.setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent, View view,
                            int position, long id) {

                        ImageView btnVFave = (ImageView) view
                                .findViewById(R.id.imgViewFave);

                        String tag = btnVFave.getTag().toString();
                        if (tag.equalsIgnoreCase("unchecked")) {
                            preferences.addFavourite(activity, favourites.get(position));
                            Toast.makeText(activity, activity.getResources().getString(
                                    R.string.addFaves), Toast.LENGTH_SHORT).show();

                            btnVFave.setTag("red");
                            btnVFave.setImageResource(R.drawable.heart_checked);
                        } else {
                            preferences.removeFavourite(activity,
                                    favourites.get(position));
                            btnVFave.setTag("checked");
                            btnVFave.setImageResource(R.drawable.heart_unchecked);
                            productListAdapter.remove(favourites.get(position));
                            Toast.makeText(activity, activity.getResources().getString(
                                            R.string.removeFaves),
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }
        }
        return view;
    }


    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.getActivity().finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}

