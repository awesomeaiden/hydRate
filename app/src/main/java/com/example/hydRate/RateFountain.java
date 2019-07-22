package com.example.hydRate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RateFountain extends AppCompatActivity {

    RatingBar tasteRatingBar;
    RatingBar tempRatingBar;
    RatingBar pressRatingBar;
    CheckBox bottleFiller;
    String fountainID;
    String ratingID;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_fountain);

        tasteRatingBar = findViewById(R.id.ratingBarTaste);
        tempRatingBar = findViewById(R.id.ratingBarTemperature);
        pressRatingBar = findViewById(R.id.ratingBarPressure);
        bottleFiller = findViewById(R.id.bottleFillCheckBox);

        // Get fountainID from intent
        fountainID = getIntent().getStringExtra("fountainID");

        database = FirebaseDatabase.getInstance().getReference();
    }

    public void onClickRate(View view) {
        // Get ratings
        float tasteRating = tasteRatingBar.getRating();
        float tempRating = tempRatingBar.getRating();
        float pressRating = pressRatingBar.getRating();

        // Get bottle filler yes/no
        boolean bottleFill = bottleFiller.isChecked();

        // Create unique ratingID
        ratingID = "rating_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        // Add ratings data to fountain being rated, using fountain ID collected from intent
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("taste").setValue(tasteRating);
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("temp").setValue(tempRating);
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("press").setValue(pressRating);
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("bottle").setValue(bottleFill);

        Toast.makeText(RateFountain.this, "Rating added!", Toast.LENGTH_SHORT).show();
        Intent goHome = new Intent(RateFountain.this, MainActivity.class);
        startActivity(goHome);
    }
}
