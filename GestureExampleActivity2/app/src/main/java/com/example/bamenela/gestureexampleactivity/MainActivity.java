package com.example.bamenela.gestureexampleactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
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
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
        //  Dans le cas où la permission n'est pas mise, on la demande en plus de son écriture dans le manifest
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Utilisation de mediastore (Application professeur)
        Cursor photoCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        assert photoCursor != null;
        photoCursor.moveToFirst();
        //tant que le curseur n'est pas a la fin, on continue.
        while (!photoCursor.isAfterLast()) {
            //Vérification des datas avec le logcat
            Log.d("VERIF", "---Verification Path--->" + photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            //La fonction marche
            Singleton.getInstance().listImageMemory.add(photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            //Pour renplir la liste, on continue avec le prochain
            photoCursor.moveToNext();
        }
        //On ferme le curseur.
        photoCursor.close();

        TouchExample view = new TouchExample(this);
        setContentView(view);

    }
}
