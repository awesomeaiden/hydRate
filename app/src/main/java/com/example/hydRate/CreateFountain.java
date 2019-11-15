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
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class CreateFountain extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView ftnPic;
    private Bitmap ftnPicBitmap;
    private String picPath;
    private int markerStatus;
    private LatLng markerLocation;
    private boolean picStatus;
    private static final int REQUEST_CODE = 100;
    private DatabaseReference database;
    private StorageReference storage;

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

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void onClickTakePicture(View view) {
        dispatchTakePictureIntent();
    }

    public void onClickCreate(View view) {
        // Add fountain identifiers to database if picture and location are present
        if ((markerLocation != null) && (picStatus)) {
            // Create new fountain object
            Fountain ftn = new Fountain();
            // Create unique fountainID seeded by markerLocation
            ftn.setFtnID("ftn_" + markerLocation);
            // Remove problematic characters from ftnID
            ftn.setFtnID(ftn.getFtnID().replace(".", "_").replace(" ", "_").replace("/", "_"));
            // Create picID seeded by name of picture
            ftn.setPicID("ftnPics/" + ftn.getFtnID() + "/" + picPath.split("/")[picPath.split("/").length - 1]);
            // Add fountain to database with location and picture reference
            database.child("fountains").child(ftn.getFtnID()).child("location").setValue(markerLocation);
            database.child("fountains").child(ftn.getFtnID()).child("picture").setValue(ftn.getPicID());

            // Upload picture to database with picPath as unique identifier
            Uri ftnPicFile = Uri.fromFile(new File(picPath));
            storage.child(ftn.getPicID()).putFile(ftnPicFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CreateFountain.this, "Fountain successfully added!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CreateFountain.this, "Failed to add fountain, please try again", Toast.LENGTH_LONG).show();
                }
            });

            // Pop up dialog and ask if the user would like to rate the fountain they just added or not
            askRate(ftn.getFtnID());
        }
        else if ((markerLocation == null) && (!picStatus)) {
            Toast.makeText(this, "Please take a picture of the fountain and set its location", Toast.LENGTH_LONG).show();
        }
        else if (markerLocation == null) {
            Toast.makeText(this, "Please set fountain location", Toast.LENGTH_LONG).show();
        }
        else if (!picStatus) {
            Toast.makeText(this, "Please take a picture of the fountain", Toast.LENGTH_LONG).show();
        }
    }

    private void askRate(final String fountainID) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent goToRate = new Intent(CreateFountain.this, RateFountain.class);
                        goToRate.putExtra("fountainID", fountainID);
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

    private void correctImageOrientation(File picFile) {
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
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
