package com.dmwm.tunitrip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChosenUserTypeActivity extends AppCompatActivity {
    Button guide,tourist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_user_type);
        guide = findViewById(R.id.buttonImGuide);
        tourist = findViewById(R.id.buttonImTourist);



        guide.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RegistrationGuideActivity.class));

        });
        tourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegistrationTouristActivity.class));
            }
        });

    }
}