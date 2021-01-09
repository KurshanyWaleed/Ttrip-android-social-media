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

public class RegistrationTouristActivity extends AppCompatActivity {
    EditText mEmail,mPassword,mCPassword;
    Button buttonOK;
    ProgressDialog progressDialog;
    private  FirebaseAuth mAuth;
    TextView mAlready;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_tourist);
        mAuth= FirebaseAuth.getInstance();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        mPassword=findViewById(R.id.editTextTextPasswordR);
        mEmail=findViewById(R.id.editTextTextEmailAddressR);
        mAlready=findViewById(R.id.TitleR);
        buttonOK=findViewById(R.id.buttonOKR);
        mCPassword=findViewById(R.id.editTextNumberPassword2T);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registration User ...");

        mAlready.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        buttonOK.setOnClickListener(v -> {
            String mail=mEmail.getText().toString().trim();
            String pass=mPassword.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches())
            {
                mEmail.setError("Invalid Email");
                mEmail.setFocusable(true);
            }
            else if (pass.length()<6)
            {
                mEmail.setError("Invalid password Password length at least 6 characters ! ");
                mEmail.setFocusable(true);
            }
            else {
                registerUser(mail,pass);
            }
        });
    }

    private void registerUser(String email, String pass) {
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
                        hashMap.put("name","");
                        hashMap.put("phone","");
                        hashMap.put("home","");
                        hashMap.put("region","");
                        hashMap.put("gender",""); //will add later
                        hashMap.put("onlineStatus","online");
                        hashMap.put("typingTo","noBody");
                        hashMap.put("photoProfile","");
                        hashMap.put("photoCover","");
                        hashMap.put("type","Tourist");
                        hashMap.put("bio","");

                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=database.getReference("Users");
                        System.out.println(databaseReference+"gggggggggggggggggggggggggggggggggggggggggggggggggggggg");
                        databaseReference.child(uid).setValue(hashMap);


                        Toast.makeText(getApplicationContext(), "Registered\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), TouristMainActivity.class));
                        finish();
                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), " Authentification Failed ..! ", Toast.LENGTH_SHORT).show();
            }
        });
    }
@Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}