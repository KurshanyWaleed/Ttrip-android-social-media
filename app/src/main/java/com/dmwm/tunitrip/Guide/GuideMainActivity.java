package com.dmwm.tunitrip.Guide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.dmwm.tunitrip.MainActivity;
import com.dmwm.tunitrip.R;
import com.dmwm.tunitrip.Tourist.Feed_Tourist_Fragment;
import com.dmwm.tunitrip.Tourist.Followers_Tourist_Fragment;
import com.dmwm.tunitrip.Tourist.Messagerie_Tourist_Fragment;
import com.dmwm.tunitrip.Tourist.Profile_Tourist_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GuideMainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);


        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        // textViewProfile=findViewById(R.id.textViewProfile);
        BottomNavigationView navigationView = findViewById(R.id.nav_bar_guide);

        firebaseAuth = FirebaseAuth.getInstance();


        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.containerG, new Guide_profile_Fragment()).commit();
        navigationView.setSelectedItemId(R.id.feedG);
    }
        private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                switch (item.getItemId()){

                    case R.id.profileG:
                        actionBar.setTitle("Profile");
                        fragment=new Guide_profile_Fragment();
                        break;
                    case R.id.messagesG:
                        actionBar.setTitle("Messenger");
                        fragment=new Guide_messagerie_Fragment();
                        break;
                    case R.id.feedG:
                        actionBar.setTitle("Feed");
                        fragment=new Guide_feed_Fragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.containerG,fragment).commit();
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