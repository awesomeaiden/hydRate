package com.example.hydRate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RatingBar;

public class RateFountain extends AppCompatActivity {

    RatingBar tasteRatingBar;
    RatingBar tempRatingBar;
    RatingBar pressRatingBar;
    CheckBox bottleFiller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_fountain);

        tasteRatingBar = findViewById(R.id.ratingBarTaste);
        tempRatingBar = findViewById(R.id.ratingBarTemperature);
        pressRatingBar = findViewById(R.id.ratingBarPressure);
    }

    public void onClickRate(View view) {
        // Get ratings
        float tasteRating = tasteRatingBar.getRating();
        float tempRating = tempRatingBar.getRating();
        float pressRating = pressRatingBar.getRating();

        // Get bottle filler yes/no
        boolean bottleFill = bottleFiller.isChecked();
    }
}
