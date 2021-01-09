package com.dmwm.tunitrip.Tourist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.dmwm.tunitrip.MainActivity;
import com.dmwm.tunitrip.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TouristMainActivity extends AppCompatActivity {
            FirebaseAuth firebaseAuth;
           // TextView textViewProfile;
               ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tourist);
         actionBar=getSupportActionBar();
        actionBar.setTitle("Profile");

       // textViewProfile=findViewById(R.id.textViewProfile);
        BottomNavigationView navigationView=findViewById(R.id.nav_bar_tourist);

        firebaseAuth=FirebaseAuth.getInstance();


        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.containerT,new Feed_Tourist_Fragment()).commit();
        navigationView.setSelectedItemId(R.id.feedT);


        /*Feed_Tourist_Fragment feed_tourist_fragment= new Feed_Tourist_Fragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contaierG,feed_tourist_fragment,"");
        fragmentTransaction.commit();*/




    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment=null;
            switch (item.getItemId()){

                case R.id.profileT:
                    actionBar.setTitle("Profile");
                    fragment=new Profile_Tourist_Fragment();
                    break;
                case R.id.messagesT:
                    actionBar.setTitle("Messenger");
                    fragment=new Messagerie_Tourist_Fragment();
                    break;
                case R.id.followersT:
                    actionBar.setTitle("Following");
                    fragment=new Followers_Tourist_Fragment();
                    break;
                case R.id.feedT:
                    actionBar.setTitle("Feed");
                    fragment=new Feed_Tourist_Fragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.containerT,fragment).commit();
            return true;

           /* switch (item.getItemId())
            {
                case R.id.feedT:
                    actionBar.setTitle("Feed");
                    Feed_Tourist_Fragment feed_tourist_fragment = new Feed_Tourist_Fragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.contaierG, feed_tourist_fragment, "");
                    fragmentTransaction.commit();
                    return true;
                //------------------------------
                case R.id.profileT:
                    //------------------------------
                    actionBar.setTitle("Profile");
                    Feed_Tourist_Fragment Profile_tourist_fragment = new Feed_Tourist_Fragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.contaierG, Profile_tourist_fragment, "");
                    fragmentTransaction2.commit();
                    return true;

                case R.id.messagesT:
                    //------------------------------
                    actionBar.setTitle("Messages");
                    Feed_Tourist_Fragment Messageries_tourist_fragment = new Feed_Tourist_Fragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.contaierG, Messageries_tourist_fragment, "");
                    fragmentTransaction3.commit();
                    return true;
                case R.id.followers:
                    //-------------------------------
                    actionBar.setTitle("Followers");
                    Feed_Tourist_Fragment Followers_tourist_fragment = new Feed_Tourist_Fragment();
                    FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.contaierG, Followers_tourist_fragment, "");
                    fragmentTransaction4.commit();
                    return true;


            }*/

        }

    };

    private  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
           // textViewProfile.setText(user.getEmail());

        }else
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }




}