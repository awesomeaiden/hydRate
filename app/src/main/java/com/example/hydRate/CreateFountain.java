package com.example.hydRate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class CreateFountain extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView ftnPic;
    private Uri picUri;
    private int markerStatus;
    public LatLng markerLocation;
    public boolean picStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_fountain);
        // Take a picture, select location, set ratings on slider for taste, temp, press,
        // mark exact location, what floor, checkbox any special features, submit for review

        ftnPic = findViewById(R.id.fountainPic);
        picStatus = false;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    public void onClickPhoto(View view) throws IOException {
        Intent picintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        @SuppressLint("SimpleDateFormat") String picName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        else {
            File picImage = File.createTempFile(picName, ".jpg", storageDir);
            Uri uri = FileProvider.getUriForFile(CreateFountain.this, BuildConfig.APPLICATION_ID + ".provider", picImage);
            picintent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            picintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            picUri = uri;
            int REQUEST_CODE = 100;
            startActivityForResult(picintent, REQUEST_CODE);
        }
    }

    public void onClickContinue(View view) {
        if ((markerLocation != null) && (picStatus == true)) {
            setContentView(R.layout.activity_create_fountain_rate);
        }
        else if ((markerLocation == null) && (picStatus == false)) {
            Toast.makeText(this, "Please take a picture of the fountain and set its location", Toast.LENGTH_LONG).show();
        }
        else if (markerLocation == null) {
            Toast.makeText(this, "Please set fountain location", Toast.LENGTH_LONG).show();
        }
        else if (picStatus == false) {
            Toast.makeText(this, "Please take a picture of the fountain", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            InputStream ftnInputStream = getContentResolver().openInputStream(picUri);
            ExifInterface exif = new ExifInterface(ftnInputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Display display = getWindowManager().getDefaultDisplay();
            ftnPic.setMaxHeight((int) (display.getHeight() / 3.75));
            ftnPic.setMaxWidth((int) (display.getWidth() / 1.1));
            ftnPic.setImageURI(picUri);
            if (orientation != 1) {
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {ftnPic.setRotation(180);}
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {ftnPic.setRotation(90);}
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {ftnPic.setRotation(270);}
            }
            picStatus = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(getBaseContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(40.4259, -86.9081), 18, 0, 0)));

        markerStatus = 0;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (markerStatus == 0) {
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Add foutain").snippet("Add a fountain at this location"));
                    markerLocation = latLng;
                    markerStatus = 1;
                }
                else {
                    googleMap.clear();
                    markerStatus = 0;
                    markerLocation = null;
                }
            }
        });
    }
}
