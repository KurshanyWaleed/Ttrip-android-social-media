package com.dmwm.tunitrip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
   Button login;
   Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=(Button) findViewById(R.id.buttonL);
        register=(Button) findViewById(R.id.buttonR);


        register.setOnClickListener(v ->  {
            Intent i = new Intent(getApplicationContext(), ChosenUserTypeActivity.class);
            startActivity(i);
        });
        login.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),LoginActivity.class)));
    }
}