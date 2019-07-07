package com.example.waterfountainapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateFountain extends AppCompatActivity implements OnMapReadyCallback {

    ImageView ftnPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_fountain);
        // Take a picture, select location, set ratings on slider for taste, temp, press,
        // mark exact location, what floor, checkbox any special features, submit for review

        Button btnPic = findViewById(R.id.btnPic);
        ftnPic = findViewById(R.id.fountainPic);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onClickPhoto(View view) throws IOException {
        Intent picintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String picName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File picImage = File.createTempFile(picName, ".jpg", storageDir);
        Uri uri = FileProvider.getUriForFile(CreateFountain.this, BuildConfig.APPLICATION_ID + ".provider", picImage);
        picintent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        picintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(picintent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri image = data.getData();
        ftnPic.setImageURI(image);
    }

    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getBaseContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.addMarker(new MarkerOptions().position(new LatLng(40.4259, -86.9081)).title("Purdue").snippet("Home of the Boilermakers"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(40.4259, -86.9081)));
    }
}
