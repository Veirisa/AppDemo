package com.angrymuscrat.ya.geoloc;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.angrymuscrat.ya.geoloc.model.GameGenInterface;
import com.angrymuscrat.ya.geoloc.model.GameMode;
import com.google.android.gms.maps.model.LatLng;

import java.security.Security;
import java.util.Date;
import java.util.Random;

/**
 * Created by pohvalister on 19.12.16.
 */

public class LocalModeActivity extends Activity {
    EditText myEditText;
    Button myButton;

    double lat;
    double lng;
    TextView locationDATA;
    TextView enabledText;
    TextView statusText;
    Button settingsRef;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        public void onProviderEnabled(String provider) {
            checkEnabled();
            try {
                showLocation(locationManager.getLastKnownLocation(provider));
            }catch (SecurityException e){
                Toast.makeText(LocalModeActivity.this," нет разрешения на обработку данных",Toast.LENGTH_LONG).show();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            statusText.setText("Status: "+String.valueOf(status));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        GameMode.generator=new LocalGenerator();
        ///////почти не трогал
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_mode);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        myButton = (Button) findViewById(R.id.localeditbutton);
        myEditText = (EditText) findViewById(R.id.localedittext);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!showLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))){
                        Toast.makeText(LocalModeActivity.this, " нет данных о месторасположении",Toast.LENGTH_LONG);
                        return;
                    }
                    GameMode.amounthOfRounds = (new Integer(myEditText.getText().toString())).intValue();
                    if (GameMode.amounthOfRounds < 1 || GameMode.amounthOfRounds > 10)
                        throw new Exception();
                    Intent intent = new Intent (LocalModeActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
                catch (SecurityException e){
                    Toast.makeText(LocalModeActivity.this," нет разрешения на обработку данных",Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    Toast errorMes = Toast.makeText(LocalModeActivity.this, "Введите корректное значение!", Toast.LENGTH_LONG);
                    errorMes.show();
                }
            }
        });
        ///////дальше мое
        locationDATA=(TextView)findViewById(R.id.localedLocation);
        enabledText=(TextView)findViewById(R.id.localedEnabled);
        statusText=(TextView)findViewById(R.id.localedStatus);
        settingsRef=(Button)findViewById(R.id.localedToSettings);
        settingsRef.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onClickLocationSettings(view);
            }
        });
    }
    protected void onResume(){
        super.onResume();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 10, locationListener);
        }catch (SecurityException e){
            Toast.makeText(LocalModeActivity.this," нет разрешения на обработку данных",Toast.LENGTH_LONG).show();
        }
        checkEnabled();
    }
    protected void onPause(){
        super.onPause();
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e){
            Toast.makeText(LocalModeActivity.this," нет разрешения на обработку данных",Toast.LENGTH_LONG).show();
        }
    }
    private boolean showLocation(Location location){
        if (location==null)
            return false;
        locationDATA.setText(String.format("Coordinates: lat=%1$.4f, lon = %2$.4f, time= %3$tF %3$tT",
                location.getLatitude(),location.getLongitude(), new Date(location.getTime())));
        lat=location.getLatitude();
        lng=location.getLongitude();
        return true;
    }
    private void checkEnabled(){
        enabledText.setText("Enabled: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    //вызов меню настроек чтобы поменять location
    public void onClickLocationSettings(View view){
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private class LocalGenerator implements GameGenInterface {
        @Override
        public LatLng genPosition() {
            return new LatLng(lat, lng);
        }
        public Integer genRadius(){
            return 20_000;
        }
    }
}
