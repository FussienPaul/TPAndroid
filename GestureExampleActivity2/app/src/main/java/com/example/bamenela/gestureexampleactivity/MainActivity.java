package com.example.bamenela.gestureexampleactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pour eviter le crash de l'application, on check les permissions. Dans ce cas, READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("STORAGE", "LA PERMISSIONS N'EST PAS DONNEE");
        //  Dans le cas où la permission n'est pas mise, on la demande en plus de son écriture dans le manifest
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            Log.d("DEMAND STORAGE", "LA PERMISSIONS A ETE DEMANDEE");
        }

        TouchExample view = new TouchExample(this);
        setContentView(view);

    }
}
