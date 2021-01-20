package com.dmwm.tunitrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class VisitProfile extends AppCompatActivity {
    FloatingActionButton floatingActionButtonVS;
    Button contactVS;
    TextView usernameVS,bioVS,regionVS,homeVS,genderVS,phoneVS;
    CircularImageView profileImageVS;
    ImageView coverImageVS,imageViewOnlineVS;
    ProgressDialog pd;

    String hisUid;
    String myUid;
    String hisImage;
    String email,uid;




    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_profile);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("Welcome to My Profile ! ");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        pd=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();



        floatingActionButtonVS=findViewById(R.id.floating_btnlikeVS);
        contactVS=findViewById(R.id.buttonSendMessageVS);
        usernameVS=findViewById(R.id.textViewUserVS);
        bioVS=findViewById(R.id.TextViewBioVS);
        regionVS=findViewById(R.id.textViewRegionVS);
        homeVS=findViewById(R.id.textViewHomeVS);
        genderVS=findViewById(R.id.genderVS);
        phoneVS=findViewById(R.id.textViewPhoneVS);
        profileImageVS=findViewById(R.id.imageVProfileVS);
        coverImageVS=findViewById(R.id.imageViewCoverVS);
        imageViewOnlineVS=findViewById(R.id.imageViewonline);


        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        storageReference  = FirebaseStorage.getInstance().getReference();



        Intent i=getIntent();
        hisUid=i.getStringExtra("hisUid");


        Query usersQuery =databaseReference.orderByChild("uid").equalTo(hisUid);
        // System.out.println(usersQuery+"555555555555555555555555555555555555555555555555555555555555555555555");
        pd.setTitle("Loading...!");
        pd.show();
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println(snapshot+"resulta is here                                          ");
                for(DataSnapshot ds : snapshot.getChildren()){
                    String name =""+ds.child("name").getValue();
                    String bio =""+ds.child("bio").getValue();
                    String home =""+ds.child("home").getValue();
                    String region =""+ds.child("region").getValue();
                    String phone =""+ds.child("phone").getValue();
                    hisImage =""+ds.child("photoProfile").getValue();
                    String onlineStatus = ""+ds.child("onlineStatus").getValue();
                    // String newOnligneStatus=onlineStatus.substring(0,12);


                    if (onlineStatus.equals("online")){
                        imageViewOnlineVS.setVisibility(View.VISIBLE);


                    }else{

                        imageViewOnlineVS.setVisibility(View.GONE);
                    }
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.userdefault).into(profileImageVS);
                    }catch (Exception e ){
                        Picasso.get().load(R.drawable.userdefault).into(coverImageVS);
                    }
                    usernameVS.setText(name);
                    bioVS.setText(bio);
                    homeVS.setText("Home :"+home);
                    regionVS.setText("Region :"+region);
                    phoneVS.setText("Phone :"+phone);
                    if (onlineStatus.equals("online")){
                        imageViewOnlineVS.setVisibility(View.VISIBLE);

                    }else
                        imageViewOnlineVS.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
              contactVS.setOnClickListener(new View.OnClickListener() {
                      @Override
                       public void onClick(View v) {
                      Intent i=new Intent(getApplicationContext(),Chat_Activity.class);
                            i.putExtra("hisUid",hisUid);
                               startActivity(i);
                                    System.out.println(hisUid+"  gggggggggggggggggggggggggggggggggggggggggggggggggg");

    }
});
        pd.dismiss();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }


    private  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            // textViewProfile.setText(user.getEmail());
            email=user.getEmail();
            uid=user.getUid();

        }else
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.add_post_action).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

}