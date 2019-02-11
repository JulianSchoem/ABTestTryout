package com.example.julianschoemaker.abtesttryout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    private EditText editText;
    private Button button;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                textView.setText("Instance ID:" + deviceToken);
                // Do whatever you want with your token now
                // i.e. store it on SharedPreferences or DB
                // or directly send it to server
            }
        });


        // Remote Config anfragen
        remoteConfig.setConfigSettings(
                new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build()
        );

        // Max characters in editText on default 5 but from Remote Config changed to other value
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("overlay_max_chars", 5);
        defaults.put("button_primary_color", "#552419");
        remoteConfig.setDefaults(defaults);

        final Task<Void> fetch = remoteConfig.fetch(FirebaseRemoteConfig.VALUE_SOURCE_STATIC);
        fetch.addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                remoteConfig.activateFetched();
                updateMaxTextLength();

                String buttonColor = remoteConfig.getString("button_primary_color");

                button.setBackgroundColor(Color.parseColor(buttonColor));

            }
        });
    }

    // Setzt die Max characters auf Wert aus Remote Config
    private void updateMaxTextLength() {
        int max = (int) remoteConfig.getLong("overlay_max_chars");
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(max)});
    }
}
