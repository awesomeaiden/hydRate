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
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_fountain);

        tasteRatingBar = findViewById(R.id.ratingBarTaste);
        tempRatingBar = findViewById(R.id.ratingBarTemperature);
        pressRatingBar = findViewById(R.id.ratingBarPressure);
        bottleFiller = findViewById(R.id.bottleFillCheckBox);

        database = FirebaseDatabase.getInstance().getReference();
    }

    public void onClickRate(View view) {
        // Initalize rating object
        Rating rating = new Rating();
        rating.setFountainID(getIntent().getStringExtra("fountainID"));

        // Get ratings
        rating.setTasteRating(tasteRatingBar.getRating());
        rating.setTempRating(tempRatingBar.getRating());
        rating.setPressRating(pressRatingBar.getRating());

        // Get bottle filler yes/no
        rating.setBottleFill(bottleFiller.isChecked());

        // Create unique ratingID
        rating.setRatingID("rating_" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()));

        // Add ratings data to fountain being rated, using fountain ID collected from intent
        database.child("fountains").child(rating.getFountainID()).child("ratings").child(rating.ratingID).child("taste").setValue(rating.getTasteRating());
        database.child("fountains").child(rating.getFountainID()).child("ratings").child(rating.ratingID).child("temp").setValue(rating.getTempRating());
        database.child("fountains").child(rating.getFountainID()).child("ratings").child(rating.ratingID).child("press").setValue(rating.getPressRating());
        database.child("fountains").child(rating.getFountainID()).child("ratings").child(rating.ratingID).child("bottle").setValue(rating.isBottleFill());

        Toast.makeText(RateFountain.this, "Rating added!", Toast.LENGTH_SHORT).show();
        Intent goHome = new Intent(RateFountain.this, MainActivity.class);
        startActivity(goHome);
    }
}
