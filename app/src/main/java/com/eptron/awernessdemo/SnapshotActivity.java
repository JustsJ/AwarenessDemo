package com.eptron.awernessdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.BeaconStateResult;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Justs on 2016.08.29..
 */
public class SnapshotActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_LOCATION = 1;

    private TextView mDetectedActivityText, mHeadphoneStateText,
                     mLocationText,mNearbyPlacesText,mNearbyPlacesCountText,
                     mWeatherText;
    private GoogleApiClient mClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_snapshot);

        mDetectedActivityText = (TextView) findViewById(R.id.detected_activity_value);
        mHeadphoneStateText = (TextView) findViewById(R.id.headphone_state);
        mLocationText= (TextView) findViewById(R.id.location);
        mNearbyPlacesText= (TextView) findViewById(R.id.nearby_places);
        mNearbyPlacesCountText= (TextView) findViewById(R.id.nearby_places_count);
        mWeatherText= (TextView) findViewById(R.id.weather);

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mClient.connect();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateDetectedActivity();
                updateHeadphoneState();
                updateLocation();
                updateNearbyPlaces();
                updateWeather();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable, 5000);
    }

    public void updateDetectedActivity() {

        Awareness.SnapshotApi.getDetectedActivity(mClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {

                            mDetectedActivityText.setText("Can't get current activity.");

                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();
                        mDetectedActivityText.setText(probableActivity.toString());
                    }
                });
    }

    public void updateHeadphoneState() {
        Awareness.SnapshotApi.getHeadphoneState(mClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        if (!headphoneStateResult.getStatus().isSuccess()) {
                            mHeadphoneStateText.setText("Can't get headphone state.");
                            return;
                        }
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();
                        if (headphoneState.getState() == HeadphoneState.PLUGGED_IN) {
                            mHeadphoneStateText.setText("Headphones are plugged in.");
                        } else {
                            mHeadphoneStateText.setText("Headphones are not plugged in.");
                        }
                    }
                });
    }

    public void updateLocation() {

        //check permissions first
        if (ContextCompat.checkSelfPermission(
               SnapshotActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }

        Awareness.SnapshotApi.getLocation(mClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            mLocationText.setText("Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        mLocationText.setText("Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    }
                });
    }

    public void updateNearbyPlaces(){
        if (ContextCompat.checkSelfPermission(
                SnapshotActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }

        Awareness.SnapshotApi.getPlaces(mClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull PlacesResult placesResult) {
                        if (!placesResult.getStatus().isSuccess()) {
                            mNearbyPlacesText.setText("Could not get places.");
                            return;
                        }
                        List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();

                        if (placeLikelihoodList!=null && !placeLikelihoodList.isEmpty()) {
                            mNearbyPlacesCountText.setText(Integer.toString(placeLikelihoodList.size()));
                            mNearbyPlacesText.setText(placeLikelihoodList.get(0).getPlace().getName().toString() );
                            Log.i("SnapshotAPI","current place: "+placeLikelihoodList.get(0).toString());
                        }
                        else{
                            mNearbyPlacesCountText.setText(0);
                            mNearbyPlacesText.setText("Nothing :(");
                        }
                    }
                });
    }

    public void updateWeather(){
        if (ContextCompat.checkSelfPermission(
                SnapshotActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }

        Awareness.SnapshotApi.getWeather(mClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            mWeatherText.setText("Could not get weather.");
                            return;
                        }
                        Weather weather = weatherResult.getWeather();
                        int[] conditions=weather.getConditions();
                        String conditionsString="";



                        for(int i=0;i<conditions.length;i++)
                        {
                            if (i>0){
                                conditionsString+=", ";
                            }

                            switch (conditions[i]){
                                case 1:
                                    conditionsString+="Clear";
                                    break;
                                case 2:
                                    conditionsString+="Cloudy";
                                    break;
                                case 3:
                                    conditionsString+="Foggy";
                                    break;
                                case 4:
                                    conditionsString+="Hazy";
                                    break;
                                case 5:
                                    conditionsString+="Icy";
                                    break;
                                case 6:
                                    conditionsString+="Rainy";
                                    break;
                                case 7:
                                    conditionsString+="Snowy";
                                    break;
                                case 8:
                                    conditionsString+="Stormy";
                                    break;
                                case 9:
                                    conditionsString+="Windy";
                                    break;
                                default:
                                    conditionsString+="Unknown";
                            }
                        }
                        mWeatherText.setText(conditionsString);
                    }
                });
    }
}
