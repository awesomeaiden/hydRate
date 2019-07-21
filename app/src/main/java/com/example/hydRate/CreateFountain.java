package com.example.hydRate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class CreateFountain extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView ftnPic;
    private Uri picUri;
    String picPath;
    Bitmap ftnPicBitmap;
    private int markerStatus;
    public LatLng markerLocation;
    public boolean picStatus;
    static final int REQUEST_CODE = 100;

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

    public void onClickTakePicture(View view) {
        dispatchTakePictureIntent();
    }

    public void onClickCreate(View view) {
        if ((markerLocation != null) && (picStatus == true)) {
            // Pop up dialog and ask if the user would like to rate the fountain they just added or not
            askRate();
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

    public void askRate() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent goToRate = new Intent(CreateFountain.this, RateFountain.class);
                        startActivity(goToRate);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Intent goToFountains = new Intent(CreateFountain.this, MainActivity.class);
                        startActivity(goToFountains);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Would you like to add a rating for this new fountain?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Not yet", dialogClickListener).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // Confirm that file exists
            File ftnPicFile = new File(picPath);
            if (ftnPicFile.exists()) {
                // Create bitmap from file
                ftnPicBitmap = BitmapFactory.decodeFile(picPath);
            }
            // Set max dimensions of the photo based on screen size
            Display display = getWindowManager().getDefaultDisplay();
            ftnPic.setMaxHeight((int) (display.getHeight() / 3.75));
            ftnPic.setMaxWidth((int) (display.getWidth() / 1.1));
            // Set image bitmap
            ftnPic.setImageBitmap(ftnPicBitmap);
            // Correct for orientation of photo
            correctImageOrientation(ftnPicFile);
            picStatus = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void correctImageOrientation(File picFile) throws IOException {
        try {
            ExifInterface exif = new ExifInterface(picFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            if (orientation != 1) {
                if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {ftnPic.setRotation(180);}
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {ftnPic.setRotation(90);}
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {ftnPic.setRotation(270);}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        picPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error occured while creating the image file", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CODE);
            }
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
