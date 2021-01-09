package com.dmwm.tunitrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dmwm.tunitrip.Tourist.TouristMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationGuideActivity extends AppCompatActivity {
       Button buttonDone;
       EditText mName,mEmail,mPassword,mPasswordConfirm;
       TextView mAlready;

    private FirebaseAuth mAuth;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registratuin_guide);

        mAuth= FirebaseAuth.getInstance();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registration User ...");

        buttonDone=(Button)findViewById(R.id.buttonDoneG);
        mName=(EditText)findViewById(R.id.editTextTextPersonNameG);
        mEmail=(EditText)findViewById(R.id.editTextTextEmailAddressG);
        mPassword=(EditText)findViewById(R.id.editTextNumberPasswordG);
        mPasswordConfirm=(EditText)findViewById(R.id.editTextNumberPassword2G);
        mAlready=findViewById(R.id.TitleG);



        mAlready.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));




        buttonDone.setOnClickListener(v ->
        {
            String email = mEmail.getText().toString().trim();
            String pass = mPassword.getText().toString().trim();
            String name = mName.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmail.setError("Invalid Email");
                mEmail.setFocusable(true);
            } else if (pass.length() < 6) {
                mEmail.setError("Invalid password Password length at least 6 characters ! ");
                mEmail.setFocusable(true);
            } else {
                registerUser(email, pass,name);
            }

        });


    }

    private void registerUser(String email, String pass,String name) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        progressDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();
                        String mail=user.getEmail();
                        String uid=user.getUid();
                        HashMap<Object,String> hashMap=new HashMap<>();
                        hashMap.put("uid",uid);
                        hashMap.put("email",mail);
                        hashMap.put("name",name);
                        hashMap.put("phone","");
                        hashMap.put("home","");
                        hashMap.put("region","");
                        hashMap.put("gender",""); //will add later
                        hashMap.put("onlineStatus","online");
                        hashMap.put("typingTo","noBody");
                        hashMap.put("photoProfile","");
                        hashMap.put("photoCover","");
                        hashMap.put("type","Guide");
                        hashMap.put("bio","");

                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=database.getReference("Users");
                        System.out.println(databaseReference+"gggggggggggggggggggggggggggggggggggggggggggggggggggggg");
                        databaseReference.child(uid).setValue(hashMap);


                        Toast.makeText(getApplicationContext(), "Registered\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), TouristMainActivity.class));
                        finish();
                    }


                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), " Authentication Failed ..! This Email is Already used !  ", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}