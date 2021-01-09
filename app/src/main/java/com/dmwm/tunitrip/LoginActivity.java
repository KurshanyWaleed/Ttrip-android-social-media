package com.dmwm.tunitrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dmwm.tunitrip.Guide.GuideMainActivity;
import com.dmwm.tunitrip.Tourist.TouristMainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignIntentClient;
    EditText mEmail,mPassword;

      TextView textViewForgotPass ,DontHaveAcound;
      Button buttonLogin ;
      SignInButton buttonGoogle;
       FirebaseAuth firebaseAuth;
      ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        GoogleSignInOptions gsp=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
     mGoogleSignIntentClient=GoogleSignIn.getClient(getApplicationContext(),gsp);

        mEmail=(EditText) findViewById(R.id.editTextTextEmailAddressL);
        mPassword=(EditText)findViewById(R.id.editTextTextPasswordL);
        buttonLogin=(Button) findViewById(R.id.buttonOKL);
        DontHaveAcound=(TextView)findViewById(R.id.textViewDontHAcc);
        buttonGoogle=(SignInButton) findViewById(R.id.buttonGoogle);
        textViewForgotPass=(TextView)findViewById(R.id.textViewForgettingPassword);
        progressDialog=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();

        buttonGoogle.setOnClickListener(v -> {
             Intent intentSignInGoogle=mGoogleSignIntentClient.getSignInIntent();
             startActivityForResult(intentSignInGoogle,RC_SIGN_IN);


        });


        DontHaveAcound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationTouristActivity.class));
                finish();
            }
        });



      textViewForgotPass.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              showRecoverPasswordDialog();
          }
      });
        buttonLogin.setOnClickListener(v -> {

            String Email =mEmail.getText().toString().trim();
            String pass=mPassword.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
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
                LoginUser(Email,pass);
            }
        });

    }


    private void showRecoverPasswordDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText email = new EditText(this);
        email.setHint("Enter your Email Please ! ");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
       email.setMaxEms(16);

        linearLayout.addView(email);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail =email.getText().toString().trim();
                beginRecovery(mail);
            }
        });
        builder.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            }
        });
builder.create().show();



    }

    private void beginRecovery(String email) {
        System.out.println(email+"heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        progressDialog.setMessage("sending Email verification .. ");
        progressDialog.show();
         firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 progressDialog.dismiss();
                 if (task.isSuccessful()) {
                     Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();
                 } else {
                     Toast.makeText(getApplicationContext(), "Failed ...! check your Internet connexion ", Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 progressDialog.dismiss();
                 Toast.makeText(getApplicationContext(), "wrong Email or "+e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });
    }

    private void LoginUser(String email, String pass) {
        progressDialog.setMessage("Login ....");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        progressDialog.dismiss();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(), "Welcome ! ", Toast.LENGTH_SHORT).show();
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        System.out.println(reference);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String type = null;
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    type = "" + snapshot.child("type").getValue();
                                    System.out.println(type + "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTType");
                                }
                                    if (type.equals("Tourist")) {

                                        startActivity(new Intent(getApplicationContext(), TouristMainActivity.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(getApplicationContext(), GuideMainActivity.class));
                                        finish();
                                    }
                                }
                           
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.

                    }


                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), " Authentication Failed ..! \n this Email Address is already used !! ", Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(RC_SIGN_IN + "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            System.out.println(data + "hhhhhhhhhhh");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            System.out.println(task + "hhhhhhhhhhh");

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Signed in successfully, show authenticated UI.
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "helooooo", Toast.LENGTH_SHORT).show();
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                //}
            }

        }

   /* private void handel(Task<GoogleSignInAccount> Completedtask) {
        try {
            GoogleSignInAccount account = Completedtask.getResult(ApiException.class);
            Toast.makeText(this, "barvoooo", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(account);
        }catch (ApiException e ){
            Toast.makeText(this, "not baravoooooo", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(null);
        }
   }

    */
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acco) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acco.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=firebaseAuth.getCurrentUser();

                    startActivity(new Intent(getApplicationContext(), TouristMainActivity.class));


                }else {
                    Toast.makeText(LoginActivity.this, "Login Failed ...", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}