package com.dmwm.tunitrip.Tourist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.dmwm.tunitrip.Add_Post_Activity;
import com.dmwm.tunitrip.MainActivity;
import com.dmwm.tunitrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Followers_Tourist_Fragment extends Fragment {
FirebaseAuth firebaseAuth;


    public Followers_Tourist_Fragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_followers_tourist, container, false);

        return  view;
    }
    private  void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            // textViewProfile.setText(user.getEmail());

        }else
        {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.menu,menu);
        menu.findItem(R.id.add_post_action).setVisible(false);
        //SearchView
      firebaseAuth=FirebaseAuth.getInstance();

        super.onCreateOptionsMenu(menu,inflater);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
            if (id == R.id.add_post_action){
                startActivity(new Intent(getActivity(), Add_Post_Activity.class));
            }

        }
        return super.onOptionsItemSelected(item);
    }

}