package com.example.tariq.outandabout.Model;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tariq.outandabout.R;

public class ProductListAdapter extends ArrayAdapter<GetFavourites> {

    private Context context;
    List<GetFavourites> getFavourites;
    Preferences preferences;

    public ProductListAdapter(Context context, List<GetFavourites> getFavourites) {
        super(context, R.layout.product_list_item, getFavourites);
        this.context = context;
        this.getFavourites = getFavourites;
        preferences = new Preferences();
    }

    private class ViewHolder {
        TextView faveName;
        ImageView favoriteImg;
        ImageView share;
    }

    @Override
    public int getCount() {
        return getFavourites.size();
    }

    @Override
    public GetFavourites getItem(int position) {
        return getFavourites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.product_list_item, null);
            holder = new ViewHolder();
            holder.faveName = (TextView) convertView.findViewById(R.id.txtFaveName);
            holder.favoriteImg = (ImageView) convertView.findViewById(R.id.imgViewFave);
            holder.share = (ImageView) convertView.findViewById(R.id.share);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GetFavourites getFavourites = (GetFavourites) getItem(position);
        holder.share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check this place out!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getFavourites.getTitle());
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        holder.faveName.setText(getFavourites.getTitle());

		/*
		 * If a favourite exists in shared preferences then set heart_checked drawable
		 * and set a tag
		 */
        if (checkFavoriteItem(getFavourites)) {
            holder.favoriteImg.setImageResource(R.drawable.heart_checked);
            holder.favoriteImg.setTag("checked");
        } else {
            holder.favoriteImg.setImageResource(R.drawable.heart_unchecked);
            holder.favoriteImg.setTag("unchecked");
        }

        return convertView;
    }

    /* Checks whether a particular product exists in SharedPreferences */
    public boolean checkFavoriteItem(GetFavourites checkGetFavourites) {
        boolean check = false;
        List<GetFavourites> favorites = preferences.getFavorites(context);
        if (favorites != null) {
            for (GetFavourites getFavourites : favorites) {
                if (getFavourites.equals(checkGetFavourites)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public void add(GetFavourites getFavourites) {
        super.add(getFavourites);
        this.getFavourites.add(getFavourites);
        notifyDataSetChanged();
    }

    @Override
    public void remove(GetFavourites getFavourites) {
        super.remove(getFavourites);
        this.getFavourites.remove(getFavourites);
        notifyDataSetChanged();
    }
}