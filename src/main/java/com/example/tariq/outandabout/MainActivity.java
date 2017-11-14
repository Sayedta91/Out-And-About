package com.example.tariq.outandabout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.tariq.outandabout.Model.FavouriteListFragment;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout btnNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNearby = (RelativeLayout) findViewById(R.id.btnNearby);
        btnNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(mapIntent);
            }
        });

    }

    // is called when the user clicks/presses on the Favourites button
    // opens the FavouriteListFragment
    public void openFragment(View view){
       getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new FavouriteListFragment()).addToBackStack(null).commit();
    }


}


