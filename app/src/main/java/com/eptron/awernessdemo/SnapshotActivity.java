package com.eptron.awernessdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Justs on 2016.08.29..
 */
public class SnapshotActivity extends AppCompatActivity {

    private static final int  MY_PERMISSION_LOCATION=1;

    private TextView mDetectedActivityText, mNearestBeaconsText, mHeadphoneStateText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_snapshot);

        mDetectedActivityText=(TextView) findViewById(R.id.detected_activity_value);
        mNearestBeaconsText= (TextView) findViewById(R.id.nearest_beacons_value);
        mHeadphoneStateText= (TextView) findViewById(R.id.headphone_state);

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        client.connect();

        manageDetectedActivity(client);
        manageNearbyBeacons(client);
        manageHeadphoneState(client);
    }

    public void manageDetectedActivity(GoogleApiClient client){

        Awareness.SnapshotApi.getDetectedActivity(client)
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

    public void manageNearbyBeacons(GoogleApiClient client){

        //First, check required permissions

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

    }

    public void manageHeadphoneState(GoogleApiClient client){
        Awareness.SnapshotApi.getHeadphoneState(client)
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
}
