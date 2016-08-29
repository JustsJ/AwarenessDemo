package com.eptron.awernessdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button mSnapshotButton=(Button) findViewById(R.id.snapshot_button);
        Button mFenceButton=(Button) findViewById(R.id.fence_button);

        mSnapshotButton.setOnClickListener(this);
        mFenceButton.setOnClickListener(this);

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        client.connect();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.snapshot_button){
            Intent i=new Intent(this, SnapshotActivity.class);
            startActivity(i);

        }
        else if (view.getId()==R.id.fence_button){
            Intent i=new Intent(this,FenceActivity.class);
            startActivity(i);
        }
    }
}
