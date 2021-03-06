package com.angrymuscrat.ya.geoloc;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Random;

import com.angrymuscrat.ya.geoloc.model.GameGenInterface;
import com.angrymuscrat.ya.geoloc.model.GameMode;
import com.angrymuscrat.ya.geoloc.model.RegionForRand;
import com.google.android.gms.maps.model.LatLng;

public class GeneralModeActivity extends Activity {
    EditText myEditText;
    Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GameMode.generator=new GeneralGenerator();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_mode);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myButton = (Button) findViewById(R.id.generaleditbutton);
        myEditText = (EditText) findViewById(R.id.generaledittext);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    GameMode.amounthOfRounds = (new Integer(myEditText.getText().toString())).intValue();
                    if (GameMode.amounthOfRounds < 1 || GameMode.amounthOfRounds > 10)
                        throw new Exception();
                    Intent intent = new Intent (GeneralModeActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
                catch (Exception e) {
                    Toast errorMes = Toast.makeText(GeneralModeActivity.this, "Enter the correct value!", Toast.LENGTH_LONG);
                    errorMes.show();
                }
            }
        });
    }
    private class GeneralGenerator implements GameGenInterface{
        @Override
        public LatLng getPosition() {
            Random rand = new Random(System.currentTimeMillis());
            int tmp = RegionForRand.regions.length;
            tmp = (rand.nextInt() % tmp + tmp) % tmp;
            double lat = ((rand.nextDouble() - 0.5) * 10 + RegionForRand.regions[tmp].latitude);
            double lng = ((rand.nextDouble() - 0.5) * 20 + RegionForRand.regions[tmp].longitude);
            return new LatLng(lat, lng);
        }
        public Integer getRadius(){
            return 12_000_000;
        }
    }
}
