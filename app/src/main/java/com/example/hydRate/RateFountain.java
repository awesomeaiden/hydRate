package com.example.hydRate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;

import com.google.firebase.database.DatabaseReference;

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
    }

    public void onClickRate(View view) {
        // Get ratings
        float tasteRating = tasteRatingBar.getRating();
        float tempRating = tempRatingBar.getRating();
        float pressRating = pressRatingBar.getRating();

        // Get bottle filler yes/no
        boolean bottleFill = bottleFiller.isChecked();

        ratingID = "rating_" + fountainID + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        // Add ratings data to fountain being rated, using fountain ID collected from intent
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("taste").setValue(tasteRating);
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("temp").setValue(tempRating);
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("press").setValue(pressRating);
        database.child("fountains").child(fountainID).child("ratings").child(ratingID).child("bottle").setValue(bottleFill);
    }
}
